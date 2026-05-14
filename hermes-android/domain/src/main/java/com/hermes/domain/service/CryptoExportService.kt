package com.hermes.domain.service

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密导出服务
 * 使用 AES-256-GCM 加密算法
 *
 * @see data-security.md 三、导出文件加密方案
 */
class CryptoExportService {

    companion object {
        private const val AES_KEY_SIZE = 256 // 256位密钥
        private const val GCM_IV_SIZE = 12 // 12字节IV
        private const val GCM_TAG_SIZE = 128 // 128位认证标签

        // 固定盐值（用于无密码模式的HKDF）
        private const val HKDF_FIXED_SALT = "Hermes_Export_Fixed_Salt_v1"
        private const val HKDF_INFO = "account_export"

        // PBKDF2参数
        private const val PBKDF2_ITERATIONS = 100_000
        private const val PBKDF2_SALT_SIZE = 16

        // AES-GCM算法名称
        private const val AES_GCM_ALGORITHM = "AES/GCM/NoPadding"
    }

    /**
     * 使用密码派生密钥（PBKDF2模式）
     *
     * @param password 用户密码
     * @param salt 随机盐值（16字节）
     * @param appSignature 应用签名SHA256
     * @return 32字节AES密钥
     */
    fun deriveKeyFromPassword(password: String, salt: ByteArray, appSignature: ByteArray): ByteArray {
        // 密码 + 应用签名拼接作为输入
        val combinedInput = password.toByteArray(Charsets.UTF_8) + appSignature

        // PBKDF2-HMAC-SHA256派生
        return pbkdf2Derive(combinedInput, salt, PBKDF2_ITERATIONS, AES_KEY_SIZE)
    }

    /**
     * 使用应用签名派生固定密钥（HKDF模式）
     *
     * @param appSignature 应用签名SHA256
     * @return 32字节AES密钥
     */
    fun deriveFixedKey(appSignature: ByteArray): ByteArray {
        // HKDF派生
        return hkdfDerive(
            inputKeyMaterial = appSignature,
            salt = HKDF_FIXED_SALT.toByteArray(Charsets.UTF_8),
            info = HKDF_INFO.toByteArray(Charsets.UTF_8),
            outputLength = AES_KEY_SIZE / 8
        )
    }

    /**
     * 生成随机盐值
     *
     * @return 16字节随机盐
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(PBKDF2_SALT_SIZE)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * 生成随机IV
     *
     * @return 12字节随机IV
     */
    fun generateIV(): ByteArray {
        val iv = ByteArray(GCM_IV_SIZE)
        SecureRandom().nextBytes(iv)
        return iv
    }

    /**
     * 加密数据
     *
     * @param plaintext 明文数据
     * @param key 32字节AES密钥
     * @param iv 12字节IV
     * @return 加密后的密文（包含认证标签）
     */
    fun encrypt(plaintext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        require(key.size == AES_KEY_SIZE / 8) { "Key must be 32 bytes" }
        require(iv.size == GCM_IV_SIZE) { "IV must be 12 bytes" }

        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance(AES_GCM_ALGORITHM)
        val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        return cipher.doFinal(plaintext)
    }

    /**
     * 解密数据
     *
     * @param ciphertext 密文（包含认证标签）
     * @param key 32字节AES密钥
     * @param iv 12字节IV
     * @return 解密后的明文
     * @throws SecurityException 如果解密失败（密码错误或数据损坏）
     */
    fun decrypt(ciphertext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        require(key.size == AES_KEY_SIZE / 8) { "Key must be 32 bytes" }
        require(iv.size == GCM_IV_SIZE) { "IV must be 12 bytes" }

        try {
            val secretKey: SecretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(AES_GCM_ALGORITHM)
            val spec = GCMParameterSpec(GCM_TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            return cipher.doFinal(ciphertext)
        } catch (e: Exception) {
            throw SecurityException("Decryption failed: password incorrect or file corrupted", e)
        }
    }

    /**
     * PBKDF2密钥派生
     */
    private fun pbkdf2Derive(password: ByteArray, salt: ByteArray, iterations: Int, keySizeBits: Int): ByteArray {
        // 使用Java内置的PBKDF2实现
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = javax.crypto.spec.PBEKeySpec(
            String(password, Charsets.UTF_8).toCharArray(),
            salt,
            iterations,
            keySizeBits
        )
        return factory.generateSecret(spec).encoded
    }

    /**
     * HKDF密钥派生（RFC 5869）
     */
    private fun hkdfDerive(
        inputKeyMaterial: ByteArray,
        salt: ByteArray,
        info: ByteArray,
        outputLength: Int
    ): ByteArray {
        // Extract step: HMAC-SHA256(salt, IKM) -> PRK
        val prk = hmacSha256(salt, inputKeyMaterial)

        // Expand step: HMAC-SHA256(PRK, info || 0x01) -> OKM
        val okm = hmacSha256(prk, info + byteArrayOf(0x01))

        // 返回指定长度
        return okm.take(outputLength).toByteArray()
    }

    /**
     * HMAC-SHA256计算
     */
    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    /**
     * 验证密码强度
     *
     * @param password 待验证密码
     * @return 是否符合最低要求（至少6个字符）
     */
    fun validatePasswordStrength(password: String): Boolean {
        return password.length >= 6
    }
}