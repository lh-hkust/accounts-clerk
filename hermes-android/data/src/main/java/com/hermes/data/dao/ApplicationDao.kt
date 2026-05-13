package com.hermes.data.dao

import androidx.room.*
import com.hermes.data.entity.ApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ApplicationEntity): Long

    @Update
    suspend fun update(entity: ApplicationEntity)

    @Delete
    suspend fun delete(entity: ApplicationEntity)

    @Query("SELECT * FROM application WHERE id = :id")
    suspend fun getById(id: Long): ApplicationEntity?

    @Query("SELECT * FROM application WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getAllActive(): List<ApplicationEntity>

    @Query("SELECT * FROM application WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveFlow(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM application WHERE category = :category AND isActive = 1 ORDER BY name ASC")
    suspend fun getByCategory(category: String): List<ApplicationEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM application WHERE name = :name)")
    suspend fun existsByName(name: String): Boolean

    @Query("UPDATE application SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean, updatedAt: Long)
}