package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.presentation.usecase.deactivation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * 停用计划 ViewModel
 */
class DeactivationViewModel(
    private val scheduleDeactivationUseCase: ScheduleDeactivationUseCase,
    private val cancelDeactivationUseCase: CancelDeactivationUseCase,
    private val updateDeactivationDateUseCase: UpdateDeactivationDateUseCase,
    private val getDeactivationDetailUseCase: GetDeactivationDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeactivationDetailState>(DeactivationDetailState.Loading)
    val uiState: StateFlow<DeactivationDetailState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    fun loadDeactivationDetail(identifierId: Long) {
        viewModelScope.launch {
            _uiState.value = DeactivationDetailState.Loading
            try {
                val detail = getDeactivationDetailUseCase(identifierId)
                if (detail != null) {
                    _uiState.value = DeactivationDetailState.Success(detail)
                } else {
                    _uiState.value = DeactivationDetailState.NoPlan
                }
            } catch (e: Exception) {
                _uiState.value = DeactivationDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun scheduleDeactivation(
        identifierId: Long,
        scheduledTime: Instant,
        reason: String,
        type: DeactivationType
    ) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                scheduleDeactivationUseCase(identifierId, scheduledTime, reason, type)
                _operationState.value = OperationState.Success("Deactivation plan scheduled")
                loadDeactivationDetail(identifierId)
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to schedule")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun cancelDeactivation(identifierId: Long, cancelReason: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                cancelDeactivationUseCase(identifierId, cancelReason)
                _operationState.value = OperationState.Success("Deactivation plan cancelled")
                _uiState.value = DeactivationDetailState.NoPlan
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to cancel")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun updateDeactivationDate(identifierId: Long, newScheduledTime: Instant) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                updateDeactivationDateUseCase(identifierId, newScheduledTime)
                _operationState.value = OperationState.Success("Deactivation date updated")
                loadDeactivationDetail(identifierId)
            } catch (e: IllegalArgumentException) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to update date")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("Unknown error")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

sealed class DeactivationDetailState {
    object Loading : DeactivationDetailState()
    data class Success(val detail: DeactivationDetail) : DeactivationDetailState()
    object NoPlan : DeactivationDetailState()
    data class Error(val message: String) : DeactivationDetailState()
}