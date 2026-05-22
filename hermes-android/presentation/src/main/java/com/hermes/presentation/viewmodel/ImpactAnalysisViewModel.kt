package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.presentation.usecase.impact.AnalyzeImpactUseCase
import com.hermes.presentation.usecase.impact.ImpactAnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ImpactAnalysisState {
    object Loading : ImpactAnalysisState()
    data class Success(val result: ImpactAnalysisResult?) : ImpactAnalysisState()
    data class Error(val message: String) : ImpactAnalysisState()
}

@HiltViewModel
class ImpactAnalysisViewModel @Inject constructor(
    private val analyzeImpactUseCase: AnalyzeImpactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImpactAnalysisState>(ImpactAnalysisState.Loading)
    val uiState: StateFlow<ImpactAnalysisState> = _uiState.asStateFlow()

    fun analyzeImpact(identifierId: Long) {
        viewModelScope.launch {
            _uiState.value = ImpactAnalysisState.Loading
            try {
                val result = analyzeImpactUseCase(identifierId)
                _uiState.value = ImpactAnalysisState.Success(result)
            } catch (e: Exception) {
                _uiState.value = ImpactAnalysisState.Error(e.message ?: "Unknown error")
            }
        }
    }
}