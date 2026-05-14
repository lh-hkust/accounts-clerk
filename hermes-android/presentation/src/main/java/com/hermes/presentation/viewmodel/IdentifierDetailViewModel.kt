package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.presentation.usecase.identifier.GetIdentifierDetailUseCase
import com.hermes.presentation.usecase.identifier.DeleteIdentifierUseCase
import com.hermes.presentation.usecase.identifier.IdentifierDetail
import com.hermes.presentation.usecase.deactivation.GetDeactivationDetailUseCase
import com.hermes.presentation.usecase.deactivation.CancelDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 标识详情 ViewModel
 */
@HiltViewModel
class IdentifierDetailViewModel @Inject constructor(
    private val getIdentifierDetailUseCase: GetIdentifierDetailUseCase,
    private val deleteIdentifierUseCase: DeleteIdentifierUseCase,
    private val getDeactivationDetailUseCase: GetDeactivationDetailUseCase,
    private val cancelDeactivationUseCase: CancelDeactivationUseCase,
    private val handleWarningUseCase: com.hermes.presentation.usecase.warning.HandleWarningUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IdentifierDetailState>(IdentifierDetailState.Loading)
    val uiState: StateFlow<IdentifierDetailState> = _uiState.asStateFlow()

    private val _canDelete = MutableStateFlow(false)
    val canDelete: StateFlow<Boolean> = _canDelete.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    private val _deactivationDetail = MutableStateFlow<DeactivationDetail?>(null)
    val deactivationDetail: StateFlow<DeactivationDetail?> = _deactivationDetail.asStateFlow()

    fun loadIdentifierDetail(identifierId: Long) {
        viewModelScope.launch {
            _uiState.value = IdentifierDetailState.Loading
            try {
                val detail = getIdentifierDetailUseCase(identifierId)
                if (detail != null) {
                    _uiState.value = IdentifierDetailState.Success(detail)
                    _canDelete.value = detail.boundAccountCount == 0
                    // 加载停用计划详情
                    val deactivation = getDeactivationDetailUseCase(identifierId)
                    _deactivationDetail.value = deactivation
                } else {
                    _uiState.value = IdentifierDetailState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = IdentifierDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteIdentifier(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                deleteIdentifierUseCase(identifierId)
                _operationState.value = OperationState.Success("Identifier deleted")
                _uiState.value = IdentifierDetailState.NotFound
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Cannot delete identifier")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun cancelDeactivation(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                cancelDeactivationUseCase(identifierId, "User cancelled")
                _operationState.value = OperationState.Success("Deactivation cancelled")
                _deactivationDetail.value = null
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to cancel")
            }
        }
    }

    fun handleWarning(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                handleWarningUseCase(identifierId)
                _operationState.value = OperationState.Success("Warning handled")
                // 刷新数据
                loadIdentifierDetail(identifierId)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to handle warning")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

sealed class IdentifierDetailState {
    object Loading : IdentifierDetailState()
    data class Success(val detail: IdentifierDetail) : IdentifierDetailState()
    object NotFound : IdentifierDetailState()
    data class Error(val message: String) : IdentifierDetailState()
}