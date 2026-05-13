package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.valueobject.BindingPurpose

/**
 * Room Entity: 标识绑定
 */
@Entity(
    tableName = "identifier_binding",
    foreignKeys = [
        ForeignKey(
            entity = ApplicationAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IdentityIdentifierEntity::class,
            parentColumns = ["id"],
            childColumns = ["identifierId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["identifierId"]),
        Index(value = ["accountId", "identifierId"], unique = true)
    ]
)
data class IdentifierBindingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val identifierId: Long,
    val purposes: String,
    val isPrimary: Boolean = false,
    val boundAt: Long,
    val verifiedAt: Long? = null,
    val notes: String? = null
) {
    fun toDomainModel(): IdentifierBinding {
        return IdentifierBinding(
            id = id,
            accountId = accountId,
            identifierId = identifierId,
            purposes = purposes.split(",").map { BindingPurpose.valueOf(it) },
            isPrimary = isPrimary,
            boundAt = java.time.Instant.ofEpochMilli(boundAt),
            verifiedAt = verifiedAt?.let { java.time.Instant.ofEpochMilli(it) },
            notes = notes
        )
    }

    companion object {
        fun fromDomainModel(model: IdentifierBinding): IdentifierBindingEntity {
            return IdentifierBindingEntity(
                id = model.id ?: 0,
                accountId = model.accountId,
                identifierId = model.identifierId,
                purposes = model.purposes.joinToString(",") { it.name },
                isPrimary = model.isPrimary,
                boundAt = model.boundAt.toEpochMilli(),
                verifiedAt = model.verifiedAt?.toEpochMilli(),
                notes = model.notes
            )
        }
    }
}