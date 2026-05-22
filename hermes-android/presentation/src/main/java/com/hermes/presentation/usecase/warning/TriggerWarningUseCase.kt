package com.hermes.presentation.usecase.warning

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.WarningRecordRepository
import com.hermes.domain.valueobject.WarningType
import java.time.Instant

/**
 * 触发预警用例
 */
class TriggerWarningUseCase(
    private val warningRepository: WarningRecordRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val accountRepository: ApplicationAccountRepository,
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 触发预警
     *
     * @param identifierId 标识ID
     * @param warningType 预警类型
     * @return 创建的预警记录
     */
    suspend operator fun invoke(identifierId: Long, warningType: WarningType): WarningRecord {
        val bindings = bindingRepository.getByIdentifierId(identifierId)
        val affectedAccounts = bindings.mapNotNull { binding ->
            accountRepository.getById(binding.accountId)
        }

        val warningLevel = calculateWarningLevel(affectedAccounts)
        val message = buildWarningMessage(affectedAccounts, identifierId)

        val now = Instant.now()
        val warning = WarningRecord(
            id = null,
            identifierId = identifierId,
            accountId = null,
            warningType = warningType,
            warningLevel = warningLevel,
            message = message,
            triggeredAt = now,
            isRead = false,
            isHandled = false
        )

        return warningRepository.insert(warning)
    }

    private suspend fun calculateWarningLevel(accounts: List<ApplicationAccount>): com.hermes.domain.valueobject.WarningLevel {
        var hasFinancial = false
        var count = 0

        for (account in accounts) {
            val app = applicationRepository.getById(account.applicationId)
            if (app?.category?.lowercase()?.contains("金融") == true || app?.category?.lowercase()?.contains("finance") == true) {
                hasFinancial = true
            }
            count++
        }

        return when {
            hasFinancial -> com.hermes.domain.valueobject.WarningLevel.HIGH
            count > 5 -> com.hermes.domain.valueobject.WarningLevel.HIGH
            count >= 2 -> com.hermes.domain.valueobject.WarningLevel.MEDIUM
            else -> com.hermes.domain.valueobject.WarningLevel.LOW
        }
    }

    private fun buildWarningMessage(accounts: List<ApplicationAccount>, identifierId: Long): String {
        val count = accounts.size
        return "Identifier (ID: $identifierId) affects $count accounts. Please handle before deactivation."
    }
}