package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.IdentifierDeactivationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentifierDeactivationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: IdentifierDeactivationEntity): Long

    @Update
    suspend fun update(entity: IdentifierDeactivationEntity)

    @Delete
    suspend fun delete(entity: IdentifierDeactivationEntity)

    @Query("SELECT * FROM identifier_deactivation WHERE id = :id")
    suspend fun getById(id: Long): IdentifierDeactivationEntity?

    @Query("SELECT * FROM identifier_deactivation WHERE identifierId = :identifierId")
    suspend fun getByIdentifierId(identifierId: Long): IdentifierDeactivationEntity?

    @Query("SELECT * FROM identifier_deactivation WHERE status = :status ORDER BY scheduledTime ASC")
    suspend fun getByStatus(status: String): List<IdentifierDeactivationEntity>

    @Query("SELECT * FROM identifier_deactivation WHERE status = 'SCHEDULED' AND scheduledTime <= :threshold ORDER BY scheduledTime ASC")
    suspend fun getScheduledBefore(threshold: Long): List<IdentifierDeactivationEntity>

    @Query("SELECT * FROM identifier_deactivation WHERE status = 'SCHEDULED' AND scheduledTime BETWEEN :start AND :end ORDER BY scheduledTime ASC")
    suspend fun getScheduledBetween(start: Long, end: Long): List<IdentifierDeactivationEntity>

    @Query("UPDATE identifier_deactivation SET status = :status, executedTime = :executedTime, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateExecuted(id: Long, status: String, executedTime: Long?, updatedAt: Long)

    @Query("UPDATE identifier_deactivation SET status = :status, cancelledTime = :cancelledTime, cancelReason = :cancelReason, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCancelled(id: Long, status: String, cancelledTime: Long?, cancelReason: String?, updatedAt: Long)

    @Query("UPDATE identifier_deactivation SET scheduledTime = :scheduledTime, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateScheduledTime(id: Long, scheduledTime: Long, updatedAt: Long)
}