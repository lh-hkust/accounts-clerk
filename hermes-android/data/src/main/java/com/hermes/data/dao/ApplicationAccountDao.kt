package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.ApplicationAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationAccountDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ApplicationAccountEntity): Long

    @Update
    suspend fun update(entity: ApplicationAccountEntity)

    @Delete
    suspend fun delete(entity: ApplicationAccountEntity)

    @Query("SELECT * FROM application_account WHERE id = :id")
    suspend fun getById(id: Long): ApplicationAccountEntity?

    @Query("SELECT * FROM application_account ORDER BY applicationId, updatedAt DESC")
    suspend fun getAll(): List<ApplicationAccountEntity>

    @Query("SELECT * FROM application_account ORDER BY applicationId, updatedAt DESC")
    fun getAllFlow(): Flow<List<ApplicationAccountEntity>>

    @Query("SELECT * FROM application_account WHERE applicationId = :applicationId ORDER BY updatedAt DESC")
    suspend fun getByApplicationId(applicationId: Long): List<ApplicationAccountEntity>

    @Query("SELECT * FROM application_account WHERE status = :status ORDER BY applicationId, updatedAt DESC")
    suspend fun getByStatus(status: String): List<ApplicationAccountEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM application_account WHERE applicationId = :applicationId AND accountIdentifier = :accountIdentifier)")
    suspend fun existsByApplicationAndIdentifier(applicationId: Long, accountIdentifier: String): Boolean

    @Query("SELECT a.* FROM application_account a INNER JOIN identifier_binding b ON a.id = b.accountId WHERE b.identifierId = :identifierId")
    suspend fun getByIdentifierId(identifierId: Long): List<ApplicationAccountEntity>

    @Query("UPDATE application_account SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)
}