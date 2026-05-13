package com.hermes.presentation.usecase.identifier

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierBindingRepository

/**
 * 删除身份标识用例
 */
class DeleteIdentifierUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val bindingRepository: IdentifierBindingRepository
) {
    /**
     * 删除标识
     *
     * @param identifierId 标识ID
     * @throws IllegalArgumentException 如果标识有绑定账户
     */
    suspend operator fun invoke(identifierId: Long) {
        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        val boundCount = bindingRepository.getCountByIdentifierId(identifierId)
        if (boundCount > 0) {
            throw IllegalArgumentException("Identifier has bound accounts, please unbind first")
        }

        identifierRepository.delete(identifier)
    }

    /**
     * 检查是否可以删除
     *
     * @param identifierId 标识ID
     * @return 是否可以删除
     */
    suspend fun canDelete(identifierId: Long): Boolean {
        val boundCount = bindingRepository.getCountByIdentifierId(identifierId)
        return boundCount == 0
    }
}