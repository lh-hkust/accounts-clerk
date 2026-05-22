package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.model.WarningRecord
import com.hermes.presentation.usecase.warning.GetWarningListUseCase
import com.hermes.presentation.usecase.warning.HandleWarningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WarningListState {
    object Loading : WarningListState()
    data class Success(val items: List<WarningRecord>) : WarningListState()
    data class Error(val message: String) : WarningListState()
}

@HiltViewModel
class WarningViewModel @Inject constructor(
    private val getWarningListUseCase: GetWarningListUseCase,
    private val handleWarningUseCase: HandleWarningUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WarningListState>(WarningListState.Loading)
    val uiState: StateFlow<WarningListState> = _uiState.asStateFlow()

    private val _handledWarnings = MutableStateFlow<Set<Long>>(emptySet())
    val handledWarnings: StateFlow<Set<Long>> = _handledWarnings.asStateFlow()

    init {
        loadWarnings()
    }

    fun loadWarnings() {
        viewModelScope.launch {
            _uiState.value = WarningListState.Loading
            try {
                val items = getWarningListUseCase()
                val handled = getWarningListUseCase.getHandled().mapNotNull { it.id }.toSet()
                _uiState.value = WarningListState.Success(items)
                _handledWarnings.value = handled
            } catch (e: Exception) {
                _uiState.value = WarningListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun handleWarning(warningId: Long) {
        viewModelScope.launch {
            try {
                handleWarningUseCase(warningId)
                loadWarnings()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}