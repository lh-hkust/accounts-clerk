package com.hermes.presentation.usecase.identifier

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository

/**
 * 删除身份标识用例
 */
class DeleteIdentifierUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 删除标识
     *
     * @param identifierId 标识ID
     * @throws IllegalArgumentException 如果标识有绑定账户
     */
    suspend operator fun invoke(identifierId: Long) {
        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        val boundCount = bindingRepository.getCountByIdentifierId(identifierId)
        if (boundCount > 0) {
            throw IllegalArgumentException("Identifier has bound accounts, please unbind first")
        }

        identifierRepository.delete(identifier)
    }

    /**
     * 检查是否可以删除
     *
     * @param identifierId 标识ID
     * @return 是否可以删除
     */
    suspend fun canDelete(identifierId: Long): Boolean {
        val boundCount = bindingRepository.getCountByIdentifierId(identifierId)
        return boundCount == 0
    }

    /**
     * 获取绑定账号数量
     *
     * @param identifierId 标识ID
     * @return 绑定账号数量
     */
    suspend fun getBoundAccountCount(identifierId: Long): Int {
        return bindingRepository.getCountByIdentifierId(identifierId)
    }

    /**
     * 获取绑定的账号列表
     *
     * @param identifierId 标识ID
     * @return 绑定账号信息列表（来自GetIdentifierDetailUseCase的BoundAccountInfo）
     */
    suspend fun getBoundAccounts(identifierId: Long): List<BoundAccountInfo> {
        val bindings = bindingRepository.getByIdentifierId(identifierId)
        return bindings.map { binding ->
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
    }
}