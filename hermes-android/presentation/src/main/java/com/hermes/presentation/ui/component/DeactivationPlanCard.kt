package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 停用计划卡片组件
 */
@Composable
fun DeactivationPlanCard(
    detail: DeactivationDetail,
    onCancelClick: () -> Unit,
    onModifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val impactColor = when {
        detail.totalAffectedCount > 5 -> HermesColors.WarningHigh
        detail.totalAffectedCount >= 2 -> HermesColors.WarningMedium
        else -> HermesColors.WarningLow
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HermesColors.Warning, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "到期提醒",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.Warning
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${detail.remainingDays}天后到期",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = impactColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 提醒信息
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "到期日期",
                        style = MaterialTheme.typography.bodySmall,
                        color = HermesColors.TextMuted
                    )
                    Text(
                        text = formatTime(detail.deactivation.scheduledTime!!),
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "受影响账号",
                        style = MaterialTheme.typography.bodySmall,
                        color = HermesColors.TextMuted
                    )
                    Text(
                        text = "${detail.totalAffectedCount}个",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = impactColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = HermesColors.TextSecondary
                    )
                ) {
                    Text(text = "取消提醒")
                }
                Button(
                    onClick = onModifyClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Primary
                    )
                ) {
                    Text(text = "修改日期")
                }
            }
        }
    }
}

private fun formatTime(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}