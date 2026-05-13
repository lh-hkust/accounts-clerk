package com.hermes.presentation.usecase.account

import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.IdentityIdentifierRepository

/**
 * 获取账户详情用例
 */
class GetAccountDetailUseCase(
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val identifierRepository: IdentityIdentifierRepository
) {
    /**
     * 获取账户详情（包含绑定的标识列表）
     *
     * @param accountId 账户ID
     * @return 账户详情（如果存在）
     */
    suspend operator fun invoke(accountId: Long): AccountDetail? {
        val account = accountRepository.getById(accountId)
            ?: return null

        val application = applicationRepository.getById(account.applicationId)
        val bindings = bindingRepository.getByAccountId(accountId)
        val boundIdentifiers = bindings.map { binding ->
            val identifier = identifierRepository.getById(binding.identifierId)
            BoundIdentifierInfo(
                identifierId = binding.identifierId,
                identifierType = identifier?.type ?: com.hermes.domain.valueobject.IdentifierType.PHONE,
                identifierValue = identifier?.value ?: "",
                purposes = binding.purposes,
                isPrimary = binding.isPrimary
            )
        }

        return AccountDetail(
            account = account,
            applicationName = application?.name ?: "",
            applicationIconUrl = application?.iconUrl,
            boundIdentifiers = boundIdentifiers,
            boundIdentifierCount = bindings.size
        )
    }
}

/**
 * 账户详情
 */
data class AccountDetail(
    val account: com.hermes.domain.model.ApplicationAccount,
    val applicationName: String,
    val applicationIconUrl: String?,
    val boundIdentifiers: List<BoundIdentifierInfo>,
    val boundIdentifierCount: Int
)

/**
 * 绑定标识信息
 */
data class BoundIdentifierInfo(
    val identifierId: Long,
    val identifierType: com.hermes.domain.valueobject.IdentifierType,
    val identifierValue: String,
    val purposes: List<com.hermes.domain.valueobject.BindingPurpose>,
    val isPrimary: Boolean
)