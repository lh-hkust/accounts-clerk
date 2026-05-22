package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType

/**
 * Room Entity: 身份标识
 */
@Entity(
    tableName = "identity_identifier",
    indices = [
        Index(value = ["status"]),
        Index(value = ["type", "value"], unique = true)
    ]
)
data class IdentityIdentifierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val value: String,
    val status: String = "ACTIVE",
    val plannedDeactTime: Long? = null,
    val deactReason: String? = null,
    val remark: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): IdentityIdentifier {
        return IdentityIdentifier(
            id = id,
            type = IdentifierType.valueOf(type),
            value = value,
            status = IdentifierStatus.valueOf(status),
            plannedDeactTime = plannedDeactTime?.let { java.time.Instant.ofEpochMilli(it) },
            deactReason = deactReason,
            remark = remark,
            createdAt = java.time.Instant.ofEpochMilli(createdAt),
            updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
        )
    }

    companion object {
        fun fromDomainModel(model: IdentityIdentifier): IdentityIdentifierEntity {
            return IdentityIdentifierEntity(
                id = model.id ?: 0,
                type = model.type.name,
                value = model.value,
                status = model.status.name,
                plannedDeactTime = model.plannedDeactTime?.toEpochMilli(),
                deactReason = model.deactReason,
                remark = model.remark,
                createdAt = model.createdAt.toEpochMilli(),
                updatedAt = model.updatedAt.toEpochMilli()
            )
        }
    }
}