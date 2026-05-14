package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.presentation.ui.component.DeleteIdentifierBlockDialog
import com.hermes.presentation.ui.component.DeleteIdentifierConfirmDialog
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.IdentifierDetailState
import com.hermes.presentation.viewmodel.DeleteCheckState
import com.hermes.presentation.usecase.deactivation.DeactivationDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifierDetailScreen(
    uiState: IdentifierDetailState,
    deactivationDetail: DeactivationDetail?,
    deleteCheckState: DeleteCheckState = DeleteCheckState.Idle,
    onBackClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onCheckDelete: (Long) -> Unit = {},
    onConfirmDelete: (Long) -> Unit = {},
    onAccountClick: (Long) -> Unit,
    onCancelDeactivation: (Long) -> Unit,
    onModifyDeactivation: () -> Unit,
    onScheduleDeactivation: () -> Unit,
    onBatchChange: () -> Unit,
    onMarkHandled: (Long) -> Unit,
    onViewBoundAccounts: (Long) -> Unit = {},
    canDelete: Boolean,
    modifier: Modifier = Modifier
) {
    // 删除阻止弹窗状态
    var showBlockDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    // 处理删除检查状态
    LaunchedEffect(deleteCheckState) {
        when (deleteCheckState) {
            is DeleteCheckState.HasBindings -> {
                showBlockDialog = true
            }
            is DeleteCheckState.CanDelete -> {
                showConfirmDialog = true
                pendingDeleteId = deleteCheckState.identifierId
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("影响范围", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is IdentifierDetailState.Loading -> {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
                is IdentifierDetailState.Success -> {
                    Text("标识: ${uiState.detail.identifier.value}", color = HermesColors.TextPrimary)
                }
                is IdentifierDetailState.NotFound -> {
                    Text("标识不存在", color = HermesColors.TextMuted)
                }
                is IdentifierDetailState.Error -> {
                    Text(uiState.message, color = HermesColors.Danger)
                }
            }
        }
    }

    // 删除阻止弹窗（有绑定账号）
    if (showBlockDialog && deleteCheckState is DeleteCheckState.HasBindings) {
        DeleteIdentifierBlockDialog(
            boundAccountCount = deleteCheckState.boundCount,
            boundAccounts = deleteCheckState.boundAccounts,
            onDismiss = { showBlockDialog = false },
            onViewBoundAccounts = { onViewBoundAccounts(deleteCheckState.identifierId) },
            onAccountClick = onAccountClick
        )
    }

    // 删除确认弹窗（无绑定账号）
    if (showConfirmDialog) {
        val identifierValue = when (uiState) {
            is IdentifierDetailState.Success -> uiState.detail.identifier.value
            else -> ""
        }
        DeleteIdentifierConfirmDialog(
            identifierValue = identifierValue,
            onDismiss = { showConfirmDialog = false },
            onConfirm = {
                pendingDeleteId?.let { onConfirmDelete(it) }
                showConfirmDialog = false
            }
        )
    }
}