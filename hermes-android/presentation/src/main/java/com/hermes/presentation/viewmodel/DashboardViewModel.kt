package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.presentation.usecase.account.GetAccountListUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierListUseCase
import com.hermes.presentation.usecase.warning.GetWarningListUseCase
import com.hermes.presentation.usecase.warning.HandleWarningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IdentifierStats(
    val totalCount: Int,
    val activeCount: Int,
    val scheduledCount: Int
)

data class AccountStats(
    val totalCount: Int,
    val activeCount: Int,
    val frozenCount: Int
)

data class DashboardWarning(
    val id: Long?,
    val message: String,
    val level: String,
    val identifierType: String,
    val identifierValue: String
)

data class DashboardUiState(
    val identifierStats: IdentifierStats = IdentifierStats(0, 0, 0),
    val accountStats: AccountStats = AccountStats(0, 0, 0),
    val warnings: List<DashboardWarning> = emptyList(),
    val unhandledWarningCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getIdentifierListUseCase: GetIdentifierListUseCase,
    private val getAccountListUseCase: GetAccountListUseCase,
    private val getWarningListUseCase: GetWarningListUseCase,
    private val handleWarningUseCase: HandleWarningUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            try {
                val identifiers = getIdentifierListUseCase()
                val accounts = getAccountListUseCase()
                val warnings = getWarningListUseCase()

                val identifierStats = IdentifierStats(
                    totalCount = identifiers.size,
                    activeCount = identifiers.count { it.identifier.status == com.hermes.domain.valueobject.IdentifierStatus.ACTIVE },
                    scheduledCount = identifiers.count { it.identifier.status == com.hermes.domain.valueobject.IdentifierStatus.PENDING_DEACTIVATION }
                )
                val accountStats = AccountStats(
                    totalCount = accounts.size,
                    activeCount = accounts.count { it.account.status == com.hermes.domain.valueobject.AccountStatus.ACTIVE },
                    frozenCount = accounts.count { it.account.status == com.hermes.domain.valueobject.AccountStatus.FROZEN }
                )
                val dashboardWarnings = warnings.filter { !it.isHandled }.take(5).map { w ->
                    DashboardWarning(
                        id = w.id,
                        message = w.message,
                        level = w.warningLevel.name,
                        identifierType = "验证渠道",
                        identifierValue = "ID: ${w.identifierId}"
                    )
                }

                _uiState.value = DashboardUiState(
                    identifierStats = identifierStats,
                    accountStats = accountStats,
                    warnings = dashboardWarnings,
                    unhandledWarningCount = warnings.count { !it.isHandled },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun handleWarning(warningId: Long) {
        viewModelScope.launch {
            try {
                handleWarningUseCase(warningId)
                loadDashboardData()
            } catch (e: Exception) {
                // Handle error silently for quick action
            }
        }
    }
}