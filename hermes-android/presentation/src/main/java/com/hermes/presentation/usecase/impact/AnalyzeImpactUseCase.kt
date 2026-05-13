package com.hermes.presentation.usecase.impact

import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.valueobject.WarningLevel
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * 影响分析用例
 */
class AnalyzeImpactUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 分析标识停用的影响
     *
     * @param identifierId 标识ID
     * @return 影响分析结果
     */
    suspend operator fun invoke(identifierId: Long): ImpactAnalysisResult? {
        val identifier = identifierRepository.getById(identifierId)
            ?: return null

        val bindings = bindingRepository.getByIdentifierId(identifierId)
        val affectedAccounts = bindings.mapNotNull { binding ->
            val account = accountRepository.getById(binding.accountId)
            val application = account?.let { applicationRepository.getById(it.applicationId) }
            if (account != null) {
                AffectedAccountDetail(
                    account = account,
                    applicationName = application?.name ?: "",
                    applicationCategory = application?.category,
                    purposes = binding.purposes,
                    isFinancial = isFinancialApplication(application?.category)
                )
            } else null
        }

        val impactLevel = calculateImpactLevel(affectedAccounts)
        val financialCount = affectedAccounts.count { it.isFinancial }
        val remainingDays = identifier.plannedDeactTime?.let { plannedTime ->
            ChronoUnit.DAYS.between(Instant.now(), plannedTime).toInt()
        }

        return ImpactAnalysisResult(
            identifierId = identifierId,
            identifierValue = identifier.value,
            identifierType = identifier.type,
            identifierStatus = identifier.status,
            plannedDeactTime = identifier.plannedDeactTime,
            affectedAccounts = affectedAccounts,
            impactLevel = impactLevel,
            remainingDays = remainingDays,
            totalAffectedCount = affectedAccounts.size,
            financialAccountCount = financialCount
        )
    }

    private fun isFinancialApplication(category: String?): Boolean {
        if (category == null) return false
        val lower = category.lowercase()
        return lower.contains("金融") || lower.contains("finance") || lower.contains("银行") || lower.contains("bank")
    }

    private fun calculateImpactLevel(accounts: List<AffectedAccountDetail>): WarningLevel {
        val hasFinancial = accounts.any { it.isFinancial }
        val count = accounts.size

        return when {
            hasFinancial -> WarningLevel.HIGH
            count > 5 -> WarningLevel.HIGH
            count >= 2 -> WarningLevel.MEDIUM
            else -> WarningLevel.LOW
        }
    }
}

/**
 * 影响分析结果
 */
data class ImpactAnalysisResult(
    val identifierId: Long,
    val identifierValue: String,
    val identifierType: com.hermes.domain.valueobject.IdentifierType,
    val identifierStatus: com.hermes.domain.valueobject.IdentifierStatus,
    val plannedDeactTime: Instant?,
    val affectedAccounts: List<AffectedAccountDetail>,
    val impactLevel: WarningLevel,
    val remainingDays: Int?,
    val totalAffectedCount: Int,
    val financialAccountCount: Int
)

/**
 * 受影响账户详情
 */
data class AffectedAccountDetail(
    val account: com.hermes.domain.model.ApplicationAccount,
    val applicationName: String,
    val applicationCategory: String?,
    val purposes: List<com.hermes.domain.valueobject.BindingPurpose>,
    val isFinancial: Boolean
)