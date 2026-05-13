package com.hermes.presentation.usecase.identifier

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.valueobject.IdentifierType
import java.time.Instant

/**
 * 添加身份标识用例
 */
class AddIdentifierUseCase(
    private val identifierRepository: IdentityIdentifierRepository
) {
    /**
     * 执行添加标识
     *
     * @param type 标识类型
     * @param value 标识值
     * @return 创建的身份标识
     * @throws IllegalArgumentException 如果标识重复
     */
    suspend operator fun invoke(type: IdentifierType, value: String): IdentityIdentifier {
        if (value.isBlank()) {
            throw IllegalArgumentException("Identifier value must not be empty")
        }

        val normalizedValue = normalizeValue(type, value)

        if (identifierRepository.checkDuplicate(type, normalizedValue)) {
            throw IllegalArgumentException("Identifier already exists")
        }

        val now = Instant.now()
        val identifier = IdentityIdentifier(
            id = null,
            type = type,
            value = normalizedValue,
            status = com.hermes.domain.valueobject.IdentifierStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        return identifierRepository.insert(identifier)
    }

    private fun normalizeValue(type: IdentifierType, value: String): String {
        return when (type) {
            IdentifierType.EMAIL -> value.lowercase().trim()
            IdentifierType.PHONE -> value.trim()
        }
    }
}