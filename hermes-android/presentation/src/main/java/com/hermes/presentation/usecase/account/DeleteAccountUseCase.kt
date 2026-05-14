package com.hermes.presentation.usecase.account

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import java.time.Instant
import javax.inject.Inject

/**
 * 删除账号用例
 *
 * 执行以下操作：
 * 1. 删除 ApplicationAccount 实体
 * 2. 删除所有 IdentifierBinding 实体
 * 3. 创建 BindingHistoryRecord 记录每次解绑
 *
 * 不限制删除条件（即使绑定验证渠道也可删除）
 */
class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: ApplicationAccountRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val bindingHistoryRepository: BindingHistoryRepository
) {
    /**
     * 删除账号（包含自动解绑所有绑定关系）
     *
     * @param accountId 账号ID
     * @return 删除结果
     */
    suspend operator fun invoke(accountId: Long): DeleteAccountResult {
        try {
            // 1. 获取账号信息
            val account = accountRepository.getById(accountId)
            if (account == null) {
                return DeleteAccountResult.AccountNotFound
            }

            // 2. 获取所有绑定关系
            val bindings = bindingRepository.getByAccountId(accountId)

            // 3. 为每个绑定创建历史记录（记录解绑操作）
            val now = Instant.now()
            bindings.forEach { binding ->
                val historyRecord = BindingHistoryRecord(
                    id = null,
                    accountId = accountId,
                    identifierId = binding.identifierId,
                    actionType = ActionType.UNBIND,
                    previousPurposes = binding.purposes,
                    newPurposes = null,
                    previousIdentifierId = null,
                    newIdentifierId = null,
                    actionAt = now,
                    actionBy = null,
                    notes = "账号删除时自动解绑"
                )
                bindingHistoryRepository.insert(historyRecord)
            }

            // 4. 删除所有绑定关系
            bindingRepository.deleteByAccountId(accountId)

            // 5. 删除账号
            accountRepository.delete(account)

            return DeleteAccountResult.Success(
                accountName = account.accountName,
                unboundCount = bindings.size
            )
        } catch (e: Exception) {
            return DeleteAccountResult.Error(e.message ?: "Unknown error")
        }
    }
}

/**
 * 删除账号结果
 */
sealed class DeleteAccountResult {
    /**
     * 成功删除
     */
    data class Success(
        val accountName: String,
        val unboundCount: Int
    ) : DeleteAccountResult()

    /**
     * 账号不存在
     */
    object AccountNotFound : DeleteAccountResult()

    /**
     * 删除失败
     */
    data class Error(val message: String) : DeleteAccountResult()
}