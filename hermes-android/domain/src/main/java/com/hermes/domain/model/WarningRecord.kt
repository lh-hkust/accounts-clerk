package com.hermes.domain.model

import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import java.time.Instant

/**
 * 预警聚合根
 * WarningRecord - 系统对潜在风险的提醒记录
 *
 * @see aggregates.md 五、预警聚合
 */
class WarningRecord(
    val id: Long?,
    val identifierId: Long?,
    val accountId: Long?,
    val warningType: WarningType,
    val warningLevel: WarningLevel,
    val message: String,
    val triggeredAt: Instant,
    var isRead: Boolean = false,
    var isHandled: Boolean = false,
    var handledAt: Instant? = null
) {
    /**
     * 标记已读
     */
    fun markAsRead() {
        isRead = true
    }

    /**
     * 标记已处理
     */
    fun handle() {
        isHandled = true
        handledAt = Instant.now()
    }

    /**
     * 检查是否为标识预警
     */
    fun isIdentifierWarning(): Boolean = identifierId != null

    /**
     * 检查是否为账户预警
     */
    fun isAccountWarning(): Boolean = accountId != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WarningRecord) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}