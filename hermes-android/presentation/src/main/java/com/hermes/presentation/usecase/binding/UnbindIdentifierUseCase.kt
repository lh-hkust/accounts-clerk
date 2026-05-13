package com.hermes.presentation.usecase.binding

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.domain.valueobject.ActionType
import java.time.Instant

/**
 * 解绑标识用例
 */
class UnbindIdentifierUseCase(
    private val bindingRepository: IdentifierBindingRepository,
    private val historyRepository: BindingHistoryRepository
) {
    /**
     * 解绑标识
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @return 历史记录
     */
    suspend operator fun invoke(accountId: Long, identifierId: Long): BindingHistoryRecord {
        val binding = bindingRepository.getByAccountId(accountId)
            .find { it.identifierId == identifierId }
            ?: throw IllegalArgumentException("Binding not found")

        val previousPurposes = binding.purposes

        bindingRepository.deleteByAccountAndIdentifier(accountId, identifierId)

        val history = BindingHistoryRecord(
            id = null,
            accountId = accountId,
            identifierId = identifierId,
            actionType = ActionType.UNBIND,
            previousPurposes = previousPurposes,
            newPurposes = null,
            actionAt = Instant.now()
        )

        return historyRepository.insert(history)
    }
}