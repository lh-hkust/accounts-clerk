package com.hermes.presentation.usecase.account

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository

/**
 * 获取账户列表用例
 */
class GetAccountListUseCase(
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 获取所有账户列表（按应用分组）
     *
     * @return 账户列表项
     */
    suspend operator fun invoke(): List<AccountListItem> {
        val accounts = accountRepository.getAll()
        return accounts.map { account ->
            val application = applicationRepository.getById(account.applicationId)
            AccountListItem(
                account = account,
                applicationName = application?.name ?: "",
                applicationIconUrl = application?.iconUrl,
                applicationCategory = application?.category
            )
        }
    }

    /**
     * 按应用获取账户列表
     *
     * @param applicationId 应用ID
     * @return 账户列表项
     */
    suspend fun getByApplication(applicationId: Long): List<AccountListItem> {
        val accounts = accountRepository.getByApplicationId(applicationId)
        val application = applicationRepository.getById(applicationId)
        return accounts.map { account ->
            AccountListItem(
                account = account,
                applicationName = application?.name ?: "",
                applicationIconUrl = application?.iconUrl,
                applicationCategory = application?.category
            )
        }
    }

    /**
     * 按状态获取账户列表
     *
     * @param status 账户状态
     * @return 账户列表项
     */
    suspend fun getByStatus(status: com.hermes.domain.valueobject.AccountStatus): List<AccountListItem> {
        val accounts = accountRepository.getByStatus(status)
        return accounts.map { account ->
            val application = applicationRepository.getById(account.applicationId)
            AccountListItem(
                account = account,
                applicationName = application?.name ?: "",
                applicationIconUrl = application?.iconUrl,
                applicationCategory = application?.category
            )
        }
    }
}

/**
 * 账户列表项
 */
data class AccountListItem(
    val account: ApplicationAccount,
    val applicationName: String,
    val applicationIconUrl: String?,
    val applicationCategory: String?
)