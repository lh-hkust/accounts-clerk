package com.hermes.presentation.usecase.deactivation

import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.repository.IdentifierBindingRepository

/**
 * 获取停用计划详情用例
 */
class GetDeactivationDetailUseCase(
    private val deactivationRepository: IdentifierDeactivationRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 获取停用计划详情
     *
     * @param identifierId 标识ID
     * @return 停用计划详情（如果存在）
     */
    suspend operator fun invoke(identifierId: Long): DeactivationDetail? {
        val deactivation = deactivationRepository.getByIdentifierId(identifierId)
            ?: return null

        val bindings = bindingRepository.getByIdentifierId(identifierId)
        val affectedAccounts = bindings.map { binding ->
            val account = accountRepository.getById(binding.accountId)
            val application = account?.let { applicationRepository.getById(it.applicationId) }
            AffectedAccountInfo(
                accountId = binding.accountId,
                accountName = account?.nickname ?: account?.accountName ?: "",
                applicationName = application?.name ?: "",
                applicationCategory = application?.category,
                purposes = binding.purposes
            )
        }

        val remainingDays = deactivation.scheduledTime?.let { calculateRemainingDays(it) } ?: 0

        return DeactivationDetail(
            deactivation = deactivation,
            affectedAccounts = affectedAccounts,
            remainingDays = remainingDays,
            totalAffectedCount = bindings.size
        )
    }

    private fun calculateRemainingDays(scheduledTime: java.time.Instant): Int {
        val now = java.time.Instant.now()
        val diff = scheduledTime.toEpochMilli() - now.toEpochMilli()
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }
}

/**
 * 停用计划详情
 */
data class DeactivationDetail(
    val deactivation: com.hermes.domain.model.IdentifierDeactivation,
    val affectedAccounts: List<AffectedAccountInfo>,
    val remainingDays: Int,
    val totalAffectedCount: Int
)

/**
 * 受影响账户信息
 */
data class AffectedAccountInfo(
    val accountId: Long,
    val accountName: String,
    val applicationName: String,
    val applicationCategory: String?,
    val purposes: List<com.hermes.domain.valueobject.BindingPurpose>
)