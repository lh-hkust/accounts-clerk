package com.hermes.presentation.usecase.binding

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose
import java.time.Instant

/**
 * 绑定标识用例
 */
class BindIdentifierUseCase(
    private val bindingRepository: IdentifierBindingRepository,
    private val historyRepository: BindingHistoryRepository
) {
    /**
     * 绑定标识到账户
     *
     * @param accountId 账户ID
     * @param identifierId 标识ID
     * @param purposes 绑定用途列表
     * @param isPrimary 是否为主要标识
     * @return 创建的绑定实体
     * @throws IllegalArgumentException 如果绑定已存在或用途列表为空
     */
    suspend operator fun invoke(
        accountId: Long,
        identifierId: Long,
        purposes: List<BindingPurpose>,
        isPrimary: Boolean = false
    ): IdentifierBinding {
        if (purposes.isEmpty()) {
            throw IllegalArgumentException("Binding purposes must not be empty")
        }

        if (bindingRepository.checkDuplicate(accountId, identifierId)) {
            throw IllegalArgumentException("Identifier already bound to this account")
        }

        val now = Instant.now()
        val binding = IdentifierBinding(
            id = null,
            accountId = accountId,
            identifierId = identifierId,
            purposes = purposes,
            isPrimary = isPrimary,
            boundAt = now
        )

        val savedBinding = bindingRepository.insert(binding)

        createHistoryRecord(accountId, identifierId, ActionType.BIND, null, purposes)

        return savedBinding
    }

    private suspend fun createHistoryRecord(
        accountId: Long,
        identifierId: Long,
        actionType: ActionType,
        previousPurposes: List<BindingPurpose>?,
        newPurposes: List<BindingPurpose>?
    ) {
        val history = BindingHistoryRecord(
            id = null,
            accountId = accountId,
            identifierId = identifierId,
            actionType = actionType,
            previousPurposes = previousPurposes,
            newPurposes = newPurposes,
            actionAt = Instant.now()
        )
        historyRepository.insert(history)
    }
}