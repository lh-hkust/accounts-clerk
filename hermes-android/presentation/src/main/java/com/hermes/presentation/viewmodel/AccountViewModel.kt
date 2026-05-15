package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.FieldType
import com.hermes.presentation.usecase.account.*
import com.hermes.presentation.usecase.binding.BindIdentifierUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 账户列表 ViewModel
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountListUseCase: GetAccountListUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase,
    private val addAccountExtensionUseCase: AddAccountExtensionUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val bindIdentifierUseCase: BindIdentifierUseCase,
    private val getApplicationListUseCase: com.hermes.presentation.usecase.application.GetApplicationListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountListState>(AccountListState.Loading)
    val uiState: StateFlow<AccountListState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    // 应用列表状态
    private val _applicationListState = MutableStateFlow<ApplicationListState>(ApplicationListState.Loading)
    val applicationListState: StateFlow<ApplicationListState> = _applicationListState.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.value = AccountListState.Loading
            try {
                val items = getAccountListUseCase()
                _uiState.value = AccountListState.Success(items)
            } catch (e: Exception) {
                _uiState.value = AccountListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun filterByApplication(applicationId: Long) {
        viewModelScope.launch {
            _uiState.value = AccountListState.Loading
            try {
                val items = getAccountListUseCase.getByApplication(applicationId)
                _uiState.value = AccountListState.Success(items)
            } catch (e: Exception) {
                _uiState.value = AccountListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * 添加账号（支持多渠道绑定）
     * @param channelBindings 多渠道绑定数据：渠道ID -> 用途集合
     */
    fun addAccount(
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?,
        channelBindings: Map<Long, Set<BindingPurpose>> = emptyMap() // 多渠道绑定数据
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                // 创建账户
                val account = addAccountUseCase(applicationId, accountName, accountIdentifier, nickname)

                // 如果选择了绑定渠道，则创建多个绑定关系
                val accountIdValue = account.id
                if (accountIdValue != null && channelBindings.isNotEmpty()) {
                    // 遍历每个渠道，创建绑定关系
                    channelBindings.forEach { (identifierId, purposes) ->
                        if (purposes.isNotEmpty()) {
                            bindIdentifierUseCase(
                                accountId = accountIdValue,
                                identifierId = identifierId,
                                purposes = purposes.toList(),
                                isPrimary = true // 第一个绑定默认为主绑定
                            )
                        }
                    }
                }

                _operationState.value = OperationState.Success("Account added successfully")
                loadAccounts()
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add account")
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                _operationState.value = OperationState.Error("数据约束错误：${e.message}")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("添加失败：${e.message ?: "请检查输入是否正确"}")
            }
        }
    }

    fun updateStatus(accountId: Long, newStatus: AccountStatus) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                updateAccountStatusUseCase(accountId, newStatus)
                _operationState.value = OperationState.Success("Status updated")
                loadAccounts()
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Invalid status transition")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("更新失败：${e.message ?: "请稍后重试"}")
            }
        }
    }

    fun addExtension(
        accountId: Long,
        key: String,
        value: String?,
        label: String,
        fieldType: FieldType
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                addAccountExtensionUseCase(accountId, key, value, label, fieldType)
                _operationState.value = OperationState.Success("Extension added")
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add extension")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

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
                        // 刷新列表
                        loadAccounts()
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
     * 加载应用列表（用于AddAccountScreen）
     */
    fun loadApplications() {
        viewModelScope.launch {
            _applicationListState.value = ApplicationListState.Loading
            try {
                val apps = getApplicationListUseCase()
                _applicationListState.value = ApplicationListState.Success(apps)
            } catch (e: Exception) {
                _applicationListState.value = ApplicationListState.Error(e.message ?: "加载应用列表失败")
            }
        }
    }
}

sealed class AccountListState {
    object Loading : AccountListState()
    data class Success(val items: List<AccountListItem>) : AccountListState()
    data class Error(val message: String) : AccountListState()
}

// 应用列表状态
sealed class ApplicationListState {
    object Loading : ApplicationListState()
    data class Success(val items: List<com.hermes.domain.model.Application>) : ApplicationListState()
    data class Error(val message: String) : ApplicationListState()
}