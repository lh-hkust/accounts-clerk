package com.hermes.domain.repository

import com.hermes.domain.model.WarningRecord

interface WarningRecordRepository {
    suspend fun insert(record: WarningRecord): WarningRecord
    suspend fun update(record: WarningRecord)
    suspend fun delete(record: WarningRecord)
    suspend fun getById(id: Long): WarningRecord?
    suspend fun getUnhandled(): List<WarningRecord>
    suspend fun getHandled(): List<WarningRecord>
    suspend fun getQuickHandleList(limit: Int): List<WarningRecord>
    suspend fun getByIdentifierId(identifierId: Long): List<WarningRecord>
    suspend fun deleteByIdentifierId(identifierId: Long)
    suspend fun markAsRead(id: Long)
    suspend fun markAsHandled(id: Long)
    suspend fun getUnhandledCount(): Int
}