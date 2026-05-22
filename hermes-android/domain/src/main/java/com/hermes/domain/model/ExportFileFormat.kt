package com.hermes.domain.model

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 导出文件格式定义
 *
 * @see export-file-format.md 一、加密文件格式（.hexport）
 */
object ExportFileFormat {

    // 文件格式常量
    const val MAGIC_NUMBER: Int = -0x5500FEFF // 魔数 (0xAAFF0101 as signed Int)
    const val VERSION: Byte = 0x01 // 版本号
    const val AES_GCM_ALGORITHM_ID: Byte = 0x01 // AES-256-GCM算法ID
    const val KDF_PBKDF2: Byte = 0x01 // PBKDF2(密码模式)
    const val KDF_HKDF: Byte = 0x02 // HKDF(无密码模式)

    // 文件扩展名
    const val ENCRYPTED_EXTENSION = ".hexport"
    const val PLAIN_EXTENSION = ".json"

    // GCM参数
    const val IV_SIZE = 12
    const val TAG_SIZE = 16
    const val SALT_SIZE = 16

    /**
     * 加密文件头信息
     */
    data class FileHeader(
        val magicNumber: Int,
        val version: Byte,
        val algorithmId: Byte,
        val kdfId: Byte,
        val flags: Byte,
        val salt: ByteArray?, // 仅密码模式存在
        val iv: ByteArray,
        val authTag: ByteArray,
        val ciphertextLength: Int
    ) {
        /**
         * 是否为密码模式
         */
        fun isPasswordMode(): Boolean = kdfId == KDF_PBKDF2

        /**
         * 是否启用压缩
         */
        fun isCompressed(): Boolean = (flags.toInt() and 0x01) != 0
    }

    /**
     * 序列化加密文件
     *
     * @param ciphertext 密文（包含GCM认证标签）
     * @param iv 初始化向量（12字节）
     * @param salt 盐值（密码模式16字节，无密码模式null）
     * @param kdfId KDF算法ID
     * @param compressed 是否压缩
     * @return 完整的加密文件数据
     */
    fun serialize(
        ciphertext: ByteArray,
        iv: ByteArray,
        salt: ByteArray?,
        kdfId: Byte,
        compressed: Boolean = true
    ): ByteArray {
        require(iv.size == IV_SIZE) { "IV must be $IV_SIZE bytes" }
        if (kdfId == KDF_PBKDF2) {
            require(salt != null && salt.size == SALT_SIZE) { "PBKDF2 mode requires $SALT_SIZE bytes salt" }
        }

        val output = ByteArrayOutputStream()

        // 魔数（4字节，大端序）
        output.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(MAGIC_NUMBER).array())

        // 版本号（1字节）
        output.write(VERSION.toInt())

        // 加密算法ID（1字节）
        output.write(AES_GCM_ALGORITHM_ID.toInt())

        // KDF算法ID（1字节）
        output.write(kdfId.toInt())

        // 标志位（1字节）
        val flags: Byte = if (compressed) 0x01 else 0x00
        output.write(flags.toInt())

        // 盐长度和盐（仅密码模式）
        if (kdfId == KDF_PBKDF2 && salt != null) {
            output.write(salt.size) // 盐长度（1字节）
            output.write(salt)
        } else {
            output.write(0) // 无密码模式，盐长度为0
        }

        // IV长度和IV
        output.write(IV_SIZE) // IV长度（1字节，固定12）
        output.write(iv)

        // 认证标签（16字节，从密文中提取）
        // AES-GCM的密文格式为: ciphertext + authTag
        require(ciphertext.size >= TAG_SIZE) { "Ciphertext must include auth tag" }
        val actualCiphertext = ciphertext.dropLast(TAG_SIZE).toByteArray()
        val authTag = ciphertext.takeLast(TAG_SIZE).toByteArray()
        output.write(authTag)

        // 密文长度（4字节，大端序）
        output.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(actualCiphertext.size).array())

        // 密文
        output.write(actualCiphertext)

        return output.toByteArray()
    }

    /**
     * 解析加密文件头
     *
     * @param data 加密文件数据
     * @return 文件头信息和剩余的密文数据
     * @throws IllegalArgumentException 如果文件格式无效
     */
    fun parse(data: ByteArray): Pair<FileHeader, ByteArray> {
        val input = ByteArrayInputStream(data)

        // 验证文件长度
        val minHeaderSize = 4 + 1 + 1 + 1 + 1 + 1 + 1 + IV_SIZE + TAG_SIZE + 4
        require(data.size >= minHeaderSize) { "File too small to be valid .hexport file" }

        // 魔数（4字节）
        val magicBytes = ByteArray(4)
        input.read(magicBytes)
        val magicNumber = ByteBuffer.wrap(magicBytes).order(ByteOrder.BIG_ENDIAN).int
        require(magicNumber == MAGIC_NUMBER) { "Invalid magic number: not a .hexport file" }

        // 版本号（1字节）
        val version = input.read().toByte()
        require(version == VERSION) { "Unsupported version: $version" }

        // 加密算法ID（1字节）
        val algorithmId = input.read().toByte()
        require(algorithmId == AES_GCM_ALGORITHM_ID) { "Unsupported algorithm: $algorithmId" }

        // KDF算法ID（1字节）
        val kdfId = input.read().toByte()
        require(kdfId == KDF_PBKDF2 || kdfId == KDF_HKDF) { "Unsupported KDF: $kdfId" }

        // 标志位（1字节）
        val flags = input.read().toByte()

        // 盐长度和盐
        val saltLength = input.read()
        val salt = if (saltLength > 0) {
            val saltBytes = ByteArray(saltLength)
            input.read(saltBytes)
            saltBytes
        } else null

        // IV长度和IV
        val ivLength = input.read()
        require(ivLength == IV_SIZE) { "Invalid IV length: $ivLength" }
        val iv = ByteArray(IV_SIZE)
        input.read(iv)

        // 认证标签（16字节）
        val authTag = ByteArray(TAG_SIZE)
        input.read(authTag)

        // 密文长度（4字节）
        val ciphertextLengthBytes = ByteArray(4)
        input.read(ciphertextLengthBytes)
        val ciphertextLength = ByteBuffer.wrap(ciphertextLengthBytes).order(ByteOrder.BIG_ENDIAN).int

        // 密文
        val ciphertext = ByteArray(ciphertextLength)
        input.read(ciphertext)

        // 构建文件头
        val header = FileHeader(
            magicNumber = magicNumber,
            version = version,
            algorithmId = algorithmId,
            kdfId = kdfId,
            flags = flags,
            salt = salt,
            iv = iv,
            authTag = authTag,
            ciphertextLength = ciphertextLength
        )

        // 密文数据（需要拼接认证标签）
        val ciphertextWithTag = ciphertext + authTag

        return Pair(header, ciphertextWithTag)
    }

    /**
     * 检测文件类型
     *
     * @param fileName 文件名
     * @return 文件类型（加密或明文）
     */
    fun detectFileType(fileName: String): FileType {
        return when {
            fileName.endsWith(ENCRYPTED_EXTENSION, ignoreCase = true) -> FileType.ENCRYPTED
            fileName.endsWith(PLAIN_EXTENSION, ignoreCase = true) -> FileType.PLAIN_JSON
            else -> FileType.UNKNOWN
        }
    }

    /**
     * 检测文件类型（通过文件内容）
     *
     * @param data 文件数据
     * @return 文件类型（加密或明文）
     */
    fun detectFileTypeByContent(data: ByteArray): FileType {
        if (data.size < 4) return FileType.UNKNOWN

        val magicBytes = data.take(4).toByteArray()
        val magicNumber = ByteBuffer.wrap(magicBytes).order(ByteOrder.BIG_ENDIAN).int

        return if (magicNumber == MAGIC_NUMBER) {
            FileType.ENCRYPTED
        } else {
            // 检查是否为JSON文件（以 { 开头）
            val firstChar = data.first().toInt().toChar()
            if (firstChar == '{') {
                FileType.PLAIN_JSON
            } else {
                FileType.UNKNOWN
            }
        }
    }

    enum class FileType {
        ENCRYPTED,
        PLAIN_JSON,
        UNKNOWN
    }
}