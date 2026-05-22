package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 受影响账户列表组件（与原型一致）
 */
@Composable
fun AffectedAccountsList(
    accounts: List<AffectedAccountInfo>,
    onAccountClick: (Long) -> Unit,
    showWarning: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        accounts.forEach { account ->
            AffectedAccountCard(
                account = account,
                onClick = { onAccountClick(account.accountId) },
                showWarning = showWarning && !account.isPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AffectedAccountCard(
    account: AffectedAccountInfo,
    onClick: () -> Unit,
    showWarning: Boolean = false
) {
    val borderColor = when (account.category) {
        "社交" -> HermesColors.Success
        "金融" -> HermesColors.Primary
        "购物" -> HermesColors.Secondary.copy(alpha = 0.5f)
        else -> HermesColors.Accent.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp) // 原型样式：左边框偏移
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 应用图标 - 原型样式
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(com.hermes.presentation.ui.util.AppColorUtils.getAppColor(account.applicationName)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = account.applicationName.take(2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${account.applicationName} (${account.accountName})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 用途标签 - 原型样式
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        account.purposes.forEach { purpose ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(HermesColors.Primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = getPurposeText(purpose),
                                    fontSize = 12.sp,
                                    color = HermesColors.Primary
                                )
                            }
                        }
                    }
                }

                if (account.isPrimary) {
                    Text(
                        text = "主要渠道",
                        fontSize = 12.sp,
                        color = HermesColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 失联警告 - 原型样式
            if (showWarning) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(start = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠ 渠道失效后将失联",
                        fontSize = 12.sp,
                        color = HermesColors.Warning
                    )
                }
            }

            // 子账号列表（如果有）- 原型样式：树状视图
            if (account.subAccounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                account.subAccounts.forEach { subAccount ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 子账号图标
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(com.hermes.presentation.ui.util.AppColorUtils.getAppColor(account.applicationName).copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = account.applicationName.take(2),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${account.applicationName} - ${subAccount.accountName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = HermesColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚠ 渠道失效后将失联",
                                fontSize = 12.sp,
                                color = HermesColors.Warning
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// 使用统一的AppColorUtils工具类（函数已删除，由AppColorUtils替代）

private fun getPurposeText(purpose: BindingPurpose): String {
    return when (purpose) {
        BindingPurpose.LOGIN -> "登录"
        BindingPurpose.VERIFICATION -> "验证"
        BindingPurpose.RECOVERY -> "找回"
        BindingPurpose.NOTIFICATION -> "通知"
        BindingPurpose.SECONDARY_AUTH -> "二次验证"
    }
}

/**
 * 受影响账号信息
 */
data class AffectedAccountInfo(
    val accountId: Long,
    val applicationName: String,
    val accountName: String,
    val category: String = "",
    val purposes: List<BindingPurpose>,
    val isPrimary: Boolean = false,
    val subAccounts: List<SubAccountInfo> = emptyList()
)

/**
 * 子账号信息
 */
data class SubAccountInfo(
    val accountId: Long,
    val accountName: String
)