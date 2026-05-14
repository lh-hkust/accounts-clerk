package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.FieldType
import com.hermes.presentation.usecase.account.*
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
    private val addAccountExtensionUseCase: AddAccountExtensionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountListState>(AccountListState.Loading)
    val uiState: StateFlow<AccountListState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

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

    fun addAccount(
        applicationId: Long,
        accountName: String,
        accountIdentifier: String?,
        nickname: String?
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                addAccountUseCase(applicationId, accountName, accountIdentifier, nickname)
                _operationState.value = OperationState.Success("Account added successfully")
                loadAccounts()
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add account")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
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
                _operationState.value = OperationState.Error("Unknown error")
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
}

sealed class AccountListState {
    object Loading : AccountListState()
    data class Success(val items: List<AccountListItem>) : AccountListState()
    data class Error(val message: String) : AccountListState()
}