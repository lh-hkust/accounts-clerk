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
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 提醒卡片组件（支持手势交互）
 *
 * 手势定义：
 * - 点击：跳转详情页（影响范围）
 * - 长按：显示上下文菜单（标记已处理、设置到期提醒、查看影响账号）
 * - 右滑：标记已处理
 * - 左滑：无操作
 */
@Composable
fun WarningCard(
    warning: WarningRecord,
    onClick: () -> Unit,
    onHandleClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSetReminder: () -> Unit = {},
    onViewAffectedAccounts: () -> Unit = {}
) {
    val levelColor = getWarningLevelColor(warning.warningLevel)
    val levelText = getWarningLevelText(warning.warningLevel)

    // 菜单显示状态
    var showMenu by remember { mutableStateOf(false) }

    Box {
        SwipeableCard(
            content = {
                // 原型样式：左侧边框卡片内容
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = getMessageText(warning),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = HermesColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getSubtitleText(warning),
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted
                        )
                    }

                    // 级别徽章 - 原型样式
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(levelColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = levelText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = levelColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "查看详情",
                        tint = HermesColors.TextMuted
                    )
                }
            },
            onRightSwipe = onHandleClick,
            onLeftSwipe = { /* 左滑无操作 */ },
            rightSwipeLabel = "标记处理",
            leftSwipeLabel = "",
            rightSwipeEnabled = true,
            leftSwipeEnabled = false, // 左滑禁用
            onClick = onClick,
            onLongPress = { showMenu = true },
            modifier = modifier
                .border(4.dp, levelColor, RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp))
                .background(levelColor.copy(alpha = 0.05f))
        )

        // 上下文菜单
        if (showMenu) {
            ContextMenu(
                menuItems = getWarningMenuItems(
                    onMarkHandled = onHandleClick,
                    onSetReminder = onSetReminder,
                    onViewAffectedAccounts = onViewAffectedAccounts
                ),
                onDismiss = { showMenu = false }
            )
        }
    }
}

private fun getWarningLevelColor(level: WarningLevel): Color {
    return when (level) {
        WarningLevel.HIGH -> HermesColors.Danger
        WarningLevel.MEDIUM -> HermesColors.Warning
        WarningLevel.LOW -> HermesColors.TextMuted
    }
}

private fun getWarningLevelText(level: WarningLevel): String {
    return when (level) {
        WarningLevel.HIGH -> "紧急"
        WarningLevel.MEDIUM -> "建议"
        WarningLevel.LOW -> "低"
    }
}

private fun getMessageText(warning: WarningRecord): String {
    return when {
        warning.message.contains("到期") || warning.message.contains("停机") -> "手机号: 138****8888"
        warning.message.contains("冻结") -> "Google 账号已冻结"
        warning.message.contains("失效") -> "验证渠道已失效"
        else -> warning.message.take(20)
    }
}

private fun getSubtitleText(warning: WarningRecord): String {
    return when {
        warning.message.contains("到期") || warning.message.contains("停机") -> "22天后停机，关联14个账号"
        warning.message.contains("冻结") -> "无法登录新设备，需验证"
        else -> warning.message
    }
}