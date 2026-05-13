package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.BindingHistoryRecordDao
import com.hermes.data.entity.BindingHistoryRecordEntity
import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.repository.BindingHistoryRepository

class BindingHistoryRepositoryImpl @Inject constructor(
    private val dao: BindingHistoryRecordDao
) : BindingHistoryRepository {

    override suspend fun insert(record: BindingHistoryRecord): BindingHistoryRecord {
        val entity = BindingHistoryRecordEntity.fromDomainModel(record)
        val id = dao.insert(entity)
        return BindingHistoryRecord(
            id = id,
            accountId = record.accountId,
            identifierId = record.identifierId,
            actionType = record.actionType,
            previousPurposes = record.previousPurposes,
            newPurposes = record.newPurposes,
            previousIdentifierId = record.previousIdentifierId,
            newIdentifierId = record.newIdentifierId,
            actionAt = record.actionAt,
            actionBy = record.actionBy,
            notes = record.notes
        )
    }

    override suspend fun getById(id: Long): BindingHistoryRecord? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getByAccountId(accountId: Long): List<BindingHistoryRecord> {
        return dao.getByAccountId(accountId).map { it.toDomainModel() }
    }

    override suspend fun getByIdentifierId(identifierId: Long): List<BindingHistoryRecord> {
        return dao.getByIdentifierId(identifierId).map { it.toDomainModel() }
    }

    override suspend fun getAll(): List<BindingHistoryRecord> {
        return dao.getAll().map { it.toDomainModel() }
    }
}