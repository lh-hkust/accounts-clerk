package com.hermes.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose

/**
 * Room Entity: 绑定历史记录
 */
@Entity(
    tableName = "binding_history_record",
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
        Index(value = ["identifierId"])
    ]
)
data class BindingHistoryRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val identifierId: Long,
    val actionType: String,
    val previousPurposes: String? = null,
    val newPurposes: String? = null,
    val previousIdentifierId: Long? = null,
    val newIdentifierId: Long? = null,
    val actionAt: Long,
    val actionBy: String? = null,
    val notes: String? = null
) {
    fun toDomainModel(): BindingHistoryRecord {
        return BindingHistoryRecord(
            id = id,
            accountId = accountId,
            identifierId = identifierId,
            actionType = ActionType.valueOf(actionType),
            previousPurposes = previousPurposes?.split(",")?.map { BindingPurpose.valueOf(it) },
            newPurposes = newPurposes?.split(",")?.map { BindingPurpose.valueOf(it) },
            previousIdentifierId = previousIdentifierId,
            newIdentifierId = newIdentifierId,
            actionAt = java.time.Instant.ofEpochMilli(actionAt),
            actionBy = actionBy,
            notes = notes
        )
    }

    companion object {
        fun fromDomainModel(model: BindingHistoryRecord): BindingHistoryRecordEntity {
            return BindingHistoryRecordEntity(
                id = model.id ?: 0,
                accountId = model.accountId,
                identifierId = model.identifierId,
                actionType = model.actionType.name,
                previousPurposes = model.previousPurposes?.joinToString(",") { it.name },
                newPurposes = model.newPurposes?.joinToString(",") { it.name },
                previousIdentifierId = model.previousIdentifierId,
                newIdentifierId = model.newIdentifierId,
                actionAt = model.actionAt.toEpochMilli(),
                actionBy = model.actionBy,
                notes = model.notes
            )
        }
    }
}