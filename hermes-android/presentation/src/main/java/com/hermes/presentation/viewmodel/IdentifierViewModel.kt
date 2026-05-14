package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.usecase.identifier.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 标识列表 ViewModel
 */
@HiltViewModel
class IdentifierViewModel @Inject constructor(
    private val getIdentifierListUseCase: GetIdentifierListUseCase,
    private val addIdentifierUseCase: AddIdentifierUseCase,
    private val deleteIdentifierUseCase: DeleteIdentifierUseCase,
    private val checkDuplicateUseCase: CheckDuplicateIdentifierUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IdentifierListState>(IdentifierListState.Loading)
    val uiState: StateFlow<IdentifierListState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    private val _deleteCheckState = MutableStateFlow<DeleteCheckState>(DeleteCheckState.Idle)
    val deleteCheckState: StateFlow<DeleteCheckState> = _deleteCheckState.asStateFlow()

    init {
        loadIdentifiers()
    }

    fun loadIdentifiers() {
        viewModelScope.launch {
            _uiState.value = IdentifierListState.Loading
            try {
                val items = getIdentifierListUseCase()
                _uiState.value = IdentifierListState.Success(items)
            } catch (e: Exception) {
                _uiState.value = IdentifierListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun filterByStatus(status: com.hermes.domain.valueobject.IdentifierStatus?) {
        viewModelScope.launch {
            _uiState.value = IdentifierListState.Loading
            try {
                val items = if (status != null) {
                    getIdentifierListUseCase.getByStatus(status)
                } else {
                    getIdentifierListUseCase()
                }
                _uiState.value = IdentifierListState.Success(items)
            } catch (e: Exception) {
                _uiState.value = IdentifierListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addIdentifier(type: IdentifierType, value: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                addIdentifierUseCase(type, value)
                _operationState.value = OperationState.Success("Identifier added successfully")
                loadIdentifiers()
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add identifier")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    /**
     * 检查是否可以删除标识（获取绑定账号信息）
     *
     * @param identifierId 标识ID
     */
    fun checkDeleteState(identifierId: Long) {
        viewModelScope.launch {
            _deleteCheckState.value = DeleteCheckState.Loading
            try {
                val boundCount = deleteIdentifierUseCase.getBoundAccountCount(identifierId)
                if (boundCount > 0) {
                    val boundAccounts = deleteIdentifierUseCase.getBoundAccounts(identifierId)
                    _deleteCheckState.value = DeleteCheckState.HasBindings(
                        identifierId = identifierId,
                        boundCount = boundCount,
                        boundAccounts = boundAccounts
                    )
                } else {
                    _deleteCheckState.value = DeleteCheckState.CanDelete(identifierId)
                }
            } catch (e: Exception) {
                _deleteCheckState.value = DeleteCheckState.Error(e.message ?: "Failed to check delete state")
            }
        }
    }

    fun deleteIdentifier(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                deleteIdentifierUseCase(identifierId)
                _operationState.value = OperationState.Success("Identifier deleted")
                loadIdentifiers()
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Cannot delete identifier")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun checkDuplicate(type: IdentifierType, value: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isDuplicate = checkDuplicateUseCase(type, value)
            onResult(isDuplicate)
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }

    fun resetDeleteCheckState() {
        _deleteCheckState.value = DeleteCheckState.Idle
    }
}

sealed class IdentifierListState {
    object Loading : IdentifierListState()
    data class Success(val items: List<IdentifierListItem>) : IdentifierListState()
    data class Error(val message: String) : IdentifierListState()
}