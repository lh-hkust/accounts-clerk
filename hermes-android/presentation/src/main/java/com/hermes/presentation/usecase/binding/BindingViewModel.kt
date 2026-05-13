package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.presentation.usecase.binding.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 绑定管理 ViewModel
 */
class BindingViewModel(
    private val bindIdentifierUseCase: BindIdentifierUseCase,
    private val unbindIdentifierUseCase: UnbindIdentifierUseCase,
    private val changeBindingPurposeUseCase: ChangeBindingPurposeUseCase,
    private val switchBindingIdentifierUseCase: SwitchBindingIdentifierUseCase
) : ViewModel() {

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    fun bindIdentifier(
        accountId: Long,
        identifierId: Long,
        purposes: List<BindingPurpose>,
        isPrimary: Boolean = false
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                bindIdentifierUseCase(accountId, identifierId, purposes, isPrimary)
                _operationState.value = OperationState.Success("Identifier bound successfully")
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to bind")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun unbindIdentifier(accountId: Long, identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                unbindIdentifierUseCase(accountId, identifierId)
                _operationState.value = OperationState.Success("Identifier unbound")
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to unbind")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun changePurpose(accountId: Long, identifierId: Long, newPurposes: List<BindingPurpose>) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                changeBindingPurposeUseCase(accountId, identifierId, newPurposes)
                _operationState.value = OperationState.Success("Purposes changed")
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to change purposes")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun switchIdentifier(accountId: Long, oldIdentifierId: Long, newIdentifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                switchBindingIdentifierUseCase(accountId, oldIdentifierId, newIdentifierId)
                _operationState.value = OperationState.Success("Identifier switched")
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to switch identifier")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}