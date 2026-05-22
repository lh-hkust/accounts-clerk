package com.hermes.domain.service

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.IdentifierType
import java.time.Instant

/**
 * 身份标识服务
 * 协调身份标识聚合的业务逻辑
 */
interface IdentifierService {

    /**
     * 创建身份标识
     *
     * @param type 标识类型（PHONE/EMAIL）
     * @param value 标识值
     * @return 创建的身份标识
     * @throws IllegalArgumentException 如果标识重复
     */
    fun createIdentifier(type: IdentifierType, value: String): IdentityIdentifier

    /**
     * 检查标识是否存在（重复检测）
     *
     * @param type 标识类型
     * @param value 标识值
     * @return 是否已存在
     */
    fun checkDuplicate(type: IdentifierType, value: String): Boolean

    /**
     * 获取标识绑定的账户数量
     *
     * @param identifierId 标识ID
     * @return 绑定的账户数量
     */
    fun getBoundAccountCount(identifierId: Long): Int

    /**
     * 获取标识详情
     *
     * @param identifierId 标识ID
     * @return 身份标识（如果存在）
     */
    fun getIdentifierById(identifierId: Long): IdentityIdentifier?

    /**
     * 获取所有标识列表
     *
     * @return 标识列表
     */
    fun getAllIdentifiers(): List<IdentityIdentifier>

    /**
     * 删除身份标识
     *
     * @param identifierId 标识ID
     * @throws IllegalArgumentException 如果标识有绑定账户
     */
    fun deleteIdentifier(identifierId: Long)
}