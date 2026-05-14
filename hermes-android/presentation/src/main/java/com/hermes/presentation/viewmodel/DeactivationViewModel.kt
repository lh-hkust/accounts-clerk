package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.presentation.usecase.deactivation.CancelDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.ScheduleDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.UpdateDeactivationDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DeactivationViewModel @Inject constructor(
    private val scheduleDeactivationUseCase: ScheduleDeactivationUseCase,
    private val cancelDeactivationUseCase: CancelDeactivationUseCase,
    private val updateDeactivationDateUseCase: UpdateDeactivationDateUseCase
) : ViewModel() {

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _deactivationType = MutableStateFlow(DeactivationType.PHONE_NUMBER_CHANGE)
    val deactivationType: StateFlow<DeactivationType> = _deactivationType.asStateFlow()

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setDeactivationType(type: DeactivationType) {
        _deactivationType.value = type
    }

    fun scheduleDeactivation(identifierId: Long) {
        val date = _selectedDate.value
        if (date == null) {
            _operationState.value = OperationState.Error("Please select a date")
            return
        }

        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                val scheduledTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                scheduleDeactivationUseCase(
                    identifierId,
                    scheduledTime,
                    "User scheduled deactivation",
                    _deactivationType.value
                )
                _operationState.value = OperationState.Success("Deactivation scheduled")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to schedule")
            }
        }
    }

    fun cancelDeactivation(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                cancelDeactivationUseCase(identifierId, "User cancelled")
                _operationState.value = OperationState.Success("Deactivation cancelled")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to cancel")
            }
        }
    }

    fun updateDeactivationDate(identifierId: Long, newDate: LocalDate) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                val newInstant = newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                updateDeactivationDateUseCase(identifierId, newInstant)
                _operationState.value = OperationState.Success("Date updated")
                _selectedDate.value = newDate
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to update")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}