package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.presentation.usecase.account.AccountDetail
import com.hermes.presentation.usecase.account.GetAccountDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 账户详情 ViewModel
 */
@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val getAccountDetailUseCase: GetAccountDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountDetailState>(AccountDetailState.Loading)
    val uiState: StateFlow<AccountDetailState> = _uiState.asStateFlow()

    /**
     * 加载账户详情
     */
    fun loadAccountDetail(accountId: Long) {
        viewModelScope.launch {
            _uiState.value = AccountDetailState.Loading
            try {
                val detail = getAccountDetailUseCase(accountId)
                if (detail != null) {
                    _uiState.value = AccountDetailState.Success(detail)
                } else {
                    _uiState.value = AccountDetailState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = AccountDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * 刷新账户详情
     */
    fun refresh(accountId: Long) {
        loadAccountDetail(accountId)
    }
}