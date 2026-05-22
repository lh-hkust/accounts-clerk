package com.hermes.presentation.usecase.account

import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.IdentityIdentifierRepository
import javax.inject.Inject

/**
 * 获取账户详情用例
 */
class GetAccountDetailUseCase @Inject constructor(
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
            boundIdentifiers = boundIdentifiers.map { info ->
                IdentifierBindingInfo(
                    identifierId = info.identifierId,
                    identifierType = info.identifierType,
                    identifierValue = info.identifierValue,
                    purposes = info.purposes
                )
            },
            relatedAccounts = emptyList()
        )
    }
}

/**
 * 账户详情
 */
data class AccountDetail(
    val account: com.hermes.domain.model.ApplicationAccount,
    val applicationName: String,
    val applicationIconUrl: String? = null,
    val applicationCategory: String = "",
    val boundIdentifiers: List<IdentifierBindingInfo>,
    val relatedAccounts: List<RelatedAccountInfo> = emptyList()
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