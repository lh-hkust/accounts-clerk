package com.hermes.domain.repository

import com.hermes.domain.model.Application

interface ApplicationRepository {
    suspend fun insert(application: Application): Application
    suspend fun update(application: Application)
    suspend fun delete(application: Application)
    suspend fun getById(id: Long): Application?
    suspend fun getAllActive(): List<Application>
    suspend fun getAll(): List<Application>
    suspend fun getByCategory(category: String): List<Application>
    suspend fun checkDuplicate(name: String): Boolean
    suspend fun deactivate(id: Long)
    suspend fun activate(id: Long)
}