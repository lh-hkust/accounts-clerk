package com.hermes.presentation.usecase.account

import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.valueobject.FieldType

/**
 * 添加账户扩展用例
 */
class AddAccountExtensionUseCase(
    private val accountRepository: ApplicationAccountRepository
) {
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
    suspend operator fun invoke(
        accountId: Long,
        key: String,
        value: String?,
        label: String,
        fieldType: FieldType
    ) {
        val account = accountRepository.getById(accountId)
            ?: throw IllegalArgumentException("Account not found")

        if (key.isBlank()) {
            throw IllegalArgumentException("Extension key must not be empty")
        }

        account.addExtension(key, value, label, fieldType)
        accountRepository.update(account)
    }
}