package com.hermes.presentation.viewmodel

import com.hermes.presentation.usecase.identifier.BoundAccountInfo

/**
 * 共享的操作状态
 */
sealed class OperationState {
    object Idle : OperationState()
    object InProgress : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}

/**
 * 账户删除状态
 */
sealed class DeleteState {
    object Idle : DeleteState()
    object InProgress : DeleteState()
    data class Success(val accountName: String, val unboundCount: Int) : DeleteState()
    data class Error(val message: String) : DeleteState()
}

/**
 * 标识删除检查状态
 */
sealed class DeleteCheckState {
    object Idle : DeleteCheckState()
    object Loading : DeleteCheckState()
    data class CanDelete(val identifierId: Long) : DeleteCheckState()
    data class HasBindings(
        val identifierId: Long,
        val boundCount: Int,
        val boundAccounts: List<BoundAccountInfo>
    ) : DeleteCheckState()
    data class Error(val message: String) : DeleteCheckState()
}

/**
 * 列表加载状态基类
 */
sealed class ListState {
    object Loading : ListState()
    data class Error(val message: String) : ListState()
}