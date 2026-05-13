package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.BindingHistoryRecordEntity

@Dao
interface BindingHistoryRecordDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: BindingHistoryRecordEntity): Long

    @Update
    suspend fun update(entity: BindingHistoryRecordEntity)

    @Delete
    suspend fun delete(entity: BindingHistoryRecordEntity)

    @Query("SELECT * FROM binding_history_record WHERE id = :id")
    suspend fun getById(id: Long): BindingHistoryRecordEntity?

    @Query("SELECT * FROM binding_history_record WHERE accountId = :accountId ORDER BY actionAt DESC")
    suspend fun getByAccountId(accountId: Long): List<BindingHistoryRecordEntity>

    @Query("SELECT * FROM binding_history_record WHERE identifierId = :identifierId ORDER BY actionAt DESC")
    suspend fun getByIdentifierId(identifierId: Long): List<BindingHistoryRecordEntity>

    @Query("SELECT * FROM binding_history_record ORDER BY actionAt DESC")
    suspend fun getAll(): List<BindingHistoryRecordEntity>
}