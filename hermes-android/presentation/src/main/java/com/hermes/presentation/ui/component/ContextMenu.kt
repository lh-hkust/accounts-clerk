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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 菜单项数据类
 */
data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit,
    val isDestructive: Boolean = false // 是否为危险操作（如删除）
)

/**
 * 动态上下文菜单组件
 *
 * 设计规范：
 * - 类型：DropdownMenu 或底部弹窗
 * - 菜单项高度：48dp
 * - 文字对齐：左对齐
 * - 分隔线：1dp，边框色 (#334155)
 * - 动画：淡入淡出 + 滑动效果
 *
 * @param menuItems 菜单项列表（动态根据状态显示）
 * @param onDismiss 关闭菜单回调
 * @param anchorPosition 菜单锚点位置（相对于屏幕）
 */
@Composable
fun ContextMenu(
    menuItems: List<MenuItem>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = modifier
            .background(HermesColors.Surface)
            .border(1.dp, HermesColors.Divider, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        menuItems.forEachIndexed { index, item ->
            // 菜单项
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (item.isDestructive) HermesColors.Danger else HermesColors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (item.isDestructive) HermesColors.Danger else HermesColors.TextPrimary
                        )
                    }
                },
                onClick = {
                    item.action()
                    onDismiss()
                },
                modifier = Modifier
                    .height(48.dp)
                    .background(HermesColors.Surface)
            )

            // 分隔线（除了最后一项）
            if (index < menuItems.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = HermesColors.Divider
                )
            }
        }

        // 取消按钮（最后一项）
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = HermesColors.Divider
        )
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "取消",
                        tint = HermesColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "取消",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextSecondary
                    )
                }
            },
            onClick = onDismiss,
            modifier = Modifier
                .height(48.dp)
                .background(HermesColors.Surface)
        )
    }
}

/**
 * 标识卡片的菜单项生成器
 *
 * 根据标识状态动态显示可用操作：
 * - ACTIVE: [编辑渠道, 设置到期提醒, 标记已失效, 删除渠道]
 * - PENDING_DEACTIVATION: [编辑渠道, 修改到期提醒, 取消到期提醒, 标记已失效, 删除渠道]
 * - DEACTIVATED: [编辑渠道, 删除渠道]
 */
fun getIdentifierMenuItems(
    status: com.hermes.domain.valueobject.IdentifierStatus,
    onEdit: () -> Unit,
    onSetReminder: () -> Unit,
    onModifyReminder: () -> Unit,
    onCancelReminder: () -> Unit,
    onMarkDeactivated: () -> Unit,
    onDelete: () -> Unit
): List<MenuItem> {
    return when (status) {
        com.hermes.domain.valueobject.IdentifierStatus.ACTIVE -> listOf(
            MenuItem("编辑渠道", Icons.Filled.Edit, onEdit),
            MenuItem("设置到期提醒", Icons.Filled.Schedule, onSetReminder),
            MenuItem("标记已失效", Icons.Filled.Block, onMarkDeactivated),
            MenuItem("删除渠道", Icons.Filled.Delete, onDelete, isDestructive = true)
        )
        com.hermes.domain.valueobject.IdentifierStatus.PENDING_DEACTIVATION -> listOf(
            MenuItem("编辑渠道", Icons.Filled.Edit, onEdit),
            MenuItem("修改到期提醒", Icons.Filled.EditCalendar, onModifyReminder),
            MenuItem("取消到期提醒", Icons.Filled.Cancel, onCancelReminder),
            MenuItem("标记已失效", Icons.Filled.Block, onMarkDeactivated),
            MenuItem("删除渠道", Icons.Filled.Delete, onDelete, isDestructive = true)
        )
        com.hermes.domain.valueobject.IdentifierStatus.DEACTIVATED -> listOf(
            MenuItem("编辑渠道", Icons.Filled.Edit, onEdit),
            MenuItem("删除渠道", Icons.Filled.Delete, onDelete, isDestructive = true)
        )
        com.hermes.domain.valueobject.IdentifierStatus.INVALIDATED -> listOf(
            MenuItem("编辑渠道", Icons.Filled.Edit, onEdit),
            MenuItem("删除渠道", Icons.Filled.Delete, onDelete, isDestructive = true)
        )
    }
}

/**
 * 账号卡片的菜单项生成器
 *
 * 菜单项：[编辑账号, 更换验证渠道, 变更账号状态, 删除账号, 取消]
 */
fun getAccountMenuItems(
    onEdit: () -> Unit,
    onChangeIdentifier: () -> Unit,
    onChangeStatus: () -> Unit,
    onDelete: () -> Unit
): List<MenuItem> {
    return listOf(
        MenuItem("编辑账号", Icons.Filled.Edit, onEdit),
        MenuItem("更换验证渠道", Icons.Filled.SwapHoriz, onChangeIdentifier),
        MenuItem("变更账号状态", Icons.Filled.ToggleOn, onChangeStatus),
        MenuItem("删除账号", Icons.Filled.Delete, onDelete, isDestructive = true)
    )
}

/**
 * 预警卡片的菜单项生成器
 *
 * 菜单项：[标记已处理, 设置到期提醒, 查看影响账号, 取消]
 */
fun getWarningMenuItems(
    onMarkHandled: () -> Unit,
    onSetReminder: () -> Unit,
    onViewAffectedAccounts: () -> Unit
): List<MenuItem> {
    return listOf(
        MenuItem("标记已处理", Icons.Filled.Check, onMarkHandled),
        MenuItem("设置到期提醒", Icons.Filled.Schedule, onSetReminder),
        MenuItem("查看影响账号", Icons.Filled.Visibility, onViewAffectedAccounts)
    )
}