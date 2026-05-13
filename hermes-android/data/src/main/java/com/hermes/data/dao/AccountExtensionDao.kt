package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.AccountExtensionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountExtensionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: AccountExtensionEntity): Long

    @Update
    suspend fun update(entity: AccountExtensionEntity)

    @Delete
    suspend fun delete(entity: AccountExtensionEntity)

    @Query("SELECT * FROM account_extension WHERE id = :id")
    suspend fun getById(id: Long): AccountExtensionEntity?

    @Query("SELECT * FROM account_extension WHERE accountId = :accountId")
    suspend fun getByAccountId(accountId: Long): List<AccountExtensionEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM account_extension WHERE accountId = :accountId AND key = :key)")
    suspend fun existsByAccountAndKey(accountId: Long, key: String): Boolean

    @Query("DELETE FROM account_extension WHERE accountId = :accountId AND key = :key")
    suspend fun deleteByAccountAndKey(accountId: Long, key: String)

    @Query("UPDATE account_extension SET value = :value, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateValue(id: Long, value: String?, updatedAt: Long)
}