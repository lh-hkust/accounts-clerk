package com.hermes.presentation.usecase.deactivation

import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.domain.valueobject.IdentifierStatus
import java.time.Instant

/**
 * 设置停用计划用例
 */
class ScheduleDeactivationUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val deactivationRepository: IdentifierDeactivationRepository
) {
    /**
     * 设置停用计划
     *
     * @param identifierId 标识ID
     * @param scheduledTime 计划停用时间
     * @param reason 停用原因
     * @param type 停用类型
     * @return 停用计划实体
     * @throws IllegalArgumentException 如果标识状态不允许或时间不合法
     */
    suspend operator fun invoke(
        identifierId: Long,
        scheduledTime: Instant,
        reason: String,
        type: DeactivationType
    ): IdentifierDeactivation {
        if (scheduledTime.isBefore(Instant.now())) {
            throw IllegalArgumentException("Deactivation date must be greater than current date")
        }

        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        if (identifier.status != IdentifierStatus.ACTIVE) {
            throw IllegalArgumentException("Only ACTIVE identifiers can be scheduled for deactivation")
        }

        identifier.scheduleDeactivation(scheduledTime, reason)
        identifierRepository.update(identifier)

        val now = Instant.now()
        val deactivation = IdentifierDeactivation(
            id = null,
            identifierId = identifierId,
            deactType = type,
            status = com.hermes.domain.valueobject.DeactivationStatus.SCHEDULED,
            scheduledTime = scheduledTime,
            createdAt = now,
            updatedAt = now
        )

        return deactivationRepository.insert(deactivation)
    }
}