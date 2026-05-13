package com.hermes.domain.model

import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant
import java.time.LocalDate

/**
 * 应用账户聚合根
 * ApplicationAccount (AA) - 用户在应用中注册的账户
 *
 * @see aggregates.md 三、应用账户聚合
 */
class ApplicationAccount(
    val id: Long?,
    val applicationId: Long,
    var accountName: String,
    var accountIdentifier: String? = null,
    var nickname: String? = null,
    var status: AccountStatus = AccountStatus.ACTIVE,
    var keepAliveEnabled: Boolean = true,
    var lastLoginDate: LocalDate? = null,
    var notes: String? = null,
    var tags: List<String> = emptyList(),
    val createdAt: Instant,
    var updatedAt: Instant
) {
    private val _bindings: MutableList<IdentifierBinding> = mutableListOf()
    private val _extensions: MutableList<AccountExtension> = mutableListOf()

    val bindings: List<IdentifierBinding> get() = _bindings.toList()
    val extensions: List<AccountExtension> get() = _extensions.toList()

    /**
     * 绑定身份标识
     *
     * @param identifierId 身份标识ID
     * @param purposes 绑定用途列表
     * @param isPrimary 是否为主要标识
     * @return 创建的绑定实体
     * @throws IllegalArgumentException 如果绑定已存在
     */
    fun bindIdentifier(identifierId: Long, purposes: List<BindingPurpose>, isPrimary: Boolean = false): IdentifierBinding {
        if (_bindings.any { it.identifierId == identifierId }) {
            throw IllegalArgumentException("Identifier already bound to this account")
        }
        if (purposes.isEmpty()) {
            throw IllegalArgumentException("Binding purposes must not be empty")
        }
        val binding = IdentifierBinding(
            id = null,
            accountId = id!!,
            identifierId = identifierId,
            purposes = purposes,
            isPrimary = isPrimary,
            boundAt = Instant.now()
        )
        _bindings.add(binding)
        updatedAt = Instant.now()
        return binding
    }

    /**
     * 解绑身份标识
     *
     * @param identifierId 身份标识ID
     */
    fun unbindIdentifier(identifierId: Long) {
        _bindings.removeAll { it.identifierId == identifierId }
        updatedAt = Instant.now()
    }

    /**
     * 更新账户状态
     *
     * @param newStatus 新状态
     * @throws IllegalArgumentException 如果状态转换非法
     */
    fun updateStatus(newStatus: AccountStatus) {
        validateStatusTransition(status, newStatus)
        status = newStatus
        updatedAt = Instant.now()
    }

    /**
     * 添加扩展字段
     *
     * @param key 字段标识
     * @param value 字段值
     * @param label 显示名称
     * @param fieldType 字段类型
     * @return 创建的扩展实体
     * @throws IllegalArgumentException 如果key已存在
     */
    fun addExtension(key: String, value: String?, label: String, fieldType: com.hermes.domain.valueobject.FieldType): AccountExtension {
        if (_extensions.any { it.key == key }) {
            throw IllegalArgumentException("Extension key already exists: $key")
        }
        val extension = AccountExtension(
            id = null,
            accountId = id!!,
            key = key,
            value = value,
            label = label,
            fieldType = fieldType,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        _extensions.add(extension)
        updatedAt = Instant.now()
        return extension
    }

    /**
     * 删除扩展字段
     *
     * @param key 字段标识
     */
    fun removeExtension(key: String) {
        _extensions.removeAll { it.key == key }
        updatedAt = Instant.now()
    }

    private fun validateStatusTransition(from: AccountStatus, to: AccountStatus) {
        val validTransitions = mapOf(
            AccountStatus.ACTIVE to setOf(AccountStatus.FROZEN, AccountStatus.LOST, AccountStatus.ARCHIVED),
            AccountStatus.FROZEN to setOf(AccountStatus.ACTIVE),
            AccountStatus.LOST to emptySet(),
            AccountStatus.ARCHIVED to emptySet()
        )
        if (validTransitions[from]?.contains(to) != true) {
            throw IllegalArgumentException("Invalid status transition: $from -> $to")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApplicationAccount) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}