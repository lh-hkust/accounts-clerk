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
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem

/**
 * 标识卡片组件（支持手势交互）
 *
 * 手势定义：
 * - 点击：跳转详情页
 * - 长按：显示上下文菜单
 * - 右滑：设置到期提醒（ACTIVE）/ 修改到期提醒（PENDING）
 * - 左滑：标记已处理（PENDING）/ 无操作（其他状态）
 */
@Composable
fun IdentifierCard(
    item: IdentifierListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onSetReminder: () -> Unit = {},
    onModifyReminder: () -> Unit = {},
    onCancelReminder: () -> Unit = {},
    onMarkDeactivated: () -> Unit = {},
    onDelete: () -> Unit = {},
    onMarkHandled: () -> Unit = {}
) {
    val statusColor = getStatusColor(item.identifier.status)
    val typeColor = getTypeColor(item.identifier.type)

    // 菜单显示状态
    var showMenu by remember { mutableStateOf(false) }

    // 根据状态确定右滑/左滑按钮文字和可用性
    val rightSwipeEnabled = item.identifier.status == IdentifierStatus.ACTIVE ||
                            item.identifier.status == IdentifierStatus.PENDING_DEACTIVATION
    val leftSwipeEnabled = item.identifier.status == IdentifierStatus.PENDING_DEACTIVATION

    val rightSwipeLabel = when (item.identifier.status) {
        IdentifierStatus.ACTIVE -> "设置提醒"
        IdentifierStatus.PENDING_DEACTIVATION -> "修改提醒"
        else -> ""
    }

    val leftSwipeLabel = if (leftSwipeEnabled) "标记处理" else ""

    Box {
        SwipeableCard(
            content = {
                // 卡片内容
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
                            .background(typeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (item.identifier.type == IdentifierType.PHONE) "📱" else "📧",
                            style = MaterialTheme.typography.titleLarge,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 内容
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.identifier.value,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 状态标签
                            Surface(
                                modifier = Modifier.height(20.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = statusColor
                            ) {
                                Text(
                                    text = getStatusText(item.identifier.status),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HermesColors.TextPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "绑定 ${item.boundAccountCount} 个账号",
                                style = MaterialTheme.typography.bodySmall,
                                color = HermesColors.TextMuted
                            )
                        }
                    }

                    // 倒计时（如果有停用计划）
                    if (item.identifier.status == IdentifierStatus.PENDING_DEACTIVATION) {
                        val remainingDays = item.identifier.plannedDeactTime?.let { plannedTime ->
                            val now = java.time.Instant.now()
                            java.time.temporal.ChronoUnit.DAYS.between(now, plannedTime).toInt()
                        }
                        if (remainingDays != null && remainingDays >= 0) {
                            Text(
                                text = "${remainingDays}天后到期",
                                style = MaterialTheme.typography.labelMedium,
                                color = HermesColors.Warning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            onRightSwipe = {
                when (item.identifier.status) {
                    IdentifierStatus.ACTIVE -> onSetReminder()
                    IdentifierStatus.PENDING_DEACTIVATION -> onModifyReminder()
                    else -> {}
                }
            },
            onLeftSwipe = {
                if (leftSwipeEnabled) {
                    onMarkHandled()
                }
            },
            rightSwipeLabel = rightSwipeLabel,
            leftSwipeLabel = leftSwipeLabel,
            rightSwipeEnabled = rightSwipeEnabled,
            leftSwipeEnabled = leftSwipeEnabled,
            onClick = onClick,
            onLongPress = { showMenu = true },
            modifier = modifier.border(1.dp, HermesColors.CardBorder, RoundedCornerShape(16.dp))
        )

        // 上下文菜单
        if (showMenu) {
            ContextMenu(
                menuItems = getIdentifierMenuItems(
                    status = item.identifier.status,
                    onEdit = onEdit,
                    onSetReminder = onSetReminder,
                    onModifyReminder = onModifyReminder,
                    onCancelReminder = onCancelReminder,
                    onMarkDeactivated = onMarkDeactivated,
                    onDelete = onDelete
                ),
                onDismiss = { showMenu = false }
            )
        }
    }
}

private fun getStatusColor(status: IdentifierStatus): Color {
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

private fun getTypeColor(type: IdentifierType): Color {
    return when (type) {
        IdentifierType.PHONE -> HermesColors.PhoneColor
        IdentifierType.EMAIL -> HermesColors.EmailColor
    }
}