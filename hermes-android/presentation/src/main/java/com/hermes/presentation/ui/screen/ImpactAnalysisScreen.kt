package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 影响分析页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactAnalysisScreen(
    identifierId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("关联账号") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Surface
                )
            )
        },
        containerColor = HermesColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 风险等级
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val riskLevel = WarningLevel.MEDIUM
                    val riskColor = when (riskLevel) {
                        WarningLevel.HIGH -> HermesColors.WarningHigh
                        WarningLevel.MEDIUM -> HermesColors.WarningMedium
                        WarningLevel.LOW -> HermesColors.WarningLow
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(riskColor, shape = MaterialTheme.shapes.large),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (riskLevel) {
                                WarningLevel.HIGH -> "高"
                                WarningLevel.MEDIUM -> "中"
                                WarningLevel.LOW -> "低"
                            },
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "风险等级",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                }
            }

            // 受影响账号统计
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "受影响账号",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBox(title = "总账号数", count = 5, color = HermesColors.Primary)
                        StatBox(title = "金融账号", count = 2, color = HermesColors.WarningHigh)
                        StatBox(title = "社交账号", count = 3, color = HermesColors.Accent)
                    }
                }
            }

            // 账号列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "账号详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 示例账户列表
                    ImpactAccountItem(
                        name = "支付宝",
                        category = "金融",
                        purposes = listOf("登录", "验证")
                    )
                    ImpactAccountItem(
                        name = "微信",
                        category = "社交",
                        purposes = listOf("登录")
                    )
                    ImpactAccountItem(
                        name = "微博",
                        category = "社交",
                        purposes = listOf("登录", "找回")
                    )
                }
            }

            // 处理建议
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.SurfaceLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 处理建议",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.Accent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. 金融账号优先更换绑定渠道\n2. 社交账号可延后处理\n3. 建议在新渠道激活后再停用旧渠道",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = HermesColors.TextSecondary
        )
    }
}

@Composable
private fun ImpactAccountItem(
    name: String,
    category: String,
    purposes: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 应用图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(HermesColors.Primary, shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = HermesColors.TextPrimary
            )
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall,
                color = HermesColors.TextSecondary
            )
        }

        Text(
            text = purposes.joinToString(", "),
            style = MaterialTheme.typography.bodySmall,
            color = HermesColors.Accent
        )
    }
}