package com.hermes.data.repository

import com.hermes.data.dao.IdentityIdentifierDao
import com.hermes.data.entity.IdentityIdentifierEntity
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.valueobject.IdentifierType
import dagger.assisted.AssistedInject
import java.time.Instant
import javax.inject.Inject

class IdentityIdentifierRepositoryImpl @Inject constructor(
    private val dao: IdentityIdentifierDao
) : IdentityIdentifierRepository {

    override suspend fun insert(identifier: IdentityIdentifier): IdentityIdentifier {
        val entity = IdentityIdentifierEntity.fromDomainModel(identifier)
        val id = dao.insert(entity)
        return IdentityIdentifier(
            id = id,
            type = identifier.type,
            value = identifier.value,
            status = identifier.status,
            plannedDeactTime = identifier.plannedDeactTime,
            deactReason = identifier.deactReason,
            createdAt = identifier.createdAt,
            updatedAt = identifier.updatedAt
        )
    }

    override suspend fun update(identifier: IdentityIdentifier) {
        val entity = IdentityIdentifierEntity.fromDomainModel(identifier)
        dao.update(entity)
    }

    override suspend fun delete(identifier: IdentityIdentifier) {
        val entity = IdentityIdentifierEntity.fromDomainModel(identifier)
        dao.delete(entity)
    }

    override suspend fun getById(id: Long): IdentityIdentifier? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getAll(): List<IdentityIdentifier> {
        return dao.getAll().map { it.toDomainModel() }
    }

    override suspend fun getByStatus(status: com.hermes.domain.valueobject.IdentifierStatus): List<IdentityIdentifier> {
        return dao.getByStatus(status.name).map { it.toDomainModel() }
    }

    override suspend fun checkDuplicate(type: IdentifierType, value: String): Boolean {
        return dao.existsByTypeAndValue(type.name, value)
    }

    override suspend fun getBoundAccountCount(identifierId: Long): Int {
        return dao.getBoundAccountCount(identifierId)
    }

    override suspend fun getPendingDeactivationBefore(threshold: Instant): List<IdentityIdentifier> {
        return dao.getPendingDeactivationBefore(threshold.toEpochMilli()).map { it.toDomainModel() }
    }
}