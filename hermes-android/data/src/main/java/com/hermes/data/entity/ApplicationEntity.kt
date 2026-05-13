package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hermes.domain.model.Application
import com.hermes.domain.valueobject.ApplicationType

/**
 * Room Entity: 应用平台
 */
@Entity(tableName = "application")
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String = "BOTH",
    val officialUrl: String? = null,
    val iconUrl: String? = null,
    val category: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): Application {
        return Application(
            id = id,
            name = name,
            type = ApplicationType.valueOf(type),
            officialUrl = officialUrl,
            iconUrl = iconUrl,
            category = category,
            isActive = isActive,
            createdAt = java.time.Instant.ofEpochMilli(createdAt),
            updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
        )
    }

    companion object {
        fun fromDomainModel(model: Application): ApplicationEntity {
            return ApplicationEntity(
                id = model.id ?: 0,
                name = model.name,
                type = model.type.name,
                officialUrl = model.officialUrl,
                iconUrl = model.iconUrl,
                category = model.category,
                isActive = model.isActive,
                createdAt = model.createdAt.toEpochMilli(),
                updatedAt = model.updatedAt.toEpochMilli()
            )
        }
    }
}