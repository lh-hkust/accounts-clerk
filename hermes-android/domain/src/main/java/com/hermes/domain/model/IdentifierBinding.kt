package com.hermes.domain.model

import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant

/**
 * 标识绑定实体
 * IdentifierBinding (IB) - 账户与身份标识的关联关系
 *
 * 所属聚合：ApplicationAccountAggregate
 *
 * @see aggregates.md 3.2 实体：IdentifierBinding
 */
class IdentifierBinding(
    val id: Long?,
    val accountId: Long,
    val identifierId: Long,
    var purposes: List<BindingPurpose>,
    var isPrimary: Boolean = false,
    val boundAt: Instant,
    var verifiedAt: Instant? = null,
    var notes: String? = null
) {
    /**
     * 更新绑定用途
     *
     * @param newPurposes 新用途列表
     * @throws IllegalArgumentException 如果用途列表为空
     */
    fun updatePurposes(newPurposes: List<BindingPurpose>) {
        if (newPurposes.isEmpty()) {
            throw IllegalArgumentException("Binding purposes must not be empty")
        }
        purposes = newPurposes
    }

    /**
     * 设置为主要标识
     */
    fun setPrimary() {
        isPrimary = true
    }

    /**
     * 设置为次要标识
     */
    fun setSecondary() {
        isPrimary = false
    }

    /**
     * 添加备注
     */
    fun updateNotes(notes: String?) {
        this.notes = notes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdentifierBinding) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}