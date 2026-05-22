package com.hermes.presentation.viewmodel

import com.hermes.presentation.usecase.account.AccountDetail

sealed class AccountDetailState {
    object Loading : AccountDetailState()
    data class Success(val detail: AccountDetail) : AccountDetailState()
    object NotFound : AccountDetailState()
    data class Error(val message: String) : AccountDetailState()
}