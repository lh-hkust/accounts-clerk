package com.hermes.presentation.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 更换验证渠道对话框
 *
 * 设计规范：
 * - 标题："更换验证渠道"
 * - 显示当前绑定的渠道和用途（如"手机号 138xxx - 验证、登录"）
 * - 新渠道选择列表：获取所有可用渠道，排除当前绑定的渠道
 * - 默认用途选择：保留原用途（自动勾选相同的用途）
 * - 可选展开："修改用途"按钮点击后显示用途选择气泡
 * - 确认按钮：调用 SwitchBindingIdentifierUseCase
 * - 确认更换后记录历史（BindingHistoryRecord）
 * - 使用深色主题风格
 *
 * 任务：21.5.1-21.5.4
 */
@Composable
fun SwitchBindingDialog(
    currentBinding: CurrentBindingInfo,
    availableIdentifiers: List<IdentifierOption>,
    onConfirm: (newIdentifierId: Long, newPurposes: Set<BindingPurpose>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 选中的新渠道ID
    var selectedNewIdentifierId by remember { mutableStateOf<Long?>(null) }
    // 选中的用途（默认保留原用途）
    var selectedPurposes by remember { mutableStateOf(currentBinding.purposes.toSet()) }
    // 是否展开用途选择区域
    var isPurposeExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 标题
                Text(
                    text = "更换验证渠道",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 当前绑定信息卡片
                CurrentBindingCard(currentBinding = currentBinding)

                Spacer(modifier = Modifier.height(20.dp))

                // 新渠道选择标题
                Text(
                    text = "选择新渠道",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 新渠道选择列表
                if (availableIdentifiers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HermesColors.SurfaceLight.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "没有可用的新渠道",
                            fontSize = 14.sp,
                            color = HermesColors.TextMuted
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableIdentifiers.forEach { identifier ->
                            NewIdentifierSelectionItem(
                                identifier = identifier,
                                selected = selectedNewIdentifierId == identifier.id,
                                onClick = { selectedNewIdentifierId = identifier.id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 用途选择区域（可展开）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(HermesColors.SurfaceLight.copy(alpha = 0.3f))
                        .clickable { isPurposeExpanded = !isPurposeExpanded }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPurposeExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = HermesColors.Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPurposeExpanded) "收起用途选择" else "修改用途",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.Primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (!isPurposeExpanded) {
                        // 显示当前用途摘要
                        Text(
                            text = getPurposesSummary(selectedPurposes),
                            fontSize = 12.sp,
                            color = HermesColors.TextSecondary
                        )
                    }
                }

                // 用途选择气泡（展开时显示）
                AnimatedVisibility(
                    visible = isPurposeExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        // 用途选择标题
                        Text(
                            text = "绑定用途",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = HermesColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 用途选择按钮（第一行）
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PurposeChip(
                                purpose = BindingPurpose.LOGIN,
                                label = "登录",
                                selected = selectedPurposes.contains(BindingPurpose.LOGIN),
                                onToggle = {
                                    selectedPurposes = if (selectedPurposes.contains(BindingPurpose.LOGIN)) {
                                        selectedPurposes - BindingPurpose.LOGIN
                                    } else {
                                        selectedPurposes + BindingPurpose.LOGIN
                                    }
                                }
                            )
                            PurposeChip(
                                purpose = BindingPurpose.VERIFICATION,
                                label = "验证",
                                selected = selectedPurposes.contains(BindingPurpose.VERIFICATION),
                                onToggle = {
                                    selectedPurposes = if (selectedPurposes.contains(BindingPurpose.VERIFICATION)) {
                                        selectedPurposes - BindingPurpose.VERIFICATION
                                    } else {
                                        selectedPurposes + BindingPurpose.VERIFICATION
                                    }
                                }
                            )
                            PurposeChip(
                                purpose = BindingPurpose.RECOVERY,
                                label = "找回",
                                selected = selectedPurposes.contains(BindingPurpose.RECOVERY),
                                onToggle = {
                                    selectedPurposes = if (selectedPurposes.contains(BindingPurpose.RECOVERY)) {
                                        selectedPurposes - BindingPurpose.RECOVERY
                                    } else {
                                        selectedPurposes + BindingPurpose.RECOVERY
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 用途选择按钮（第二行）
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PurposeChip(
                                purpose = BindingPurpose.NOTIFICATION,
                                label = "通知",
                                selected = selectedPurposes.contains(BindingPurpose.NOTIFICATION),
                                onToggle = {
                                    selectedPurposes = if (selectedPurposes.contains(BindingPurpose.NOTIFICATION)) {
                                        selectedPurposes - BindingPurpose.NOTIFICATION
                                    } else {
                                        selectedPurposes + BindingPurpose.NOTIFICATION
                                    }
                                }
                            )
                            PurposeChip(
                                purpose = BindingPurpose.SECONDARY_AUTH,
                                label = "二次验证",
                                selected = selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH),
                                onToggle = {
                                    selectedPurposes = if (selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH)) {
                                        selectedPurposes - BindingPurpose.SECONDARY_AUTH
                                    } else {
                                        selectedPurposes + BindingPurpose.SECONDARY_AUTH
                                    }
                                }
                            )
                        }

                        // 用途选择提示
                        if (selectedPurposes.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "请至少选择一个用途",
                                fontSize = 12.sp,
                                color = HermesColors.Danger
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.SurfaceLight.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    // 确认按钮
                    val canConfirm = selectedNewIdentifierId != null && selectedPurposes.isNotEmpty()
                    Button(
                        onClick = {
                            if (canConfirm) {
                                onConfirm(selectedNewIdentifierId!!, selectedPurposes)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canConfirm) HermesColors.Primary else HermesColors.Primary.copy(alpha = 0.5f)
                        ),
                        enabled = canConfirm
                    ) {
                        Text(
                            text = "确认更换",
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

/**
 * 当前绑定信息卡片
 */
@Composable
private fun CurrentBindingCard(
    currentBinding: CurrentBindingInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.SurfaceLight.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型图标
            Icon(
                imageVector = if (currentBinding.type == IdentifierType.PHONE)
                    Icons.Filled.Phone
                else
                    Icons.Filled.Email,
                contentDescription = null,
                tint = HermesColors.TextMuted
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 渠道值
                Text(
                    text = "${getTypeLabel(currentBinding.type)} ${currentBinding.value}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 用途标签
                if (currentBinding.purposes.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        currentBinding.purposes.forEach { purpose ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(HermesColors.Primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = getPurposeLabel(purpose),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.Primary
                                )
                            }
                        }
                    }
                }
            }

            // 当前绑定标记
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = HermesColors.TextMuted.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "当前绑定",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextMuted,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * 新渠道选择项
 */
@Composable
private fun NewIdentifierSelectionItem(
    identifier: IdentifierOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = getStatusColor(identifier.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (selected)
                    Modifier.border(1.dp, HermesColors.Primary, RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                HermesColors.Primary.copy(alpha = 0.1f)
            else
                HermesColors.SurfaceLight.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型图标
            Icon(
                imageVector = if (identifier.type == IdentifierType.PHONE)
                    Icons.Filled.Phone
                else
                    Icons.Filled.Email,
                contentDescription = null,
                tint = if (selected) HermesColors.Primary else HermesColors.TextMuted
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${getTypeLabel(identifier.type)} ${identifier.value}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected) HermesColors.TextPrimary else HermesColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 状态标签
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(statusColor)
                    )
                    Text(
                        text = getStatusLabel(identifier.status),
                        fontSize = 10.sp,
                        color = statusColor
                    )
                }
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "已选中",
                    tint = HermesColors.Primary
                )
            }
        }
    }
}

/**
 * 用途选择气泡
 */
@Composable
private fun PurposeChip(
    purpose: BindingPurpose,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) HermesColors.Primary.copy(alpha = 0.2f)
                else HermesColors.SurfaceLight.copy(alpha = 0.5f)
            )
            .then(
                if (selected)
                    Modifier.border(1.dp, HermesColors.Primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) HermesColors.Primary else HermesColors.TextSecondary
        )
    }
}

// ===== 辅助函数 =====

private fun getTypeLabel(type: IdentifierType): String {
    return when (type) {
        IdentifierType.PHONE -> "手机号"
        IdentifierType.EMAIL -> "邮箱"
    }
}

private fun getPurposeLabel(purpose: BindingPurpose): String {
    return when (purpose) {
        BindingPurpose.LOGIN -> "登录"
        BindingPurpose.VERIFICATION -> "验证"
        BindingPurpose.RECOVERY -> "找回"
        BindingPurpose.NOTIFICATION -> "通知"
        BindingPurpose.SECONDARY_AUTH -> "二次验证"
    }
}

private fun getPurposesSummary(purposes: Set<BindingPurpose>): String {
    return purposes.map { getPurposeLabel(it) }.joinToString("、")
}

private fun getStatusColor(status: IdentifierStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        IdentifierStatus.ACTIVE -> HermesColors.Success
        IdentifierStatus.PENDING_DEACTIVATION -> HermesColors.Warning
        IdentifierStatus.DEACTIVATED -> HermesColors.Danger
        IdentifierStatus.INVALIDATED -> HermesColors.Danger
    }
}

private fun getStatusLabel(status: IdentifierStatus): String {
    return when (status) {
        IdentifierStatus.ACTIVE -> "正常使用"
        IdentifierStatus.PENDING_DEACTIVATION -> "即将到期"
        IdentifierStatus.DEACTIVATED -> "已失效"
        IdentifierStatus.INVALIDATED -> "已失效"
    }
}

// ===== 数据类 =====

/**
 * 当前绑定信息
 */
data class CurrentBindingInfo(
    val identifierId: Long,
    val type: IdentifierType,
    val value: String,
    val purposes: List<BindingPurpose>
)