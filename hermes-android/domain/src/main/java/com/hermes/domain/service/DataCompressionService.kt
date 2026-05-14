package com.hermes.domain.service

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * 数据压缩服务
 * 使用 zlib 算法压缩和解压数据
 *
 * @see export-file-format.md 三、数据序列化规则
 */
class DataCompressionService {

    /**
     * 压缩数据
     *
     * @param data 待压缩数据
     * @return 压缩后的数据
     */
    fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater(Deflater.BEST_COMPRESSION)
        deflater.setInput(data)
        deflater.finish()

        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)

        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            output.write(buffer, 0, count)
        }

        deflater.end()
        return output.toByteArray()
    }

    /**
     * 解压数据
     *
     * @param compressedData 压缩数据
     * @return 解压后的原始数据
     * @throws IllegalArgumentException 如果解压失败
     */
    fun decompress(compressedData: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(compressedData)

        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)

        try {
            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0 && inflater.needsInput()) {
                    break
                }
                output.write(buffer, 0, count)
            }
        } catch (e: Exception) {
            inflater.end()
            throw IllegalArgumentException("Decompression failed: invalid compressed data", e)
        }

        inflater.end()
        return output.toByteArray()
    }
}