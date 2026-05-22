package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType

/**
 * Room Entity: 预警记录
 */
@Entity(
    tableName = "warning_record",
    foreignKeys = [
        ForeignKey(
            entity = IdentityIdentifierEntity::class,
            parentColumns = ["id"],
            childColumns = ["identifierId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ApplicationAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["warningLevel"]),
        Index(value = ["isHandled", "warningLevel"])
    ]
)
data class WarningRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifierId: Long? = null,
    val accountId: Long? = null,
    val warningType: String,
    val warningLevel: String,
    val message: String,
    val triggeredAt: Long,
    val isRead: Boolean = false,
    val isHandled: Boolean = false,
    val handledAt: Long? = null
) {
    fun toDomainModel(): WarningRecord {
        return WarningRecord(
            id = id,
            identifierId = identifierId,
            accountId = accountId,
            warningType = WarningType.valueOf(warningType),
            warningLevel = WarningLevel.valueOf(warningLevel),
            message = message,
            triggeredAt = java.time.Instant.ofEpochMilli(triggeredAt),
            isRead = isRead,
            isHandled = isHandled,
            handledAt = handledAt?.let { java.time.Instant.ofEpochMilli(it) }
        )
    }

    companion object {
        fun fromDomainModel(model: WarningRecord): WarningRecordEntity {
            return WarningRecordEntity(
                id = model.id ?: 0,
                identifierId = model.identifierId,
                accountId = model.accountId,
                warningType = model.warningType.name,
                warningLevel = model.warningLevel.name,
                message = model.message,
                triggeredAt = model.triggeredAt.toEpochMilli(),
                isRead = model.isRead,
                isHandled = model.isHandled,
                handledAt = model.handledAt?.toEpochMilli()
            )
        }
    }
}