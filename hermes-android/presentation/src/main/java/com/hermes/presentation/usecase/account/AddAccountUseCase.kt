package com.hermes.presentation.usecase.account

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.valueobject.AccountStatus
import java.time.Instant

/**
 * 添加应用账户用例
 */
class AddAccountUseCase(
    private val accountRepository: ApplicationAccountRepository
) {
    /**
     * 添加账户
     *
     * @param applicationId 应用ID
     * @param accountName 账户名称
     * @param accountIdentifier 账户标识（可选）
     * @param nickname 昵称（可选）
     * @return 创建的账户
     * @throws IllegalArgumentException 如果账户标识重复
     */
    suspend operator fun invoke(
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?
    ): ApplicationAccount {
        if (accountName.isBlank()) {
            throw IllegalArgumentException("Account name must not be empty")
        }

        if (accountIdentifier != null && accountRepository.checkDuplicate(applicationId, accountIdentifier)) {
            throw IllegalArgumentException("Account identifier already exists for this application")
        }

        val now = Instant.now()
        val account = ApplicationAccount(
            id = null,
            applicationId = applicationId,
            accountName = accountName,
            accountIdentifier = accountIdentifier,
            nickname = nickname,
            status = AccountStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        return accountRepository.insert(account)
    }
}