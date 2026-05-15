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
import com.hermes.presentation.ui.component.GestureHintOverlay
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
    gestureHintShown: Boolean = true,
    searchQuery: String = "",
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
    onMarkHandled: (Long) -> Unit = {},
    onGestureHintDismissed: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {}
) {
    // 删除弹窗状态
    var showBlockDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    var pendingDeleteValue by remember { mutableStateOf("") }

    // 手势提示状态
    var showGestureHint by remember { mutableStateOf(!gestureHintShown) }

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
        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = {
                Text(
                    text = "搜索验证渠道...",
                    color = HermesColors.TextMuted,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "搜索",
                    tint = HermesColors.TextMuted
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "清除",
                            tint = HermesColors.TextMuted
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = HermesColors.Surface,
                unfocusedContainerColor = HermesColors.Surface,
                focusedBorderColor = HermesColors.Primary,
                unfocusedBorderColor = HermesColors.CardBorder,
                cursorColor = HermesColors.Primary
            )
        )

        // 手势提示
        if (showGestureHint && uiState is IdentifierListState.Success && uiState.items.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                GestureHintOverlay(
                    message = "尝试滑动卡片进行操作",
                    onDismiss = {
                        showGestureHint = false
                        onGestureHintDismissed()
                    }
                )
            }
        }

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
                if (uiState.items.isEmpty()) {
                    // 区分搜索无结果和列表为空两种场景
                    if (searchQuery.isNotEmpty()) {
                        // 搜索无结果
                        SearchEmptyResultHint(
                            query = searchQuery,
                            onClearSearch = { onSearchQueryChange("") }
                        )
                    } else {
                        // 列表为空
                        EmptyIdentifierStateCard(onAddClick = onAddClick)
                    }
                } else {
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

/**
 * 空状态卡片组件
 */
@Composable
fun EmptyIdentifierStateCard(
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = HermesColors.Primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "暂无验证渠道",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "添加手机号或邮箱，开始追踪账号绑定关系",
                    fontSize = 14.sp,
                    color = HermesColors.TextMuted
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "添加验证渠道",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 搜索无结果提示组件
 */
@Composable
fun SearchEmptyResultHint(
    query: String,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "未找到匹配结果",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "请尝试其他关键词",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
            }
        }
    }
}