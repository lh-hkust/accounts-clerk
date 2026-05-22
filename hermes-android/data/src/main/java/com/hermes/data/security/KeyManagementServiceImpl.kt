package com.hermes.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.hermes.domain.service.KeyManagementService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 密钥管理服务实现
 * 使用Android Keystore存储KEK，实现DEK+KEK信封加密
 *
 * @see data-security.md 二、数据库加密方案
 */
@Singleton
class KeyManagementServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : KeyManagementService {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEK_ALIAS = "hermes_kek_key"
        private const val KEY_STORAGE_PREFS = "hermes_key_storage"

        // PBKDF2参数
        private const val PBKDF2_ITERATIONS = 100_000
        private const val PBKDF2_SALT_SIZE = 16
        private const val KEY_SIZE = 256 // 256位

        // AES-GCM参数
        private const val GCM_IV_SIZE = 12
        private const val GCM_TAG_SIZE = 128

        // 存储键名
        private const val PREF_DEK_ENCRYPTED = "encrypted_dek"
        private const val PREF_DEK_IV = "dek_iv"
        private const val PREF_KEK_SALT = "kek_salt"
        private const val PREF_HAS_PASSWORD = "has_password_protection"
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    private val sharedPrefs by lazy {
        context.getSharedPreferences(KEY_STORAGE_PREFS, Context.MODE_PRIVATE)
    }

    override fun isKeyInitialized(): Boolean {
        return sharedPrefs.contains(PREF_DEK_ENCRYPTED)
    }

    override fun hasPassword(): Boolean {
        return sharedPrefs.getBoolean(PREF_HAS_PASSWORD, false)
    }

    override fun initializeKey(password: String?): ByteArray {
        // 生成DEK（数据加密密钥）
        val dek = generateDEK()

        // 根据是否有密码，决定存储方式
        if (password != null) {
            // 有密码模式：使用PBKDF2派生KEK，然后加密DEK
            val salt = generateSalt()
            val kek = deriveKEKFromPassword(password, salt)

            // 加密DEK
            val iv = generateIV()
            val encryptedDek = encryptDEK(dek, kek, iv)

            // 存储
            sharedPrefs.edit()
                .putString(PREF_DEK_ENCRYPTED, bytesToBase64(encryptedDek))
                .putString(PREF_DEK_IV, bytesToBase64(iv))
                .putString(PREF_KEK_SALT, bytesToBase64(salt))
                .putBoolean(PREF_HAS_PASSWORD, true)
                .apply()
        } else {
            // 无密码模式：直接使用Android Keystore的KEK加密DEK
            ensureKeystoreKeyExists()
            val kek = getKeystoreKey()
            val iv = generateIV()
            val encryptedDek = encryptDEK(dek, kek, iv)

            // 存储
            sharedPrefs.edit()
                .putString(PREF_DEK_ENCRYPTED, bytesToBase64(encryptedDek))
                .putString(PREF_DEK_IV, bytesToBase64(iv))
                .putBoolean(PREF_HAS_PASSWORD, false)
                .apply()
        }

        return dek
    }

    override fun getDatabaseKey(password: String?): ByteArray {
        if (!isKeyInitialized()) {
            throw SecurityException("Key not initialized")
        }

        val encryptedDek = base64ToBytes(sharedPrefs.getString(PREF_DEK_ENCRYPTED, null)!!)
        val iv = base64ToBytes(sharedPrefs.getString(PREF_DEK_IV, null)!!)

        if (hasPassword()) {
            if (password == null) {
                throw SecurityException("Password required")
            }
            val salt = base64ToBytes(sharedPrefs.getString(PREF_KEK_SALT, null)!!)
            val kek = deriveKEKFromPassword(password, salt)
            return decryptDEK(encryptedDek, kek, iv)
        } else {
            // 无密码模式：使用Keystore的KEK
            ensureKeystoreKeyExists()
            val kek = getKeystoreKey()
            return decryptDEK(encryptedDek, kek, iv)
        }
    }

    override fun updatePassword(currentPassword: String?, newPassword: String?) {
        if (!isKeyInitialized()) {
            throw SecurityException("Key not initialized")
        }

        // 验证当前密码
        val dek = getDatabaseKey(currentPassword)

        // 使用新密码重新加密DEK
        if (newPassword != null) {
            val salt = generateSalt()
            val kek = deriveKEKFromPassword(newPassword, salt)
            val iv = generateIV()
            val encryptedDek = encryptDEK(dek, kek, iv)

            sharedPrefs.edit()
                .putString(PREF_DEK_ENCRYPTED, bytesToBase64(encryptedDek))
                .putString(PREF_DEK_IV, bytesToBase64(iv))
                .putString(PREF_KEK_SALT, bytesToBase64(salt))
                .putBoolean(PREF_HAS_PASSWORD, true)
                .apply()
        } else {
            // 移除密码保护
            ensureKeystoreKeyExists()
            val kek = getKeystoreKey()
            val iv = generateIV()
            val encryptedDek = encryptDEK(dek, kek, iv)

            sharedPrefs.edit()
                .putString(PREF_DEK_ENCRYPTED, bytesToBase64(encryptedDek))
                .putString(PREF_DEK_IV, bytesToBase64(iv))
                .remove(PREF_KEK_SALT)
                .putBoolean(PREF_HAS_PASSWORD, false)
                .apply()
        }
    }

    override fun verifyPassword(password: String?): Boolean {
        if (!hasPassword()) {
            return password == null
        }

        try {
            getDatabaseKey(password)
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    override fun clearKeys() {
        // 清除SharedPreferences中的密钥
        sharedPrefs.edit().clear().apply()

        // 尝试删除Keystore中的密钥
        try {
            if (keyStore.containsAlias(KEK_ALIAS)) {
                keyStore.deleteEntry(KEK_ALIAS)
            }
        } catch (e: Exception) {
            // 忽略删除失败
        }
    }

    // ========== 私有方法 ==========

    private fun generateDEK(): ByteArray {
        val secureRandom = SecureRandom()
        val dek = ByteArray(KEY_SIZE / 8)
        secureRandom.nextBytes(dek)
        return dek
    }

    private fun generateSalt(): ByteArray {
        val secureRandom = SecureRandom()
        val salt = ByteArray(PBKDF2_SALT_SIZE)
        secureRandom.nextBytes(salt)
        return salt
    }

    private fun generateIV(): ByteArray {
        val secureRandom = SecureRandom()
        val iv = ByteArray(GCM_IV_SIZE)
        secureRandom.nextBytes(iv)
        return iv
    }

    private fun deriveKEKFromPassword(password: String, salt: ByteArray): ByteArray {
        // PBKDF2-HMAC-SHA256派生密钥
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = javax.crypto.spec.PBEKeySpec(
            password.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            KEY_SIZE
        )
        return factory.generateSecret(spec).encoded
    }

    private fun ensureKeystoreKeyExists() {
        if (!keyStore.containsAlias(KEK_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEK_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE)
                .setRandomizedEncryptionRequired(false) // 允许自定义IV
                .build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    private fun getKeystoreKey(): SecretKey {
        return (keyStore.getEntry(KEK_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun encryptDEK(dek: ByteArray, kek: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(kek, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        return cipher.doFinal(dek)
    }

    private fun encryptDEK(dek: ByteArray, kek: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
        cipher.init(Cipher.ENCRYPT_MODE, kek, spec)
        return cipher.doFinal(dek)
    }

    private fun decryptDEK(encryptedDek: ByteArray, kek: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(kek, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedDek)
    }

    private fun decryptDEK(encryptedDek: ByteArray, kek: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, kek, spec)
        return cipher.doFinal(encryptedDek)
    }

    private fun bytesToBase64(bytes: ByteArray): String {
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    private fun base64ToBytes(base64: String): ByteArray {
        return android.util.Base64.decode(base64, android.util.Base64.NO_WRAP)
    }
}