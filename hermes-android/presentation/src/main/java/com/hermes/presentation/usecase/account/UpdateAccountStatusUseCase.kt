package com.hermes.presentation.usecase.account

import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.valueobject.AccountStatus

/**
 * 更新账户状态用例
 */
class UpdateAccountStatusUseCase(
    private val accountRepository: ApplicationAccountRepository
) {
    /**
     * 更新账户状态
     *
     * @param accountId 账户ID
     * @param newStatus 新状态
     * @throws IllegalArgumentException 如果状态转换非法
     */
    suspend operator fun invoke(accountId: Long, newStatus: AccountStatus) {
        val account = accountRepository.getById(accountId)
            ?: throw IllegalArgumentException("Account not found")

        account.updateStatus(newStatus)
        accountRepository.update(account)
    }
}