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
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem

/**
 * 标识卡片组件
 */
@Composable
fun IdentifierCard(
    item: IdentifierListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = getStatusColor(item.identifier.status)
    val typeColor = getTypeColor(item.identifier.type)

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
                        text = "绑定 ${item.boundAccountCount} 个账户",
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
                        text = "${remainingDays}天后停用",
                        style = MaterialTheme.typography.labelMedium,
                        color = HermesColors.Warning,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
        IdentifierStatus.ACTIVE -> "活跃"
        IdentifierStatus.PENDING_DEACTIVATION -> "待停用"
        IdentifierStatus.DEACTIVATED -> "已停用"
        IdentifierStatus.INVALIDATED -> "已失效"
    }
}

private fun getTypeColor(type: IdentifierType): Color {
    return when (type) {
        IdentifierType.PHONE -> HermesColors.PhoneColor
        IdentifierType.EMAIL -> HermesColors.EmailColor
    }
}