package com.hermes.domain.model

import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant

/**
 * 绑定历史聚合根
 * BindingHistoryRecord - 标识绑定关系的历史变更记录
 *
 * @see aggregates.md 六、绑定历史聚合
 */
class BindingHistoryRecord(
    val id: Long?,
    val accountId: Long,
    val identifierId: Long,
    val actionType: ActionType,
    var previousPurposes: List<BindingPurpose>? = null,
    var newPurposes: List<BindingPurpose>? = null,
    var previousIdentifierId: Long? = null,
    var newIdentifierId: Long? = null,
    val actionAt: Instant,
    var actionBy: String? = null,
    var notes: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BindingHistoryRecord) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}