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
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.theme.HermesColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 预警卡片组件
 */
@Composable
fun WarningCard(
    warning: WarningRecord,
    onClick: () -> Unit,
    onHandleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColor = getWarningLevelColor(warning.warningLevel)

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 级别指示器
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(levelColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 级别标签
                    Surface(
                        modifier = Modifier.height(20.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = levelColor
                    ) {
                        Text(
                            text = getWarningLevelText(warning.warningLevel),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = warning.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.TextPrimary,
                        maxLines = 2
                    )
                }

                // 未读指示
                if (!warning.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(HermesColors.Primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(warning.triggeredAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = HermesColors.TextMuted
                )

                // 快速处理按钮
                Button(
                    onClick = onHandleClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "处理",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private fun getWarningLevelColor(level: WarningLevel): Color {
    return when (level) {
        WarningLevel.HIGH -> HermesColors.WarningHigh
        WarningLevel.MEDIUM -> HermesColors.WarningMedium
        WarningLevel.LOW -> HermesColors.WarningLow
    }
}

private fun getWarningLevelText(level: WarningLevel): String {
    return when (level) {
        WarningLevel.HIGH -> "高"
        WarningLevel.MEDIUM -> "中"
        WarningLevel.LOW -> "低"
    }
}

private fun formatTime(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}