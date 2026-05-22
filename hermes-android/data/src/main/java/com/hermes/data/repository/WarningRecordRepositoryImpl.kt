package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.WarningRecordDao
import com.hermes.data.entity.WarningRecordEntity
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.repository.WarningRecordRepository
import java.time.Instant

class WarningRecordRepositoryImpl @Inject constructor(
    private val dao: WarningRecordDao
) : WarningRecordRepository {

    override suspend fun insert(record: WarningRecord): WarningRecord {
        val entity = WarningRecordEntity.fromDomainModel(record)
        val id = dao.insert(entity)
        return WarningRecord(
            id = id,
            identifierId = record.identifierId,
            accountId = record.accountId,
            warningType = record.warningType,
            warningLevel = record.warningLevel,
            message = record.message,
            triggeredAt = record.triggeredAt,
            isRead = record.isRead,
            isHandled = record.isHandled,
            handledAt = record.handledAt
        )
    }

    override suspend fun update(record: WarningRecord) {
        val entity = WarningRecordEntity.fromDomainModel(record)
        dao.update(entity)
    }

    override suspend fun delete(record: WarningRecord) {
        val entity = WarningRecordEntity.fromDomainModel(record)
        dao.delete(entity)
    }

    override suspend fun getById(id: Long): WarningRecord? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getUnhandled(): List<WarningRecord> {
        return dao.getByHandledStatus(false).map { it.toDomainModel() }
    }

    override suspend fun getHandled(): List<WarningRecord> {
        return dao.getByHandledStatus(true).map { it.toDomainModel() }
    }

    override suspend fun getQuickHandleList(limit: Int): List<WarningRecord> {
        return dao.getUnhandled(limit).map { it.toDomainModel() }
    }

    override suspend fun getByIdentifierId(identifierId: Long): List<WarningRecord> {
        return dao.getByIdentifierId(identifierId).map { it.toDomainModel() }
    }

    override suspend fun deleteByIdentifierId(identifierId: Long) {
        dao.deleteByIdentifierId(identifierId)
    }

    override suspend fun markAsRead(id: Long) {
        dao.markAsRead(id)
    }

    override suspend fun markAsHandled(id: Long) {
        dao.markAsHandled(id, Instant.now().toEpochMilli())
    }

    override suspend fun getUnhandledCount(): Int {
        return dao.getUnhandledCount()
    }
}