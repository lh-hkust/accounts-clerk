package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.ApplicationDao
import com.hermes.data.entity.ApplicationEntity
import com.hermes.domain.model.Application
import com.hermes.domain.repository.ApplicationRepository
import java.time.Instant

class ApplicationRepositoryImpl @Inject constructor(
    private val dao: ApplicationDao
) : ApplicationRepository {

    override suspend fun insert(application: Application): Application {
        val entity = ApplicationEntity.fromDomainModel(application)
        val id = dao.insert(entity)
        return Application(
            id = id,
            name = application.name,
            type = application.type,
            officialUrl = application.officialUrl,
            iconUrl = application.iconUrl,
            category = application.category,
            isActive = application.isActive,
            createdAt = application.createdAt,
            updatedAt = application.updatedAt
        )
    }

    override suspend fun update(application: Application) {
        val entity = ApplicationEntity.fromDomainModel(application)
        dao.update(entity)
    }

    override suspend fun delete(application: Application) {
        val entity = ApplicationEntity.fromDomainModel(application)
        dao.delete(entity)
    }

    override suspend fun getById(id: Long): Application? {
        return dao.getById(id)?.toDomainModel()
    }

    override suspend fun getAllActive(): List<Application> {
        return dao.getAllActive().map { it.toDomainModel() }
    }

    override suspend fun getByCategory(category: String): List<Application> {
        return dao.getByCategory(category).map { it.toDomainModel() }
    }

    override suspend fun checkDuplicate(name: String): Boolean {
        return dao.existsByName(name)
    }

    override suspend fun deactivate(id: Long) {
        dao.updateActiveStatus(id, false, Instant.now().toEpochMilli())
    }

    override suspend fun activate(id: Long) {
        dao.updateActiveStatus(id, true, Instant.now().toEpochMilli())
    }

    override suspend fun getAll(): List<Application> {
        return dao.getAll().map { app -> app.toDomainModel() }
    }
}