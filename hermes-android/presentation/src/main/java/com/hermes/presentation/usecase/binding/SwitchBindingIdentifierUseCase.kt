package com.hermes.presentation.usecase.binding

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.domain.valueobject.ActionType
import java.time.Instant

/**
 * 更换绑定标识用例
 */
class SwitchBindingIdentifierUseCase(
    private val bindingRepository: IdentifierBindingRepository,
    private val historyRepository: BindingHistoryRepository
) {
    /**
     * 更换绑定标识
     *
     * @param accountId 账户ID
     * @param oldIdentifierId 原标识ID
     * @param newIdentifierId 新标识ID
     * @return 历史记录
     */
    suspend operator fun invoke(
        accountId: Long,
        oldIdentifierId: Long,
        newIdentifierId: Long
    ): BindingHistoryRecord {
        val binding = bindingRepository.getByAccountId(accountId)
            .find { it.identifierId == oldIdentifierId }
            ?: throw IllegalArgumentException("Binding not found")

        if (bindingRepository.checkDuplicate(accountId, newIdentifierId)) {
            throw IllegalArgumentException("New identifier already bound to this account")
        }

        bindingRepository.switchIdentifier(accountId, oldIdentifierId, newIdentifierId)

        val history = BindingHistoryRecord(
            id = null,
            accountId = accountId,
            identifierId = newIdentifierId,
            actionType = ActionType.SWITCH_IDENTIFIER,
            previousIdentifierId = oldIdentifierId,
            newIdentifierId = newIdentifierId,
            actionAt = Instant.now()
        )

        return historyRepository.insert(history)
    }
}