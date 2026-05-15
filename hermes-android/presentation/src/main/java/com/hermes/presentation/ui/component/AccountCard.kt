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
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.account.AccountListItem
// Note: getAccountStatusColor and getAccountStatusText are defined in StatusMapping.kt in the same package

/**
 * 账户卡片组件（支持手势交互）
 *
 * 手势定义：
 * - 点击：跳转详情页
 * - 镋按：显示上下文菜单（编辑账号、更换验证渠道、变更账号状态、删除账号）
 * - 右滑：编辑账号
 * - 左滑：删除账号
 */
@Composable
fun AccountCard(
    item: AccountListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onChangeIdentifier: () -> Unit = {},
    onChangeStatus: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val statusColor = getAccountStatusColor(item.account.status)

    // 菜单显示状态
    var showMenu by remember { mutableStateOf(false) }

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
                    // 应用图标
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(getAppIconColor(item.applicationName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.applicationName.take(2),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 内容
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // 显示名称（优先昵称）
                        Text(
                            text = item.account.nickname ?: item.account.accountName,
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
                                    text = getAccountStatusText(item.account.status),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HermesColors.TextPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.applicationName,
                                style = MaterialTheme.typography.bodySmall,
                                color = HermesColors.TextMuted
                            )
                        }
                    }

                    // 分类标签
                    if (item.applicationCategory != null) {
                        Surface(
                            modifier = Modifier.height(20.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = HermesColors.SurfaceLight
                        ) {
                            Text(
                                text = item.applicationCategory,
                                style = MaterialTheme.typography.labelSmall,
                                color = HermesColors.TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }
                }
            },
            onRightSwipe = onEdit,
            onLeftSwipe = onDelete,
            rightSwipeLabel = "编辑",
            leftSwipeLabel = "删除",
            rightSwipeEnabled = true,
            leftSwipeEnabled = true,
            onClick = onClick,
            onLongPress = { showMenu = true },
            modifier = modifier.border(1.dp, HermesColors.CardBorder, RoundedCornerShape(16.dp))
        )

        // 上下文菜单
        if (showMenu) {
            ContextMenu(
                menuItems = getAccountMenuItems(
                    onEdit = onEdit,
                    onChangeIdentifier = onChangeIdentifier,
                    onChangeStatus = onChangeStatus,
                    onDelete = onDelete
                ),
                onDismiss = { showMenu = false }
            )
        }
    }
}

private fun getAppIconColor(appName: String): Color {
    return when {
        appName.contains("微信") -> Color(0xFF07c160)
        appName.contains("支付宝") -> Color(0xFF1677ff)
        appName.contains("微博") -> Color(0xFFe6162d)
        appName.contains("抖音") -> Color(0xFF000000)
        appName.contains("淘宝") -> Color(0xFFff4400)
        appName.contains("京东") -> Color(0xFFe53935)
        appName.contains("QQ") -> Color(0xFF12b7f5)
        appName.contains("招商银行") || appName.contains("银行") -> Color(0xFF1677ff)
        else -> HermesColors.Primary
    }
}