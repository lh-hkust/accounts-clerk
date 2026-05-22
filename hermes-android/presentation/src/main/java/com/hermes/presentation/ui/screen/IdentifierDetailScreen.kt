package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.presentation.ui.component.DeleteIdentifierBlockDialog
import com.hermes.presentation.ui.component.DeleteIdentifierConfirmDialog
import com.hermes.presentation.ui.component.DeactivationPlanCard
import com.hermes.presentation.ui.component.getIdentifierStatusColor
import com.hermes.presentation.ui.component.getIdentifierStatusText
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.IdentifierDetailState
import com.hermes.presentation.viewmodel.DeleteCheckState
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import com.hermes.presentation.usecase.identifier.BoundAccountInfo

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
    // 删除弹窗状态
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
                actions = {
                    if (canDelete) {
                        IconButton(onClick = {
                            when (uiState) {
                                is IdentifierDetailState.Success -> onCheckDelete(uiState.detail.identifier.id!!)
                                else -> {}
                            }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除")
                        }
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
                .background(HermesColors.Background)
        ) {
            when (uiState) {
                is IdentifierDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = HermesColors.Primary
                    )
                }
                is IdentifierDetailState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 标识详情卡片
                        item {
                            IdentifierDetailCard(
                                identifier = uiState.detail.identifier,
                                deactivationDetail = deactivationDetail,
                                onScheduleDeactivation = onScheduleDeactivation
                            )
                        }

                        // 停用计划卡片（如有）
                        if (deactivationDetail != null) {
                            item {
                                DeactivationPlanCard(
                                    detail = deactivationDetail,
                                    onCancelClick = { deactivationDetail.deactivation.id?.let { onCancelDeactivation(it) } },
                                    onModifyClick = onModifyDeactivation,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // 关联账号标题
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountTree,
                                    contentDescription = null,
                                    tint = HermesColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "关联账号",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${uiState.detail.boundAccountCount}个",
                                    fontSize = 12.sp,
                                    color = HermesColors.TextSecondary
                                )
                            }
                        }

                        // 关联账号列表
                        if (uiState.detail.boundAccounts.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "暂无关联账号",
                                            fontSize = 14.sp,
                                            color = HermesColors.TextMuted
                                        )
                                    }
                                }
                            }
                        } else {
                            items(uiState.detail.boundAccounts) { account ->
                                BoundAccountCard(
                                    account = account,
                                    onClick = { onAccountClick(account.accountId) }
                                )
                            }
                        }

                        // 操作按钮区域
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 批量更换渠道
                                Button(
                                    onClick = onBatchChange,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HermesColors.Primary
                                    )
                                ) {
                                    Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "批量更换",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                // 标记已处理（仅当有停用计划时）
                                if (deactivationDetail != null) {
                                    Button(
                                        onClick = { deactivationDetail.deactivation.identifierId?.let { onMarkHandled(it) } },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                                        )
                                    ) {
                                        Text(
                                            text = "标记已处理",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = HermesColors.TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is IdentifierDetailState.NotFound -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = HermesColors.TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("标识不存在", color = HermesColors.TextMuted)
                    }
                }
                is IdentifierDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = HermesColors.Danger,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(uiState.message, color = HermesColors.Danger)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("返回")
                        }
                    }
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

/**
 * 标识详情卡片 - 渐变背景，显示状态和到期信息
 */
@Composable
private fun IdentifierDetailCard(
    identifier: com.hermes.domain.model.IdentityIdentifier,
    deactivationDetail: DeactivationDetail?,
    onScheduleDeactivation: () -> Unit
) {
    // 状态颜色和文本
    val statusColor = getIdentifierStatusColor(identifier.status)
    val statusText = getIdentifierStatusText(identifier.status)

    // 边框颜色：根据状态
    val borderColor = when (identifier.status) {
        IdentifierStatus.PENDING_DEACTIVATION -> Color(0xFFeab308) // 黄色警告
        IdentifierStatus.DEACTIVATED -> HermesColors.Danger // 红色失效
        IdentifierStatus.INVALIDATED -> HermesColors.TextMuted // 灰色失效
        else -> HermesColors.Primary.copy(alpha = 0.3f) // 正常蓝色
    }

    // 渐变背景
    val gradientColors = when (identifier.status) {
        IdentifierStatus.PENDING_DEACTIVATION -> listOf(
            HermesColors.Primary.copy(alpha = 0.15f),
            Color(0xFFeab308).copy(alpha = 0.15f)
        )
        IdentifierStatus.DEACTIVATED -> listOf(
            HermesColors.Danger.copy(alpha = 0.15f),
            HermesColors.Danger.copy(alpha = 0.05f)
        )
        else -> listOf(
            HermesColors.Primary.copy(alpha = 0.2f),
            HermesColors.Secondary.copy(alpha = 0.2f)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(gradientColors),
                    RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                // 状态警告提示（仅即将失效/已失效显示）
                if (identifier.status == IdentifierStatus.PENDING_DEACTIVATION) {
                    Text(
                        text = "即将失效的验证渠道",
                        fontSize = 12.sp,
                        color = Color(0xFFeab308),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (identifier.status == IdentifierStatus.DEACTIVATED || identifier.status == IdentifierStatus.INVALIDATED) {
                    Text(
                        text = "已失效的验证渠道",
                        fontSize = 12.sp,
                        color = HermesColors.Danger,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 标识值（大号粗体）
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 类型图标
                    Icon(
                        imageVector = if (identifier.type == IdentifierType.EMAIL)
                            Icons.Filled.Email
                        else
                            Icons.Filled.Phone,
                        contentDescription = null,
                        tint = HermesColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = identifier.value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 到期倒计时（如有停用计划）
                if (deactivationDetail != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFFeab308),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "到期倒计时: ${deactivationDetail.remainingDays} 天",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                } else if (identifier.status == IdentifierStatus.ACTIVE) {
                    // 正常状态显示"设置提醒"按钮
                    Button(
                        onClick = onScheduleDeactivation,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "设置到期提醒",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 状态徽章
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }
        }
    }
}

/**
 * 绑定账号卡片 - 左边框颜色区分，显示用途标签
 */
@Composable
private fun BoundAccountCard(
    account: BoundAccountInfo,
    onClick: () -> Unit
) {
    // 应用边框颜色映射
    val borderColor = when {
        account.applicationName.contains("微信") -> HermesColors.Success // 绿色
        account.applicationName.contains("支付宝") || account.applicationName.contains("银行") -> HermesColors.Primary // 蓝色
        account.applicationName.contains("抖音") -> HermesColors.Secondary // 紫色
        account.applicationName.contains("淘宝") -> Color(0xFFff4400) // 橙色
        account.applicationName.contains("京东") -> HermesColors.Danger // 红色
        account.applicationName.contains("微博") -> Color(0xFFe6162d) // 红色
        account.applicationName.contains("QQ") -> Color(0xFF12b7f5) // 蓝色
        else -> HermesColors.Accent // 默认青色
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp)),
        shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 应用图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(borderColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = account.applicationName.take(1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 账号信息
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.applicationName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )
                    if (account.isPrimary) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(主账号)",
                            fontSize = 12.sp,
                            color = HermesColors.TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = account.accountName,
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
            }

            // 用途标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                account.purposes.forEach { purpose ->
                    PurposeTag(purpose = purpose)
                }
            }
        }
    }
}

/**
 * 用途标签 - 小圆角卡片
 */
@Composable
private fun PurposeTag(purpose: BindingPurpose) {
    val (text, color) = when (purpose) {
        BindingPurpose.LOGIN -> ("登录" to HermesColors.Success)
        BindingPurpose.VERIFICATION -> ("验证" to HermesColors.Primary)
        BindingPurpose.RECOVERY -> ("找回" to Color(0xFF2196F3))
        BindingPurpose.NOTIFICATION -> ("通知" to Color(0xFFFF9800))
        BindingPurpose.SECONDARY_AUTH -> ("二次验证" to HermesColors.Secondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}