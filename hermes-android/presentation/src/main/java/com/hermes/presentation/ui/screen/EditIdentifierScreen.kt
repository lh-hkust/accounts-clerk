package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.DeactivationPlanCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import com.hermes.presentation.viewmodel.OperationState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 编辑验证渠道页面
 *
 * 设计规范：
 * - 类型（PHONE/EMAIL）：显示图标和文字，不可编辑
 * - 渠道值（手机号/邮箱）：显示完整值，不可编辑
 * - 状态标签：显示当前状态，不可编辑
 * - 备注字段：可编辑
 * - 停用计划：如果有，显示详情卡片（不可编辑，通过其他入口修改）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIdentifierScreen(
    identifierType: IdentifierType,
    identifierValue: String,
    identifierStatus: IdentifierStatus,
    plannedDeactTime: Instant?,
    deactReason: String?,
    currentRemark: String?,
    deactivationDetail: DeactivationDetail?,
    operationState: OperationState,
    onBackClick: () -> Unit,
    onSaveClick: (String?) -> Unit,
    onCancelDeactivation: () -> Unit,
    onModifyDeactivation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remark by remember { mutableStateOf(currentRemark ?: "") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("编辑渠道", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(HermesColors.Background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 类型卡片（不可编辑）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 类型图标
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (identifierType) {
                                    IdentifierType.PHONE -> HermesColors.PhoneColor
                                    IdentifierType.EMAIL -> HermesColors.EmailColor
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (identifierType == IdentifierType.PHONE) "📱" else "📧",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 类型文字
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "类型",
                            style = MaterialTheme.typography.bodySmall,
                            color = HermesColors.TextMuted
                        )
                        Text(
                            text = if (identifierType == IdentifierType.PHONE) "手机号" else "邮箱",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    // 锁定图标表示不可编辑
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "不可编辑",
                        tint = HermesColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 渠道值卡片（不可编辑）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (identifierType == IdentifierType.PHONE) "手机号" else "邮箱地址",
                            style = MaterialTheme.typography.bodySmall,
                            color = HermesColors.TextMuted
                        )
                        Text(
                            text = identifierValue,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    // 锁定图标表示不可编辑
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "不可编辑",
                        tint = HermesColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 状态卡片（不可编辑）
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "状态",
                            style = MaterialTheme.typography.bodySmall,
                            color = HermesColors.TextMuted
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.height(20.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = getStatusColor(identifierStatus)
                            ) {
                                Text(
                                    text = getStatusText(identifierStatus),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                            if (plannedDeactTime != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatTime(plannedDeactTime),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HermesColors.Warning
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "不可编辑",
                        tint = HermesColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 备注字段（可编辑）
            OutlinedTextField(
                value = remark,
                onValueChange = { remark = it },
                label = { Text("备注") },
                placeholder = { Text("添加备注信息（可选）") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.Divider,
                    focusedContainerColor = HermesColors.SurfaceLight,
                    unfocusedContainerColor = HermesColors.Surface,
                    focusedTextColor = HermesColors.TextPrimary,
                    unfocusedTextColor = HermesColors.TextPrimary,
                    cursorColor = HermesColors.Primary
                )
            )

            // 停用计划卡片（如果有）
            if (deactivationDetail != null) {
                DeactivationPlanCard(
                    detail = deactivationDetail,
                    onCancelClick = onCancelDeactivation,
                    onModifyClick = onModifyDeactivation,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 操作状态提示
            when (operationState) {
                is OperationState.InProgress -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = HermesColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("保存中...", color = HermesColors.TextSecondary)
                    }
                }
                is OperationState.Error -> {
                    Text(
                        text = operationState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.Danger
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮
            Button(
                onClick = { onSaveClick(remark.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is OperationState.InProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                )
            ) {
                Text("保存")
            }
        }
    }
}

private fun getStatusColor(status: IdentifierStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        IdentifierStatus.ACTIVE -> HermesColors.Success
        IdentifierStatus.PENDING_DEACTIVATION -> HermesColors.Warning
        IdentifierStatus.DEACTIVATED -> HermesColors.Danger
        IdentifierStatus.INVALIDATED -> HermesColors.TextMuted
    }
}

private fun getStatusText(status: IdentifierStatus): String {
    return when (status) {
        IdentifierStatus.ACTIVE -> "正常使用"
        IdentifierStatus.PENDING_DEACTIVATION -> "即将到期"
        IdentifierStatus.DEACTIVATED -> "已失效"
        IdentifierStatus.INVALIDATED -> "已失效"
    }
}

private fun formatTime(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}