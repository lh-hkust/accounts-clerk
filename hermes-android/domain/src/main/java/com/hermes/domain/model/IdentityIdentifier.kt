package com.hermes.domain.model

import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import java.time.Instant

/**
 * 身份标识聚合根
 * IdentityIdentifier (IID) - 用于声明身份的唯一标识
 *
 * 身份标识是手机号或邮箱，用于身份验证或安全验证。
 * 与认证凭据（密码/令牌）不同，身份标识仅声明身份，不证明身份。
 *
 * @see NIST SP 800-63B Digital Identity Guidelines
 * @see aggregates.md 二、身份标识聚合
 */
class IdentityIdentifier(
    val id: Long?,
    val type: IdentifierType,
    val value: String,
    var status: IdentifierStatus = IdentifierStatus.ACTIVE,
    var plannedDeactTime: Instant? = null,
    var deactReason: String? = null,
    val createdAt: Instant,
    var updatedAt: Instant
) {
    /**
     * 设置停用计划
     * 状态从ACTIVE变为PENDING_DEACTIVATION
     *
     * @param time 计划停用时间（必须大于当前时间）
     * @param reason 停用原因
     * @throws IllegalArgumentException 如果时间不合法或状态不允许
     */
    fun scheduleDeactivation(time: Instant, reason: String) {
        if (time.isBefore(Instant.now())) {
            throw IllegalArgumentException("Deactivation time must be greater than current time")
        }
        if (status != IdentifierStatus.ACTIVE) {
            throw IllegalArgumentException("Only ACTIVE identifiers can be scheduled for deactivation")
        }
        status = IdentifierStatus.PENDING_DEACTIVATION
        plannedDeactTime = time
        deactReason = reason
        updatedAt = Instant.now()
    }

    /**
     * 取消停用计划
     * 状态从PENDING_DEACTIVATION恢复为ACTIVE
     *
     * @throws IllegalArgumentException 如果状态不是PENDING_DEACTIVATION
     */
    fun cancelDeactivation() {
        if (status != IdentifierStatus.PENDING_DEACTIVATION) {
            throw IllegalArgumentException("Only PENDING_DEACTIVATION identifiers can cancel deactivation")
        }
        status = IdentifierStatus.ACTIVE
        plannedDeactTime = null
        deactReason = null
        updatedAt = Instant.now()
    }

    /**
     * 激活标识
     * 状态设置为ACTIVE
     */
    fun activate() {
        status = IdentifierStatus.ACTIVE
        updatedAt = Instant.now()
    }

    /**
     * 执行停用
     * 状态从PENDING_DEACTIVATION变为DEACTIVATED
     *
     * @throws IllegalArgumentException 如果状态不是PENDING_DEACTIVATION
     */
    fun deactivate() {
        if (status != IdentifierStatus.PENDING_DEACTIVATION) {
            throw IllegalArgumentException("Only PENDING_DEACTIVATION identifiers can be deactivated")
        }
        status = IdentifierStatus.DEACTIVATED
        updatedAt = Instant.now()
    }

    /**
     * 标记失效
     * 状态设置为INVALIDATED
     */
    fun invalidate() {
        status = IdentifierStatus.INVALIDATED
        updatedAt = Instant.now()
    }

    /**
     * 检查是否可以删除
     * 只有未绑定任何账户的标识才能删除
     *
     * @param boundAccountCount 绑定的账户数量
     * @return 是否可以删除
     */
    fun canDelete(boundAccountCount: Int): Boolean {
        return boundAccountCount == 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdentityIdentifier) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "IdentityIdentifier(id=$id, type=$type, value='$value', status=$status)"
    }
}