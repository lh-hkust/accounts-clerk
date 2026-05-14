package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.IdentifierBindingDao
import com.hermes.data.entity.IdentifierBindingEntity
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant

class IdentifierBindingRepositoryImpl @Inject constructor(
    private val dao: IdentifierBindingDao
) : IdentifierBindingRepository {

    override suspend fun insert(binding: IdentifierBinding): IdentifierBinding {
        val entity = IdentifierBindingEntity.fromDomainModel(binding)
        val id = dao.insert(entity)
        return IdentifierBinding(
            id = id,
            accountId = binding.accountId,
            identifierId = binding.identifierId,
            purposes = binding.purposes,
            isPrimary = binding.isPrimary,
            boundAt = binding.boundAt,
            verifiedAt = binding.verifiedAt,
            notes = binding.notes
        )
    }

    override suspend fun update(binding: IdentifierBinding) {
        val entity = IdentifierBindingEntity.fromDomainModel(binding)
        dao.update(entity)
    }

    override suspend fun delete(binding: IdentifierBinding) {
        val entity = IdentifierBindingEntity.fromDomainModel(binding)
        dao.delete(entity)
    }

    override suspend fun getById(id: Long): IdentifierBinding? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getByAccountId(accountId: Long): List<IdentifierBinding> {
        return dao.getByAccountId(accountId).map { it.toDomainModel() }
    }

    override suspend fun getByIdentifierId(identifierId: Long): List<IdentifierBinding> {
        return dao.getByIdentifierId(identifierId).map { it.toDomainModel() }
    }

    override suspend fun checkDuplicate(accountId: Long, identifierId: Long): Boolean {
        return dao.existsByAccountAndIdentifier(accountId, identifierId)
    }

    override suspend fun getCountByIdentifierId(identifierId: Long): Int {
        return dao.getCountByIdentifierId(identifierId)
    }

    override suspend fun deleteByAccountAndIdentifier(accountId: Long, identifierId: Long) {
        dao.deleteByAccountAndIdentifier(accountId, identifierId)
    }

    override suspend fun updatePurposes(id: Long, purposes: List<BindingPurpose>) {
        dao.updatePurposes(id, purposes.joinToString(",") { it.name })
    }

    override suspend fun switchIdentifier(accountId: Long, oldIdentifierId: Long, newIdentifierId: Long) {
        dao.switchIdentifier(accountId, oldIdentifierId, newIdentifierId)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}