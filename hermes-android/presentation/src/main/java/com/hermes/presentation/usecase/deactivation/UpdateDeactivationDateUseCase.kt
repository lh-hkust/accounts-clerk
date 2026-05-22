package com.hermes.presentation.usecase.deactivation

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import java.time.Instant

/**
 * 修改停用日期用例
 */
class UpdateDeactivationDateUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val deactivationRepository: IdentifierDeactivationRepository
) {
    /**
     * 修改停用日期
     *
     * @param identifierId 标识ID
     * @param newScheduledTime 新的计划停用时间
     * @throws IllegalArgumentException 如果新时间不合法
     */
    suspend operator fun invoke(identifierId: Long, newScheduledTime: Instant) {
        if (newScheduledTime.isBefore(Instant.now())) {
            throw IllegalArgumentException("Deactivation date must be greater than current date")
        }

        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        if (identifier.status != com.hermes.domain.valueobject.IdentifierStatus.PENDING_DEACTIVATION) {
            throw IllegalArgumentException("Only PENDING_DEACTIVATION identifiers can modify deactivation date")
        }

        identifier.plannedDeactTime = newScheduledTime
        identifier.updatedAt = Instant.now()
        identifierRepository.update(identifier)

        val deactivation = deactivationRepository.getByIdentifierId(identifierId)
        if (deactivation != null) {
            deactivationRepository.updateScheduledTime(deactivation.id!!, newScheduledTime)
        }
    }
}