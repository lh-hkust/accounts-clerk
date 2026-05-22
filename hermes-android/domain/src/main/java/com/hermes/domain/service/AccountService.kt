package com.hermes.domain.service

import com.hermes.domain.model.AccountExtension
import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.FieldType

/**
 * 应用账户服务
 * 协调应用账户聚合的业务逻辑
 */
interface AccountService {

    /**
     * 创建应用账户
     *
     * @param applicationId 应用ID
     * @param accountName 账户名称
     * @param accountIdentifier 账户标识（可选）
     * @param nickname 昵称（可选）
     * @return 创建的应用账户
     * @throws IllegalArgumentException 如果账户标识重复
     */
    fun createAccount(
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?
    ): ApplicationAccount

    /**
     * 检查账户标识是否重复
     *
     * @param applicationId 应用ID
     * @param accountIdentifier 账户标识
     * @return 是否已存在
     */
    fun checkDuplicateAccount(applicationId: Long, accountIdentifier: String): Boolean

    /**
     * 获取账户详情
     *
     * @param accountId 账户ID
     * @return 应用账户（如果存在）
     */
    fun getAccountById(accountId: Long): ApplicationAccount?

    /**
     * 获取账户列表
     *
     * @param applicationId 应用ID（可选，用于筛选）
     * @return 账户列表
     */
    fun getAccountList(applicationId: Long? = null): List<ApplicationAccount>

    /**
     * 更新账户状态
     *
     * @param accountId 账户ID
     * @param newStatus 新状态
     * @throws IllegalArgumentException 如果状态转换非法
     */
    fun updateAccountStatus(accountId: Long, newStatus: AccountStatus)

    /**
     * 添加账户扩展字段
     *
     * @param accountId 账户ID
     * @param key 字段标识
     * @param value 字段值
     * @param label 显示名称
     * @param fieldType 字段类型
     * @return 创建的扩展实体
     * @throws IllegalArgumentException 如果key已存在
     */
    fun addAccountExtension(
        accountId: Long,
        key: String,
        value: String?,
        label: String,
        fieldType: FieldType
    ): AccountExtension

    /**
     * 删除账户扩展字段
     *
     * @param accountId 账户ID
     * @param key 字段标识
     */
    fun removeAccountExtension(accountId: Long, key: String)

    /**
     * 获取绑定到指定标识的所有账户
     *
     * @param identifierId 标识ID
     * @return 账户列表
     */
    fun getAccountsByIdentifierId(identifierId: Long): List<ApplicationAccount>
}