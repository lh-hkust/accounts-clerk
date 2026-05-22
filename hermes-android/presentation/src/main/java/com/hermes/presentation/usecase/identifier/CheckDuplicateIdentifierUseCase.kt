package com.hermes.presentation.usecase.identifier

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.valueobject.IdentifierType

/**
 * 检查标识重复用例
 */
class CheckDuplicateIdentifierUseCase(
    private val identifierRepository: IdentityIdentifierRepository
) {
    /**
     * 检查标识是否已存在
     *
     * @param type 标识类型
     * @param value 标识值
     * @return 是否重复
     */
    suspend operator fun invoke(type: IdentifierType, value: String): Boolean {
        val normalizedValue = normalizeValue(type, value)
        return identifierRepository.checkDuplicate(type, normalizedValue)
    }

    private fun normalizeValue(type: IdentifierType, value: String): String {
        return when (type) {
            IdentifierType.EMAIL -> value.lowercase().trim()
            IdentifierType.PHONE -> value.trim()
        }
    }
}