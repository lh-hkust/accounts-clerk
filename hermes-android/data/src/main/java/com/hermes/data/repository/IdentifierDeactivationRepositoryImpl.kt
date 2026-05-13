package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.IdentifierDeactivationDao
import com.hermes.data.entity.IdentifierDeactivationEntity
import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.valueobject.DeactivationStatus
import java.time.Instant

class IdentifierDeactivationRepositoryImpl @Inject constructor(
    private val dao: IdentifierDeactivationDao
) : IdentifierDeactivationRepository {

    override suspend fun insert(deactivation: IdentifierDeactivation): IdentifierDeactivation {
        val entity = IdentifierDeactivationEntity.fromDomainModel(deactivation)
        val id = dao.insert(entity)
        return IdentifierDeactivation(
            id = id,
            identifierId = deactivation.identifierId,
            deactType = deactivation.deactType,
            status = deactivation.status,
            scheduledTime = deactivation.scheduledTime,
            actualTime = deactivation.actualTime,
            reason = deactivation.reason,
            cancelReason = deactivation.cancelReason,
            createdAt = deactivation.createdAt,
            updatedAt = deactivation.updatedAt
        )
    }

    override suspend fun update(deactivation: IdentifierDeactivation) {
        val entity = IdentifierDeactivationEntity.fromDomainModel(deactivation)
        dao.update(entity)
    }

    override suspend fun delete(deactivation: IdentifierDeactivation) {
        val entity = IdentifierDeactivationEntity.fromDomainModel(deactivation)
        dao.delete(entity)
    }

    override suspend fun getById(id: Long): IdentifierDeactivation? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getByIdentifierId(identifierId: Long): IdentifierDeactivation? {
        return dao.getByIdentifierId(identifierId)?.toDomainModel()
    }

    override suspend fun getByStatus(status: DeactivationStatus): List<IdentifierDeactivation> {
        return dao.getByStatus(status.name).map { it.toDomainModel() }
    }

    override suspend fun getScheduledBefore(threshold: Instant): List<IdentifierDeactivation> {
        return dao.getScheduledBefore(threshold.toEpochMilli()).map { it.toDomainModel() }
    }

    override suspend fun getScheduledBetween(start: Instant, end: Instant): List<IdentifierDeactivation> {
        return dao.getScheduledBetween(start.toEpochMilli(), end.toEpochMilli()).map { it.toDomainModel() }
    }

    override suspend fun markExecuted(id: Long) {
        dao.updateExecuted(id, DeactivationStatus.EXECUTED.name, Instant.now().toEpochMilli(), Instant.now().toEpochMilli())
    }

    override suspend fun markCancelled(id: Long, reason: String) {
        dao.updateCancelled(id, DeactivationStatus.CANCELLED.name, Instant.now().toEpochMilli(), reason, Instant.now().toEpochMilli())
    }

    override suspend fun updateScheduledTime(id: Long, newTime: Instant) {
        dao.updateScheduledTime(id, newTime.toEpochMilli(), Instant.now().toEpochMilli())
    }
}