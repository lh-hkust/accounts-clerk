package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.model.WarningRecord
import com.hermes.presentation.usecase.warning.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 预警列表 ViewModel
 */
class WarningViewModel(
    private val getWarningListUseCase: GetWarningListUseCase,
    private val handleWarningUseCase: HandleWarningUseCase,
    private val markWarningReadUseCase: MarkWarningReadUseCase,
    private val clearWarningUseCase: ClearWarningUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WarningListState>(WarningListState.Loading)
    val uiState: StateFlow<WarningListState> = _uiState.asStateFlow()

    private val _handledWarnings = MutableStateFlow<List<WarningRecord>>(emptyList())
    val handledWarnings: StateFlow<List<WarningRecord>> = _handledWarnings.asStateFlow()

    private val _unhandledCount = MutableStateFlow(0)
    val unhandledCount: StateFlow<Int> = _unhandledCount.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    init {
        loadWarnings()
    }

    fun loadWarnings() {
        viewModelScope.launch {
            _uiState.value = WarningListState.Loading
            try {
                val unhandled = getWarningListUseCase()
                val handled = getWarningListUseCase.getHandled()
                val count = getWarningListUseCase.getUnhandledCount()

                _uiState.value = WarningListState.Success(unhandled)
                _handledWarnings.value = handled
                _unhandledCount.value = count
            } catch (e: Exception) {
                _uiState.value = WarningListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadQuickHandleList(): List<WarningRecord> {
        return _uiState.value.let { state ->
            if (state is WarningListState.Success) {
                state.warnings.take(3)
            } else {
                emptyList()
            }
        }
    }

    fun handleWarning(warningId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                handleWarningUseCase(warningId)
                _operationState.value = OperationState.Success("Warning handled")
                loadWarnings()
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to handle warning")
            }
        }
    }

    fun markAsRead(warningId: Long) {
        viewModelScope.launch {
            markWarningReadUseCase(warningId)
        }
    }

    fun clearWarningsByIdentifier(identifierId: Long) {
        viewModelScope.launch {
            clearWarningUseCase(identifierId)
            loadWarnings()
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

sealed class WarningListState {
    object Loading : WarningListState()
    data class Success(val warnings: List<WarningRecord>) : WarningListState()
    data class Error(val message: String) : WarningListState()
}