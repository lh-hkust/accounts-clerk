package com.hermes.domain.service

/**
 * 密钥管理服务接口
 * 定义数据库加密密钥管理的方法
 *
 * @see data-security.md 四、密钥管理
 */
interface KeyManagementService {

    /**
     * 检查是否已初始化密钥
     *
     * @return 是否已初始化
     */
    fun isKeyInitialized(): Boolean

    /**
     * 检查是否设置了用户密码
     *
     * @return 是否有密码保护
     */
    fun hasPassword(): Boolean

    /**
     * 初始化密钥（首次使用）
     *
     * @param password 用户密码（可选，null表示无密码模式）
     * @return 数据库密钥（用于SQLCipher）
     */
    fun initializeKey(password: String?): ByteArray

    /**
     * 获取数据库密钥
     *
     * @param password 用户密码（如果有密码保护）
     * @return 数据库密钥
     * @throws SecurityException 如果密码错误或密钥未初始化
     */
    fun getDatabaseKey(password: String?): ByteArray

    /**
     * 更新密码
     *
     * @param currentPassword 当前密码
     * @param newPassword 新密码
     * @throws SecurityException 如果当前密码错误
     */
    fun updatePassword(currentPassword: String?, newPassword: String?)

    /**
     * 验证密码
     *
     * @param password 待验证密码
     * * @return 是否正确
     */
    fun verifyPassword(password: String?): Boolean

    /**
     * 清除所有密钥（数据清空时使用）
     */
    fun clearKeys()
}