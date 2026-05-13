package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.component.WarningCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem

/**
 * 首页看板页面
 */
@Composable
fun DashboardScreen(
    identifierStats: Map<IdentifierStatus, Int>,
    accountStats: Int,
    warnings: List<WarningRecord>,
    unhandledWarningCount: Int,
    onWarningClick: (Long) -> Unit,
    onHandleClick: (Long) -> Unit,
    onViewAllWarnings: () -> Unit,
    onQuickHandle: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        item {
            Text(
                text = "账号概览",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = HermesColors.Primary
            )
            Text(
                text = "账号资产守护专家",
                style = MaterialTheme.typography.bodyMedium,
                color = HermesColors.TextSecondary
            )
        }

        // 风险雷达
        item {
            RiskRadarCard(
                unhandledWarningCount = unhandledWarningCount,
                onQuickHandle = onQuickHandle
            )
        }

        // 状态概览
        item {
            StatusOverviewCard(
                identifierStats = identifierStats,
                accountStats = accountStats
            )
        }

        // 预警卡片列表
        if (warnings.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "提醒列表",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onViewAllWarnings) {
                        Text("查看全部", color = HermesColors.TextSecondary)
                    }
                }
            }

            items(warnings.take(3)) { warning ->
                WarningCard(
                    warning = warning,
                    onClick = { onWarningClick(warning.id!!) },
                    onHandleClick = { onHandleClick(warning.id!!) }
                )
            }
        }

        // 快捷入口
        item {
            QuickActionsCard()
        }
    }
}

@Composable
private fun RiskRadarCard(
    unhandledWarningCount: Int,
    onQuickHandle: () -> Unit
) {
    val riskColor = when {
        unhandledWarningCount > 5 -> HermesColors.WarningHigh
        unhandledWarningCount > 0 -> HermesColors.WarningMedium
        else -> HermesColors.Success
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, riskColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 安全指数圆环
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(riskColor),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = unhandledWarningCount.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                    Text(
                        text = "条提醒",
                        style = MaterialTheme.typography.bodySmall,
                        color = HermesColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (unhandledWarningCount > 0) "需要关注" else "安全指数良好",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )

            if (unhandledWarningCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onQuickHandle,
                    colors = ButtonDefaults.buttonColors(containerColor = HermesColors.Primary)
                ) {
                    Text("快速处理")
                }
            }
        }
    }
}

@Composable
private fun StatusOverviewCard(
    identifierStats: Map<IdentifierStatus, Int>,
    accountStats: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 活跃标识
            StatItem(
                title = "活跃渠道",
                count = identifierStats[IdentifierStatus.ACTIVE] ?: 0,
                color = HermesColors.Success,
                modifier = Modifier.weight(1f)
            )
            // 即将到期
            StatItem(
                title = "即将到期",
                count = identifierStats[IdentifierStatus.PENDING_DEACTIVATION] ?: 0,
                color = HermesColors.Warning,
                modifier = Modifier.weight(1f)
            )
            // 账号
            StatItem(
                title = "账号总数",
                count = accountStats,
                color = HermesColors.Primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = HermesColors.TextSecondary
        )
    }
}

@Composable
private fun QuickActionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快捷入口",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "添加渠道",
                    color = HermesColors.Primary,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    text = "添加账号",
                    color = HermesColors.Secondary,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    text = "数据导出",
                    color = HermesColors.Accent,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}