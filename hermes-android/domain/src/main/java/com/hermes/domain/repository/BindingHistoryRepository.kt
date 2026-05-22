package com.hermes.domain.repository

import com.hermes.domain.model.BindingHistoryRecord

interface BindingHistoryRepository {
    suspend fun insert(record: BindingHistoryRecord): BindingHistoryRecord
    suspend fun getById(id: Long): BindingHistoryRecord?
    suspend fun getByAccountId(accountId: Long): List<BindingHistoryRecord>
    suspend fun getByIdentifierId(identifierId: Long): List<BindingHistoryRecord>
    suspend fun getAll(): List<BindingHistoryRecord>
}