package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.WarningRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarningRecordDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: WarningRecordEntity): Long

    @Update
    suspend fun update(entity: WarningRecordEntity)

    @Delete
    suspend fun delete(entity: WarningRecordEntity)

    @Query("SELECT * FROM warning_record WHERE id = :id")
    suspend fun getById(id: Long): WarningRecordEntity?

    @Query("SELECT * FROM warning_record WHERE isHandled = :isHandled ORDER BY warningLevel ASC, triggeredAt DESC")
    suspend fun getByHandledStatus(isHandled: Boolean): List<WarningRecordEntity>

    @Query("SELECT * FROM warning_record WHERE isHandled = 0 ORDER BY warningLevel ASC, triggeredAt DESC LIMIT :limit")
    suspend fun getUnhandled(limit: Int): List<WarningRecordEntity>

    @Query("SELECT * FROM warning_record WHERE identifierId = :identifierId")
    suspend fun getByIdentifierId(identifierId: Long): List<WarningRecordEntity>

    @Query("DELETE FROM warning_record WHERE identifierId = :identifierId")
    suspend fun deleteByIdentifierId(identifierId: Long)

    @Query("UPDATE warning_record SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE warning_record SET isHandled = 1, handledAt = :handledAt WHERE id = :id")
    suspend fun markAsHandled(id: Long, handledAt: Long)

    @Query("SELECT COUNT(*) FROM warning_record WHERE isHandled = 0")
    suspend fun getUnhandledCount(): Int
}