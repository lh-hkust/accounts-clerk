package com.hermes.domain.model

import com.hermes.domain.valueobject.DeactivationStatus
import com.hermes.domain.valueobject.DeactivationType
import java.time.Instant

/**
 * 标识停用实体
 * IdentifierDeactivation - 身份标识的停用计划记录
 *
 * 所属聚合：IdentityIdentifierAggregate
 *
 * @see aggregates.md 2.2 实体：IdentifierDeactivation
 */
class IdentifierDeactivation(
    val id: Long?,
    val identifierId: Long,
    val deactType: DeactivationType,
    var status: DeactivationStatus = DeactivationStatus.SCHEDULED,
    var scheduledTime: Instant?,
    var actualTime: Instant? = null,
    var reason: String? = null,
    var cancelReason: String? = null,
    val createdAt: Instant,
    var updatedAt: Instant
) {
    /**
     * 执行停用
     * 状态从SCHEDULED变为EXECUTED
     */
    fun execute() {
        status = DeactivationStatus.EXECUTED
        actualTime = Instant.now()
        updatedAt = Instant.now()
    }

    /**
     * 取消停用计划
     * 状态从SCHEDULED变为CANCELLED
     *
     * @param cancelReason 取消原因
     */
    fun cancel(cancelReason: String) {
        status = DeactivationStatus.CANCELLED
        this.cancelReason = cancelReason
        updatedAt = Instant.now()
    }

    /**
     * 更新计划时间
     *
     * @param newTime 新的计划时间
     */
    fun updateScheduledTime(newTime: Instant) {
        scheduledTime = newTime
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdentifierDeactivation) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}