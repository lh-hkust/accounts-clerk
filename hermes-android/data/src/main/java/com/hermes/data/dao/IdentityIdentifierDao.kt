package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.IdentityIdentifierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentityIdentifierDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: IdentityIdentifierEntity): Long

    @Update
    suspend fun update(entity: IdentityIdentifierEntity)

    @Delete
    suspend fun delete(entity: IdentityIdentifierEntity)

    @Query("SELECT * FROM identity_identifier WHERE id = :id")
    suspend fun getById(id: Long): IdentityIdentifierEntity?

    @Query("SELECT * FROM identity_identifier ORDER BY status, updatedAt DESC")
    suspend fun getAll(): List<IdentityIdentifierEntity>

    @Query("SELECT * FROM identity_identifier ORDER BY status, updatedAt DESC")
    fun getAllFlow(): Flow<List<IdentityIdentifierEntity>>

    @Query("SELECT * FROM identity_identifier WHERE status = :status ORDER BY updatedAt DESC")
    suspend fun getByStatus(status: String): List<IdentityIdentifierEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM identity_identifier WHERE type = :type AND value = :value)")
    suspend fun existsByTypeAndValue(type: String, value: String): Boolean

    @Query("SELECT COUNT(*) FROM identifier_binding WHERE identifierId = :identifierId")
    suspend fun getBoundAccountCount(identifierId: Long): Int

    @Query("UPDATE identity_identifier SET status = :status, plannedDeactTime = :plannedDeactTime, deactReason = :deactReason, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDeactivation(id: Long, status: String, plannedDeactTime: Long?, deactReason: String?, updatedAt: Long)

    @Query("SELECT * FROM identity_identifier WHERE status = 'PENDING_DEACTIVATION' AND plannedDeactTime <= :threshold ORDER BY plannedDeactTime ASC")
    suspend fun getPendingDeactivationBefore(threshold: Long): List<IdentityIdentifierEntity>

    @Query("DELETE FROM identity_identifier")
    suspend fun deleteAll()
}