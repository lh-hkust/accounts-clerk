package com.hermes.presentation.usecase.deactivation

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.WarningRecordRepository

/**
 * 取消停用计划用例
 */
class CancelDeactivationUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val deactivationRepository: IdentifierDeactivationRepository,
    private val warningRepository: WarningRecordRepository
) {
    /**
     * 取消停用计划
     *
     * @param identifierId 标识ID
     * @param cancelReason 取消原因
     * @throws IllegalArgumentException 如果标识状态不是PENDING_DEACTIVATION
     */
    suspend operator fun invoke(identifierId: Long, cancelReason: String) {
        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        if (identifier.status != com.hermes.domain.valueobject.IdentifierStatus.PENDING_DEACTIVATION) {
            throw IllegalArgumentException("Only PENDING_DEACTIVATION identifiers can cancel deactivation")
        }

        identifier.cancelDeactivation()
        identifierRepository.update(identifier)

        val deactivation = deactivationRepository.getByIdentifierId(identifierId)
        if (deactivation != null) {
            deactivation.cancel(cancelReason)
            deactivationRepository.markCancelled(deactivation.id!!, cancelReason)
        }

        warningRepository.deleteByIdentifierId(identifierId)
    }
}