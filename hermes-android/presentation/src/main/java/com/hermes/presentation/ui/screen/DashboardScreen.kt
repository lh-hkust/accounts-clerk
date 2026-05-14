package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.IdentifierStats
import com.hermes.presentation.viewmodel.AccountStats
import com.hermes.presentation.viewmodel.DashboardWarning

/**
 * 首页概览页面（与原型一致）
 */
@Composable
fun DashboardScreen(
    identifierStats: IdentifierStats,
    accountStats: AccountStats,
    warnings: List<DashboardWarning>,
    unhandledWarningCount: Int,
    onWarningClick: (Long) -> Unit,
    onHandleClick: (Long) -> Unit,
    onViewAllWarnings: () -> Unit,
    onQuickHandle: () -> Unit,
    onIdentifierListClick: () -> Unit,
    onAccountListClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        item {
            Text(
                text = "账号概览",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )
        }

        // 账号安全指数卡片 - 原型样式
        item {
            SafetyIndexCard(
                score = maxOf(0, 100 - unhandledWarningCount * 10),
                warningCount = unhandledWarningCount,
                accountStats = accountStats.totalCount,
                onQuickHandle = onQuickHandle
            )
        }

        // 验证渠道/账号库入口 - 原型样式：两列卡片
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardEntryCard(
                    icon = Icons.Filled.Security,
                    iconBgColor = HermesColors.Primary.copy(alpha = 0.2f),
                    iconColor = HermesColors.Primary,
                    title = "验证渠道",
                    subtitle = "手机号/邮箱",
                    onClick = onIdentifierListClick,
                    modifier = Modifier.weight(1f)
                )
                DashboardEntryCard(
                    icon = Icons.Filled.Inventory2,
                    iconBgColor = HermesColors.Secondary.copy(alpha = 0.2f),
                    iconColor = HermesColors.Secondary,
                    title = "账号库",
                    subtitle = "${accountStats.totalCount}个账号",
                    onClick = onAccountListClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 紧急处理标题
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = HermesColors.Warning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "紧急处理",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "查看全部",
                    fontSize = 12.sp,
                    color = HermesColors.Primary,
                    modifier = Modifier.clickable { onViewAllWarnings() }
                )
            }
        }

        // 提醒卡片列表（最多3条）
        items(warnings.take(3)) { warning ->
            DashboardWarningCard(
                warning = warning,
                onClick = { warning.id?.let { onWarningClick(it) } },
                onHandleClick = { warning.id?.let { onHandleClick(it) } }
            )
        }

        // 数据中心卡片 - 原型样式
        item {
            DataCenterCard(
                onImportClick = onImportClick,
                onExportClick = onExportClick
            )
        }
    }
}

@Composable
private fun SafetyIndexCard(
    score: Int,
    warningCount: Int,
    accountStats: Int,
    onQuickHandle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HermesColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "账号安全指数",
                fontSize = 12.sp,
                color = HermesColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = score.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = HermesColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HermesColors.SurfaceLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(score.toFloat() / 100f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(HermesColors.Primary, HermesColors.Secondary)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (warningCount > 0) {
                    Text(
                        text = "● ${warningCount}个验证渠道即将到期",
                        fontSize = 12.sp,
                        color = HermesColors.TextPrimary.copy(alpha = 0.8f)
                    )
                }
                Text(
                    text = "● ${accountStats}个账号",
                    fontSize = 12.sp,
                    color = HermesColors.TextPrimary.copy(alpha = 0.8f)
                )
            }

            if (warningCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onQuickHandle,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "快速处理",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardEntryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = HermesColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DataCenterCard(
    onImportClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HermesColors.CardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "数据中心",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onImportClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "批量导入",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
                Button(
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "加密导出",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardWarningCard(
    warning: DashboardWarning,
    onClick: () -> Unit,
    onHandleClick: () -> Unit
) {
    val levelColor = when (warning.level) {
        "HIGH" -> HermesColors.Danger
        "MEDIUM" -> HermesColors.Warning
        "LOW" -> HermesColors.Success
        else -> HermesColors.TextMuted
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(4.dp, levelColor, RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp)),
        shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = levelColor.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(levelColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = levelColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = warning.message.take(30),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onHandleClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Surface
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "处理",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = levelColor
                )
            }
        }
    }
}