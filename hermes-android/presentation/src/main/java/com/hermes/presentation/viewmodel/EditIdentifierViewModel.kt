package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.presentation.usecase.identifier.UpdateIdentifierUseCase
import com.hermes.presentation.usecase.deactivation.GetDeactivationDetailUseCase
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import com.hermes.presentation.usecase.deactivation.CancelDeactivationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 编辑标识 ViewModel
 */
@HiltViewModel
class EditIdentifierViewModel @Inject constructor(
    private val identifierRepository: IdentityIdentifierRepository,
    private val updateIdentifierUseCase: UpdateIdentifierUseCase,
    private val getDeactivationDetailUseCase: GetDeactivationDetailUseCase,
    private val cancelDeactivationUseCase: CancelDeactivationUseCase
) : ViewModel() {

    private val _identifier = MutableStateFlow<IdentityIdentifier?>(null)
    val identifier: StateFlow<IdentityIdentifier?> = _identifier.asStateFlow()

    private val _deactivationDetail = MutableStateFlow<DeactivationDetail?>(null)
    val deactivationDetail: StateFlow<DeactivationDetail?> = _deactivationDetail.asStateFlow()

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    fun loadIdentifier(identifierId: Long) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                val identifier = identifierRepository.getById(identifierId)
                if (identifier != null) {
                    _identifier.value = identifier
                    // 加载停用计划详情
                    val deactivation = getDeactivationDetailUseCase(identifierId)
                    _deactivationDetail.value = deactivation
                    _operationState.value = OperationState.Idle
                } else {
                    _operationState.value = OperationState.Error("标识不存在")
                }
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "加载失败")
            }
        }
    }

    fun updateRemark(remark: String?) {
        val identifier = _identifier.value
        if (identifier == null || identifier.id == null) {
            _operationState.value = OperationState.Error("标识不存在")
            return
        }

        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                updateIdentifierUseCase(identifier.id!!, remark)
                _operationState.value = OperationState.Success("备注已更新")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "保存失败")
            }
        }
    }

    fun cancelDeactivation() {
        val identifierId = _identifier.value?.id
        if (identifierId == null) {
            return
        }

        viewModelScope.launch {
            _operationState.value = OperationState.InProgress
            try {
                cancelDeactivationUseCase(identifierId, "用户取消")
                _deactivationDetail.value = null
                // 重新加载标识以更新状态
                loadIdentifier(identifierId)
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "取消失败")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}