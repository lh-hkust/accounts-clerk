package com.hermes.domain.model

import com.hermes.domain.valueobject.FieldType
import java.time.Instant

/**
 * 账户扩展实体
 * AccountExtension (AE) - 账户的额外属性
 *
 * 所属聚合：ApplicationAccountAggregate
 *
 * @see aggregates.md 3.3 实体：AccountExtension
 */
class AccountExtension(
    val id: Long?,
    val accountId: Long,
    val key: String,
    var value: String?,
    var label: String,
    val fieldType: FieldType,
    var options: List<String>? = null,
    val createdAt: Instant,
    var updatedAt: Instant
) {
    /**
     * 更新值
     */
    fun updateValue(newValue: String?) {
        value = newValue
        updatedAt = Instant.now()
    }

    /**
     * 更新显示名称
     */
    fun updateLabel(newLabel: String) {
        label = newLabel
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccountExtension) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}