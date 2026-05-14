package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.DeleteIdentifierBlockDialog
import com.hermes.presentation.ui.component.DeleteIdentifierConfirmDialog
import com.hermes.presentation.ui.component.IdentifierCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem
import com.hermes.presentation.viewmodel.IdentifierListState
import com.hermes.presentation.viewmodel.DeleteCheckState

/**
 * 标识列表页面（支持卡片手势交互）
 */
@Composable
fun IdentifierListScreen(
    uiState: IdentifierListState,
    deleteCheckState: DeleteCheckState = DeleteCheckState.Idle,
    onIdentifierClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditIdentifier: (Long) -> Unit = {},
    onSetReminder: (Long) -> Unit = {},
    onModifyReminder: (Long) -> Unit = {},
    onCancelReminder: (Long) -> Unit = {},
    onMarkDeactivated: (Long) -> Unit = {},
    onCheckDelete: (Long) -> Unit = {},
    onConfirmDelete: (Long) -> Unit = {},
    onViewBoundAccounts: (Long) -> Unit = {},
    onAccountClick: (Long) -> Unit = {},
    onResetDeleteCheckState: () -> Unit = {},
    onMarkHandled: (Long) -> Unit = {}
) {
    // 删除弹窗状态
    var showBlockDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    var pendingDeleteValue by remember { mutableStateOf("") }

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

    // 获取要删除的标识值（从列表中查找）
    LaunchedEffect(uiState, pendingDeleteId) {
        if (pendingDeleteId != null && uiState is IdentifierListState.Success) {
            val item = uiState.items.find { it.identifier.id == pendingDeleteId }
            pendingDeleteValue = item?.identifier?.value ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background)
    ) {
        when (uiState) {
            is IdentifierListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
            }
            is IdentifierListState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.items) { item ->
                        val identifierId = item.identifier.id
                        if (identifierId != null) {
                            IdentifierCard(
                                item = item,
                                onClick = { onIdentifierClick(identifierId) },
                                onEdit = { onEditIdentifier(identifierId) },
                                onSetReminder = { onSetReminder(identifierId) },
                                onModifyReminder = { onModifyReminder(identifierId) },
                                onCancelReminder = { onCancelReminder(identifierId) },
                                onMarkDeactivated = { onMarkDeactivated(identifierId) },
                                onDelete = { onCheckDelete(identifierId) },
                                onMarkHandled = { onMarkHandled(identifierId) }
                            )
                        }
                    }
                }
            }
            is IdentifierListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
            onDismiss = {
                showBlockDialog = false
                onResetDeleteCheckState()
            },
            onViewBoundAccounts = { onViewBoundAccounts(deleteCheckState.identifierId) },
            onAccountClick = onAccountClick
        )
    }

    // 删除确认弹窗（无绑定账号）
    if (showConfirmDialog) {
        DeleteIdentifierConfirmDialog(
            identifierValue = pendingDeleteValue,
            onDismiss = {
                showConfirmDialog = false
                onResetDeleteCheckState()
            },
            onConfirm = {
                pendingDeleteId?.let { onConfirmDelete(it) }
                showConfirmDialog = false
            }
        )
    }
}