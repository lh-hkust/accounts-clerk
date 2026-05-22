package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.valueobject.AccountStatus

/**
 * Room Entity: 应用账户
 */
@Entity(
    tableName = "application_account",
    foreignKeys = [
        ForeignKey(
            entity = ApplicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["applicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["applicationId"]),
        Index(value = ["status"]),
        Index(value = ["applicationId", "accountIdentifier"], unique = true)
    ]
)
data class ApplicationAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val applicationId: Long,
    val accountName: String,
    val accountIdentifier: String? = null,
    val nickname: String? = null,
    val status: String = "ACTIVE",
    val keepAliveEnabled: Boolean = true,
    val lastLoginDate: Long? = null,
    val notes: String? = null,
    val tags: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): ApplicationAccount {
        return ApplicationAccount(
            id = id,
            applicationId = applicationId,
            accountName = accountName,
            accountIdentifier = accountIdentifier,
            nickname = nickname,
            status = AccountStatus.valueOf(status),
            keepAliveEnabled = keepAliveEnabled,
            lastLoginDate = lastLoginDate?.let { java.time.LocalDate.ofEpochDay(it) },
            notes = notes,
            tags = tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            createdAt = java.time.Instant.ofEpochMilli(createdAt),
            updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
        )
    }

    companion object {
        fun fromDomainModel(model: ApplicationAccount): ApplicationAccountEntity {
            return ApplicationAccountEntity(
                id = model.id ?: 0,
                applicationId = model.applicationId,
                accountName = model.accountName,
                accountIdentifier = model.accountIdentifier,
                nickname = model.nickname,
                status = model.status.name,
                keepAliveEnabled = model.keepAliveEnabled,
                lastLoginDate = model.lastLoginDate?.toEpochDay(),
                notes = model.notes,
                tags = model.tags.joinToString(","),
                createdAt = model.createdAt.toEpochMilli(),
                updatedAt = model.updatedAt.toEpochMilli()
            )
        }
    }
}