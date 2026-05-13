package com.hermes.domain.service

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose

/**
 * 绑定服务
 * 协调标识绑定的业务逻辑
 */
interface BindingService {

    /**
     * 绑定标识到账户
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @param purposes 绑定用途列表
     * @param isPrimary 是否为主要标识
     * @return 创建的绑定实体
     * @throws IllegalArgumentException 如果绑定已存在或用途列表为空
     */
    fun bindIdentifier(
        accountId: Long,
        identifierId: Long,
        purposes: List<BindingPurpose>,
        isPrimary: Boolean = false
    ): IdentifierBinding

    /**
     * 解绑标识
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @return 历史记录
     */
    fun unbindIdentifier(accountId: Long, identifierId: Long): BindingHistoryRecord

    /**
     * 修改绑定用途
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @param newPurposes 新用途列表
     * @return 历史记录
     * @throws IllegalArgumentException 如果新用途列表为空
     */
    fun changeBindingPurpose(
        accountId: Long,
        identifierId: Long,
        newPurposes: List<BindingPurpose>
    ): BindingHistoryRecord

    /**
     * 更换绑定标识
     *
     * @param accountId 账户ID
     * @param oldIdentifierId 原标识ID
     * @param newIdentifierId 新标识ID
     * @return 历史记录
     */
    fun switchBindingIdentifier(
        accountId: Long,
        oldIdentifierId: Long,
        newIdentifierId: Long
    ): BindingHistoryRecord

    /**
     * 获取账户的绑定列表
     *
     * @param accountId 账户ID
     * @return 绑定列表
     */
    fun getBindingsByAccountId(accountId: Long): List<IdentifierBinding>

    /**
     * 获取标识的绑定列表
     *
     * @param identifierId 标识ID
     * @return 绑定列表
     */
    fun getBindingsByIdentifierId(identifierId: Long): List<IdentifierBinding>

    /**
     * 创建绑定历史记录
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @param actionType 动作类型
     * @param previousPurposes 原用途列表
     * @param newPurposes 新用途列表
     * @param previousIdentifierId 原标识ID
     * @param newIdentifierId 新标识ID
     * @return 历史记录
     */
    fun createHistoryRecord(
        accountId: Long,
        identifierId: Long,
        actionType: ActionType,
        previousPurposes: List<BindingPurpose>? = null,
        newPurposes: List<BindingPurpose>? = null,
        previousIdentifierId: Long? = null,
        newIdentifierId: Long? = null
    ): BindingHistoryRecord

    /**
     * 获取绑定历史记录
     *
     * @param accountId 账户ID（可选）
     * @param identifierId 标识ID（可选）
     * @return 历史记录列表
     */
    fun getBindingHistory(accountId: Long? = null, identifierId: Long? = null): List<BindingHistoryRecord>
}