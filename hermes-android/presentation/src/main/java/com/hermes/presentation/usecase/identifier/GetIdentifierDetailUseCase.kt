package com.hermes.presentation.usecase.identifier

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository

/**
 * 获取标识详情用例
 */
class GetIdentifierDetailUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 获取标识详情（包含绑定的账户列表）
     *
     * @param identifierId 标识ID
     * @return 标识详情（如果存在）
     */
    suspend operator fun invoke(identifierId: Long): IdentifierDetail? {
        val identifier = identifierRepository.getById(identifierId)
            ?: return null

        val bindings = bindingRepository.getByIdentifierId(identifierId)
        val boundAccounts = bindings.map { binding ->
            val account = accountRepository.getById(binding.accountId)
            val application = account?.let { applicationRepository.getById(it.applicationId) }
            BoundAccountInfo(
                accountId = binding.accountId,
                accountName = account?.nickname ?: account?.accountName ?: "",
                accountIdentifier = account?.accountIdentifier,
                applicationName = application?.name ?: "",
                applicationIconUrl = application?.iconUrl,
                purposes = binding.purposes,
                isPrimary = binding.isPrimary
            )
        }

        return IdentifierDetail(
            identifier = identifier,
            boundAccounts = boundAccounts,
            boundAccountCount = bindings.size
        )
    }
}

/**
 * 标识详情
 */
data class IdentifierDetail(
    val identifier: com.hermes.domain.model.IdentityIdentifier,
    val boundAccounts: List<BoundAccountInfo>,
    val boundAccountCount: Int
)

/**
 * 绑定账户信息
 */
data class BoundAccountInfo(
    val accountId: Long,
    val accountName: String,
    val accountIdentifier: String?,
    val applicationName: String,
    val applicationIconUrl: String?,
    val purposes: List<com.hermes.domain.valueobject.BindingPurpose>,
    val isPrimary: Boolean
)