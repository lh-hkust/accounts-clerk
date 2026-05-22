package com.hermes.presentation.usecase.account

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.BindingHistoryRepository
import java.time.Instant
import javax.inject.Inject

/**
 * 更新账号用例
 *
 * 支持更新以下字段：
 * - accountName
 * - accountIdentifier（需校验唯一性）
 * - nickname
 * - status
 * - 绑定渠道列表（新增/删除）
 */
class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: ApplicationAccountRepository,
    private val bindingRepository: IdentifierBindingRepository,
    private val bindingHistoryRepository: BindingHistoryRepository
) {
    /**
     * 更新账号信息
     *
     * @param accountId 账号ID
     * @param accountName 账号名称
     * @param accountIdentifier 账号标识（可选）
     * @param nickname 昵称（可选）
     * @param status 账号状态
     * @param bindings 绑定渠道列表（IdentifierId -> Purposes）
     * @return 更新后的账号
     * @throws IllegalArgumentException 如果账号ID重复
     */
    suspend operator fun invoke(
        accountId: Long,
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?,
        status: AccountStatus,
        bindings: List<BindingUpdate>
    ): ApplicationAccount {
        if (accountName.isBlank()) {
            throw IllegalArgumentException("Account name must not be empty")
        }

        // 获取现有账号
        val existingAccount = accountRepository.getById(accountId)
            ?: throw IllegalArgumentException("Account not found")

        // 校验账号ID唯一性（排除当前账号）
        if (accountIdentifier != null && accountIdentifier != existingAccount.accountIdentifier) {
            if (accountRepository.checkDuplicate(applicationId, accountIdentifier)) {
                throw IllegalArgumentException("Account identifier already exists for this application")
            }
        }

        // 获取现有绑定
        val existingBindings = bindingRepository.getByAccountId(accountId)
        val existingBindingMap = existingBindings.associateBy { it.identifierId }

        val now = Instant.now()

        // 创建历史记录
        val historyRecords = mutableListOf<BindingHistoryRecord>()

        // 删除不再需要的绑定
        val newBindingIds = bindings.map { it.identifierId }.toSet()
        existingBindings.forEach { existingBinding ->
            if (existingBinding.identifierId !in newBindingIds) {
                // 删除绑定
                bindingRepository.delete(existingBinding)
                // 记录历史
                historyRecords.add(
                    BindingHistoryRecord(
                        id = null,
                        accountId = accountId,
                        identifierId = existingBinding.identifierId,
                        actionType = ActionType.UNBIND,
                        previousPurposes = existingBinding.purposes,
                        newPurposes = null,
                        previousIdentifierId = null,
                        newIdentifierId = null,
                        actionAt = now,
                        actionBy = null,
                        notes = "编辑账号时解绑"
                    )
                )
            }
        }

        // 添加或更新绑定
        bindings.forEach { newBinding ->
            val existingBinding = existingBindingMap[newBinding.identifierId]
            if (existingBinding == null) {
                // 新增绑定
                val newBindingEntity = com.hermes.domain.model.IdentifierBinding(
                    id = null,
                    accountId = accountId,
                    identifierId = newBinding.identifierId,
                    purposes = newBinding.purposes.toList(),
                    isPrimary = false,
                    boundAt = now
                )
                bindingRepository.insert(newBindingEntity)
                // 记录历史
                historyRecords.add(
                    BindingHistoryRecord(
                        id = null,
                        accountId = accountId,
                        identifierId = newBinding.identifierId,
                        actionType = ActionType.BIND,
                        previousPurposes = null,
                        newPurposes = newBinding.purposes.toList(),
                        previousIdentifierId = null,
                        newIdentifierId = null,
                        actionAt = now,
                        actionBy = null,
                        notes = "编辑账号时绑定"
                    )
                )
            } else if (existingBinding.purposes != newBinding.purposes) {
                // 更新用途
                bindingRepository.updatePurposes(existingBinding.id!!, newBinding.purposes.toList())
                // 记录历史
                historyRecords.add(
                    BindingHistoryRecord(
                        id = null,
                        accountId = accountId,
                        identifierId = newBinding.identifierId,
                        actionType = ActionType.CHANGE_PURPOSE,
                        previousPurposes = existingBinding.purposes,
                        newPurposes = newBinding.purposes.toList(),
                        previousIdentifierId = null,
                        newIdentifierId = null,
                        actionAt = now,
                        actionBy = null,
                        notes = "编辑账号时修改用途"
                    )
                )
            }
        }

        // 保存历史记录
        historyRecords.forEach { bindingHistoryRepository.insert(it) }

        // 更新账号基本信息
        existingAccount.accountName = accountName
        existingAccount.accountIdentifier = accountIdentifier
        existingAccount.nickname = nickname
        existingAccount.status = status
        existingAccount.updatedAt = now

        accountRepository.update(existingAccount)

        return existingAccount
    }

    /**
     * 校验账号ID唯一性（用于编辑页面实时校验）
     *
     * @param applicationId 应用ID
     * @param accountIdentifier 账号标识
     * @param excludeAccountId 排除的账号ID（当前编辑的账号）
     * @return true表示重复，false表示不重复
     */
    suspend fun checkDuplicate(
        applicationId: Long,
        accountIdentifier: String,
        excludeAccountId: Long
    ): Boolean {
        // 获取排除账号的当前标识
        val excludeAccount = accountRepository.getById(excludeAccountId)
        val excludeIdentifier = excludeAccount?.accountIdentifier

        // 如果标识未变化，则不算重复
        if (accountIdentifier == excludeIdentifier) {
            return false
        }

        return accountRepository.checkDuplicate(applicationId, accountIdentifier)
    }
}

/**
 * 绑定更新数据
 */
data class BindingUpdate(
    val identifierId: Long,
    val purposes: Set<BindingPurpose>
)