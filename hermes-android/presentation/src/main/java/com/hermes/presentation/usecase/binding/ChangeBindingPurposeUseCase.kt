package com.hermes.presentation.usecase.binding

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant

/**
 * 修改绑定用途用例
 */
class ChangeBindingPurposeUseCase(
    private val bindingRepository: IdentifierBindingRepository,
    private val historyRepository: BindingHistoryRepository
) {
    /**
     * 修改绑定用途
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @param newPurposes 新用途列表
     * @return 历史记录
     * @throws IllegalArgumentException 如果新用途列表为空
     */
    suspend operator fun invoke(
        accountId: Long,
        identifierId: Long,
        newPurposes: List<BindingPurpose>
    ): BindingHistoryRecord {
        if (newPurposes.isEmpty()) {
            throw IllegalArgumentException("Binding purposes must not be empty")
        }

        val binding = bindingRepository.getByAccountId(accountId)
            .find { it.identifierId == identifierId }
            ?: throw IllegalArgumentException("Binding not found")

        val previousPurposes = binding.purposes

        bindingRepository.updatePurposes(binding.id!!, newPurposes)

        val history = BindingHistoryRecord(
            id = null,
            accountId = accountId,
            identifierId = identifierId,
            actionType = ActionType.CHANGE_PURPOSE,
            previousPurposes = previousPurposes,
            newPurposes = newPurposes,
            actionAt = Instant.now()
        )

        return historyRepository.insert(history)
    }
}