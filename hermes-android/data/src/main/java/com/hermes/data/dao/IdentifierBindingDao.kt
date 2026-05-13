package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.IdentifierBindingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdentifierBindingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: IdentifierBindingEntity): Long

    @Update
    suspend fun update(entity: IdentifierBindingEntity)

    @Delete
    suspend fun delete(entity: IdentifierBindingEntity)

    @Query("SELECT * FROM identifier_binding WHERE id = :id")
    suspend fun getById(id: Long): IdentifierBindingEntity?

    @Query("SELECT * FROM identifier_binding WHERE accountId = :accountId")
    suspend fun getByAccountId(accountId: Long): List<IdentifierBindingEntity>

    @Query("SELECT * FROM identifier_binding WHERE identifierId = :identifierId")
    suspend fun getByIdentifierId(identifierId: Long): List<IdentifierBindingEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM identifier_binding WHERE accountId = :accountId AND identifierId = :identifierId)")
    suspend fun existsByAccountAndIdentifier(accountId: Long, identifierId: Long): Boolean

    @Query("SELECT COUNT(*) FROM identifier_binding WHERE identifierId = :identifierId")
    suspend fun getCountByIdentifierId(identifierId: Long): Int

    @Query("DELETE FROM identifier_binding WHERE accountId = :accountId AND identifierId = :identifierId")
    suspend fun deleteByAccountAndIdentifier(accountId: Long, identifierId: Long)

    @Query("UPDATE identifier_binding SET purposes = :purposes WHERE id = :id")
    suspend fun updatePurposes(id: Long, purposes: String)

    @Query("UPDATE identifier_binding SET identifierId = :newIdentifierId WHERE accountId = :accountId AND identifierId = :oldIdentifierId")
    suspend fun switchIdentifier(accountId: Long, oldIdentifierId: Long, newIdentifierId: Long)
}