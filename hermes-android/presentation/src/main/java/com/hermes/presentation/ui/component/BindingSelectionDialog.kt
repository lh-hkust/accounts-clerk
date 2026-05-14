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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 绑定选择弹窗（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BindingSelectionDialog(
    availableIdentifiers: List<IdentifierOption>,
    selectedIdentifierId: Long?,
    selectedPurposes: Set<BindingPurpose>,
    onIdentifierSelected: (Long) -> Unit,
    onPurposeToggle: (BindingPurpose) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // 标题
                Text(
                    text = "选择验证渠道",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 可用渠道列表 - 原型样式
                availableIdentifiers.forEach { identifier ->
                    IdentifierSelectionItem(
                        identifier = identifier,
                        selected = selectedIdentifierId == identifier.id,
                        onClick = { onIdentifierSelected(identifier.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 用途选择标题
                Text(
                    text = "绑定用途",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 用途选择按钮 - 原型样式
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PurposeChip(
                        purpose = BindingPurpose.LOGIN,
                        label = "登录",
                        selected = selectedPurposes.contains(BindingPurpose.LOGIN),
                        onToggle = { onPurposeToggle(BindingPurpose.LOGIN) }
                    )
                    PurposeChip(
                        purpose = BindingPurpose.VERIFICATION,
                        label = "验证",
                        selected = selectedPurposes.contains(BindingPurpose.VERIFICATION),
                        onToggle = { onPurposeToggle(BindingPurpose.VERIFICATION) }
                    )
                    PurposeChip(
                        purpose = BindingPurpose.RECOVERY,
                        label = "找回",
                        selected = selectedPurposes.contains(BindingPurpose.RECOVERY),
                        onToggle = { onPurposeToggle(BindingPurpose.RECOVERY) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PurposeChip(
                        purpose = BindingPurpose.NOTIFICATION,
                        label = "通知",
                        selected = selectedPurposes.contains(BindingPurpose.NOTIFICATION),
                        onToggle = { onPurposeToggle(BindingPurpose.NOTIFICATION) }
                    )
                    PurposeChip(
                        purpose = BindingPurpose.SECONDARY_AUTH,
                        label = "二次验证",
                        selected = selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH),
                        onToggle = { onPurposeToggle(BindingPurpose.SECONDARY_AUTH) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮 - 原型样式
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Primary
                        )
                    ) {
                        Text(
                            text = "确认绑定",
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

@Composable
private fun IdentifierSelectionItem(
    identifier: IdentifierOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (selected)
                    Modifier.border(1.dp, HermesColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                HermesColors.Primary.copy(alpha = 0.1f)
            else
                HermesColors.Surface.copy(alpha = 0.5f)
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

            Text(
                text = identifier.value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) HermesColors.TextPrimary else HermesColors.TextMuted
            )

            Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun PurposeChip(
    purpose: BindingPurpose,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) HermesColors.Primary.copy(alpha = 0.2f)
                else HermesColors.Surface.copy(alpha = 0.5f)
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

/**
 * 标识选项
 */
data class IdentifierOption(
    val id: Long,
    val type: IdentifierType,
    val value: String,
    val status: com.hermes.domain.valueobject.IdentifierStatus
)