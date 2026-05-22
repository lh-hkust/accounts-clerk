package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 账号状态选择对话框
 *
 * 设计规范：
 * - 标题："变更账号状态"
 * - 当前状态显示，标记为"当前状态"且不可选择
 * - 其他状态可选择，每个选项下方显示说明文字
 * - 无状态机限制：任何状态可直接切换到任何状态
 * - 使用状态对应的颜色标识
 *
 * 任务：21.4.1-21.4.3
 */
@Composable
fun AccountStatusSelectionDialog(
    currentStatus: AccountStatus,
    onStatusSelected: (AccountStatus) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // 标题
                Text(
                    text = "变更账号状态",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 提示说明
                Text(
                    text = "选择新的账号状态，当前状态不可选择",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 状态选项列表
                AccountStatus.values().forEach { status ->
                    val isSelected = status == currentStatus
                    val statusInfo = getStatusInfo(status)
                    val statusColor = getAccountStatusColor(status)

                    StatusOptionItem(
                        status = status,
                        statusText = statusInfo.text,
                        description = statusInfo.description,
                        statusColor = statusColor,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onStatusSelected(status)
                            }
                        }
                    )

                    if (status != AccountStatus.values().last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 取消按钮
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = HermesColors.TextSecondary
                    )
                ) {
                    Text("取消", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatusOptionItem(
    status: AccountStatus,
    statusText: String,
    description: String,
    statusColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) HermesColors.Divider else statusColor.copy(alpha = 0.5f)
    val backgroundColor = if (isSelected) HermesColors.SurfaceLight else HermesColors.SurfaceLight.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .then(
                if (!isSelected) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 状态颜色指示点
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(statusColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = statusText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) HermesColors.TextMuted else HermesColors.TextPrimary
                )

                if (isSelected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    // 当前状态标记
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = HermesColors.TextMuted.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "当前状态",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = HermesColors.TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = if (isSelected) HermesColors.TextMuted.copy(alpha = 0.7f) else HermesColors.TextSecondary
            )
        }

        // 状态图标（非当前状态显示可选图标）
        if (!isSelected) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "选择",
                tint = HermesColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        } else {
            // 当前状态显示锁定图标
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "当前状态不可选",
                tint = HermesColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 获取状态显示信息
 */
private fun getStatusInfo(status: AccountStatus): StatusInfo {
    return when (status) {
        AccountStatus.ACTIVE -> StatusInfo(
            text = "正常使用",
            description = "账号正常可用"
        )
        AccountStatus.FROZEN -> StatusInfo(
            text = "已冻结",
            description = "账号暂时冻结"
        )
        AccountStatus.LOST -> StatusInfo(
            text = "已丢失",
            description = "账号凭证丢失"
        )
        AccountStatus.ARCHIVED -> StatusInfo(
            text = "已归档",
            description = "账号已归档保存"
        )
    }
}

private data class StatusInfo(
    val text: String,
    val description: String
)