package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.account.AccountListItem

/**
 * 账户卡片组件
 */
@Composable
fun AccountCard(
    item: AccountListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = getAccountStatusColor(item.account.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
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
                    .background(HermesColors.Primary),
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
    }
}

private fun getAccountStatusColor(status: AccountStatus): Color {
    return when (status) {
        AccountStatus.ACTIVE -> HermesColors.Success
        AccountStatus.FROZEN -> HermesColors.Info
        AccountStatus.LOST -> HermesColors.Warning
        AccountStatus.ARCHIVED -> HermesColors.TextMuted
    }
}

private fun getAccountStatusText(status: AccountStatus): String {
    return when (status) {
        AccountStatus.ACTIVE -> "活跃"
        AccountStatus.FROZEN -> "冻结"
        AccountStatus.LOST -> "丢失"
        AccountStatus.ARCHIVED -> "归档"
    }
}