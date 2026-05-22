package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.AccountExtension
import com.hermes.domain.valueobject.FieldType

/**
 * Room Entity: 账户扩展
 */
@Entity(
    tableName = "account_extension",
    foreignKeys = [
        ForeignKey(
            entity = ApplicationAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["accountId", "key"], unique = true)
    ]
)
data class AccountExtensionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val key: String,
    val value: String? = null,
    val label: String,
    val fieldType: String,
    val options: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomainModel(): AccountExtension {
        return AccountExtension(
            id = id,
            accountId = accountId,
            key = key,
            value = value,
            label = label,
            fieldType = FieldType.valueOf(fieldType),
            options = options?.split(",")?.filter { it.isNotBlank() },
            createdAt = java.time.Instant.ofEpochMilli(createdAt),
            updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
        )
    }

    companion object {
        fun fromDomainModel(model: AccountExtension): AccountExtensionEntity {
            return AccountExtensionEntity(
                id = model.id ?: 0,
                accountId = model.accountId,
                key = model.key,
                value = model.value,
                label = model.label,
                fieldType = model.fieldType.name,
                options = model.options?.joinToString(","),
                createdAt = model.createdAt.toEpochMilli(),
                updatedAt = model.updatedAt.toEpochMilli()
            )
        }
    }
}