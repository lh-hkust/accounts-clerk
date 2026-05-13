package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.presentation.usecase.identifier.GetIdentifierDetailUseCase
import com.hermes.presentation.usecase.identifier.DeleteIdentifierUseCase
import com.hermes.presentation.usecase.identifier.IdentifierDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 标识详情 ViewModel
 */
class IdentifierDetailViewModel(
    private val getIdentifierDetailUseCase: GetIdentifierDetailUseCase,
    private val deleteIdentifierUseCase: DeleteIdentifierUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IdentifierDetailState>(IdentifierDetailState.Loading)
    val uiState: StateFlow<IdentifierDetailState> = _uiState.asStateFlow()

    private val _canDelete = MutableStateFlow(false)
    val canDelete: StateFlow<Boolean> = _canDelete.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    fun loadIdentifierDetail(identifierId: Long) {
        viewModelScope.launch {
            _uiState.value = IdentifierDetailState.Loading
            try {
                val detail = getIdentifierDetailUseCase(identifierId)
                if (detail != null) {
                    _uiState.value = IdentifierDetailState.Success(detail)
                    _canDelete.value = detail.boundAccountCount == 0
                } else {
                    _uiState.value = IdentifierDetailState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = IdentifierDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteIdentifier() {
        val currentState = _uiState.value
        if (currentState !is IdentifierDetailState.Success) return

        val identifierId = currentState.detail.identifier.id ?: return

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