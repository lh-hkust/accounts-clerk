package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.valueobject.DeactivationStatus
import com.hermes.domain.valueobject.DeactivationType

/**
 * Room Entity: 停用计划
 */
@Entity(
    tableName = "identifier_deactivation",
    foreignKeys = [
        ForeignKey(
            entity = IdentityIdentifierEntity::class,
            parentColumns = ["id"],
            childColumns = ["identifierId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["identifierId"]),
        Index(value = ["status", "scheduledTime"])
    ]
)
data class IdentifierDeactivationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifierId: Long,
    val deactivationType: String,
    val status: String = "SCHEDULED",
    val scheduledTime: Long,
    val executedTime: Long? = null,
    val cancelledTime: Long? = null,
    val cancelReason: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): IdentifierDeactivation {
        return IdentifierDeactivation(
            id = id,
            identifierId = identifierId,
            deactType = DeactivationType.valueOf(deactivationType),
            status = DeactivationStatus.valueOf(status),
            scheduledTime = java.time.Instant.ofEpochMilli(scheduledTime),
            actualTime = executedTime?.let { java.time.Instant.ofEpochMilli(it) },
            reason = cancelReason,
            createdAt = java.time.Instant.ofEpochMilli(createdAt),
            updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
        )
    }

    companion object {
        fun fromDomainModel(model: IdentifierDeactivation): IdentifierDeactivationEntity {
            return IdentifierDeactivationEntity(
                id = model.id ?: 0,
                identifierId = model.identifierId,
                deactivationType = model.deactType.name,
                status = model.status.name,
                scheduledTime = model.scheduledTime!!.toEpochMilli(),
                executedTime = model.actualTime?.toEpochMilli(),
                cancelReason = model.reason,
                createdAt = model.createdAt.toEpochMilli(),
                updatedAt = model.updatedAt.toEpochMilli()
            )
        }
    }
}