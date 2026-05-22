package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.presentation.usecase.account.AccountDetail
import com.hermes.presentation.usecase.account.DeleteAccountResult
import com.hermes.presentation.usecase.account.DeleteAccountUseCase
import com.hermes.presentation.usecase.account.GetAccountDetailUseCase
import com.hermes.presentation.usecase.account.UpdateAccountStatusUseCase
import com.hermes.presentation.usecase.account.UpdateAccountUseCase
import com.hermes.presentation.usecase.account.BindingUpdate
import com.hermes.presentation.usecase.binding.SwitchBindingIdentifierUseCase
import com.hermes.presentation.ui.component.CurrentBindingInfo
import com.hermes.presentation.ui.component.IdentifierOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 账户详情 ViewModel
 */
@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val getAccountDetailUseCase: GetAccountDetailUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val identifierRepository: IdentityIdentifierRepository,
    private val switchBindingIdentifierUseCase: SwitchBindingIdentifierUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountDetailState>(AccountDetailState.Loading)
    val uiState: StateFlow<AccountDetailState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    // 可用于更换的渠道列表（排除当前绑定）
    private val _availableIdentifiersForSwitch = MutableStateFlow<List<IdentifierOption>>(emptyList())
    val availableIdentifiersForSwitch: StateFlow<List<IdentifierOption>> = _availableIdentifiersForSwitch.asStateFlow()

    // 当前正在更换的绑定信息
    private val _currentBindingForSwitch = MutableStateFlow<CurrentBindingInfo?>(null)
    val currentBindingForSwitch: StateFlow<CurrentBindingInfo?> = _currentBindingForSwitch.asStateFlow()

    // 更换绑定操作状态
    private val _switchBindingOperationState = MutableStateFlow<SwitchBindingOperationState>(SwitchBindingOperationState.Idle)
    val switchBindingOperationState: StateFlow<SwitchBindingOperationState> = _switchBindingOperationState.asStateFlow()

    /**
     * 加载账户详情
     */
    fun loadAccountDetail(accountId: Long) {
        viewModelScope.launch {
            _uiState.value = AccountDetailState.Loading
            try {
                val detail = getAccountDetailUseCase(accountId)
                if (detail != null) {
                    _uiState.value = AccountDetailState.Success(detail)
                } else {
                    _uiState.value = AccountDetailState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = AccountDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * 刷新账户详情
     */
    fun refresh(accountId: Long) {
        loadAccountDetail(accountId)
    }

    /**
     * 更新账户状态
     *
     * @param accountId 账户ID
     * @param newStatus 新状态
     */
    fun updateStatus(accountId: Long, newStatus: AccountStatus) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                updateAccountStatusUseCase(accountId, newStatus)
                _operationState.value = OperationState.Success("状态已更新")
                // 刷新详情
                loadAccountDetail(accountId)
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "状态转换无效")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("未知错误")
            }
        }
    }

    /**
     * 准备更换绑定的数据
     * 加载可用渠道列表（排除当前绑定）
     *
     * @param accountId 账户ID
     * @param currentBindingId 当前绑定的标识ID
     */
    fun prepareSwitchBinding(accountId: Long, currentBindingId: Long) {
        viewModelScope.launch {
            _switchBindingOperationState.value = SwitchBindingOperationState.Loading

            try {
                // 获取当前绑定信息
                val currentDetail = (_uiState.value as? AccountDetailState.Success)?.detail
                val currentBinding = currentDetail?.boundIdentifiers?.find { it.identifierId == currentBindingId }

                if (currentBinding != null) {
                    _currentBindingForSwitch.value = CurrentBindingInfo(
                        identifierId = currentBindingId,
                        type = currentBinding.identifierType,
                        value = currentBinding.identifierValue,
                        purposes = currentBinding.purposes
                    )

                    // 获取所有可用标识，排除当前绑定的标识
                    val allIdentifiers = identifierRepository.getAll()
                    val availableForSwitch = allIdentifiers
                        .filter { it.id != currentBindingId }
                        .filter { it.status == IdentifierStatus.ACTIVE || it.status == IdentifierStatus.PENDING_DEACTIVATION }
                        .sortedWith(compareBy(
                            { getStatusOrder(it.status) },
                            { it.updatedAt }
                        ))
                        .map { identifier ->
                            IdentifierOption(
                                id = identifier.id ?: 0L,
                                type = identifier.type,
                                value = identifier.value,
                                status = identifier.status
                            )
                        }

                    _availableIdentifiersForSwitch.value = availableForSwitch
                    _switchBindingOperationState.value = SwitchBindingOperationState.Prepared
                } else {
                    _switchBindingOperationState.value = SwitchBindingOperationState.Error("找不到当前绑定信息")
                }
            } catch (e: Exception) {
                _switchBindingOperationState.value = SwitchBindingOperationState.Error(e.message ?: "加载失败")
            }
        }
    }

    /**
     * 执行更换绑定
     *
     * @param accountId 账户ID
     * @param oldIdentifierId 原标识ID
     * @param newIdentifierId 新标识ID
     * @param newPurposes 新用途列表
     */
    fun switchBinding(
        accountId: Long,
        oldIdentifierId: Long,
        newIdentifierId: Long,
        newPurposes: Set<BindingPurpose>
    ) {
        viewModelScope.launch {
            _switchBindingOperationState.value = SwitchBindingOperationState.Switching

            try {
                // 调用更换绑定用例（会自动记录历史）
                switchBindingIdentifierUseCase(accountId, oldIdentifierId, newIdentifierId)

                // 如果用途有变化，需要额外调用修改用途用例
                // 注意：当前 SwitchBindingIdentifierUseCase 会保留原用途
                // 如果需要修改用途，需要额外处理（此处简化处理）

                // 刷新账户详情
                loadAccountDetail(accountId)

                // 清理临时状态
                _currentBindingForSwitch.value = null
                _availableIdentifiersForSwitch.value = emptyList()
                _switchBindingOperationState.value = SwitchBindingOperationState.Success
            } catch (e: Exception) {
                _switchBindingOperationState.value = SwitchBindingOperationState.Error(e.message ?: "更换失败")
            }
        }
    }

    /**
     * 重置更换绑定操作状态
     */
    fun resetSwitchBindingState() {
        _switchBindingOperationState.value = SwitchBindingOperationState.Idle
        _currentBindingForSwitch.value = null
        _availableIdentifiersForSwitch.value = emptyList()
    }

    /**
     * 重置操作状态
     */
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }

    /**
     * 删除账户（自动解绑所有绑定关系）
     */
    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.InProgress
            try {
                val result = deleteAccountUseCase(accountId)
                when (result) {
                    is DeleteAccountResult.Success -> {
                        _deleteState.value = DeleteState.Success(
                            accountName = result.accountName,
                            unboundCount = result.unboundCount
                        )
                    }
                    is DeleteAccountResult.AccountNotFound -> {
                        _deleteState.value = DeleteState.Error("账号不存在")
                    }
                    is DeleteAccountResult.Error -> {
                        _deleteState.value = DeleteState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.message ?: "删除失败")
            }
        }
    }

    /**
     * 重置删除状态
     */
    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }

    /**
     * 更新账号信息（包含绑定渠道）
     *
     * @param accountId 账号ID
     * @param applicationId 应用ID
     * @param accountName 账号名称
     * @param accountIdentifier 账号标识
     * @param nickname 昵称
     * @param status 状态
     * @param bindings 绑定渠道列表
     */
    fun updateAccount(
        accountId: Long,
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?,
        status: AccountStatus,
        bindings: List<BindingUpdate>
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                updateAccountUseCase(
                    accountId = accountId,
                    applicationId = applicationId,
                    accountName = accountName,
                    accountIdentifier = accountIdentifier,
                    nickname = nickname,
                    status = status,
                    bindings = bindings
                )
                _operationState.value = OperationState.Success("账号已更新")
                // 刷新详情
                loadAccountDetail(accountId)
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "更新失败")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("未知错误")
            }
        }
    }

    /**
     * 校验账号ID唯一性
     *
     * @param applicationId 应用ID
     * @param accountIdentifier 账号标识
     * @param excludeAccountId 排除的账号ID
     * @param callback 回调结果（true表示重复）
     */
    fun checkDuplicate(
        applicationId: Long,
        accountIdentifier: String,
        excludeAccountId: Long,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val isDuplicate = updateAccountUseCase.checkDuplicate(
                    applicationId = applicationId,
                    accountIdentifier = accountIdentifier,
                    excludeAccountId = excludeAccountId
                )
                callback(isDuplicate)
            } catch (e: Exception) {
                callback(false) // 发生异常时假定不重复
            }
        }
    }

    private fun getStatusOrder(status: IdentifierStatus): Int {
        return when (status) {
            IdentifierStatus.ACTIVE -> 1
            IdentifierStatus.PENDING_DEACTIVATION -> 2
            IdentifierStatus.DEACTIVATED -> 3
            IdentifierStatus.INVALIDATED -> 4
        }
    }
}

/**
 * 更换绑定操作状态
 */
sealed class SwitchBindingOperationState {
    object Idle : SwitchBindingOperationState()
    object Loading : SwitchBindingOperationState()
    object Prepared : SwitchBindingOperationState()
    object Switching : SwitchBindingOperationState()
    object Success : SwitchBindingOperationState()
    data class Error(val message: String) : SwitchBindingOperationState()
}