package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 隐私与安全
            SettingsSection(title = "隐私与安全") {
                SettingsItem(
                    title = "数据库加密",
                    subtitle = "已启用 SQLCipher 加密",
                    icon = "🔐"
                )
                SettingsItem(
                    title = "生物识别解锁",
                    subtitle = "使用指纹或面容解锁",
                    icon = "👆"
                )
                SettingsItem(
                    title = "自动锁定",
                    subtitle = "5分钟后自动锁定",
                    icon = "🔒"
                )
            }

            // 通知设置
            SettingsSection(title = "通知设置") {
                SettingsItem(
                    title = "停用计划提醒",
                    subtitle = "提前7天通知",
                    icon = "⚠️"
                )
                SettingsItem(
                    title = "预警通知",
                    subtitle = "实时推送预警",
                    icon = "🔔"
                )
            }

            // 数据管理
            SettingsSection(title = "数据管理") {
                SettingsItem(
                    title = "数据导出",
                    subtitle = "导出为 JSON/CSV 格式",
                    icon = "📤"
                )
                SettingsItem(
                    title = "数据导入",
                    subtitle = "从文件导入数据",
                    icon = "📥"
                )
                SettingsItem(
                    title = "清除数据",
                    subtitle = "删除所有本地数据",
                    icon = "🗑️"
                )
            }

            // 应用信息
            SettingsSection(title = "应用信息") {
                SettingsItem(
                    title = "版本",
                    subtitle = "1.0.0",
                    icon = "📦"
                )
                SettingsItem(
                    title = "关于 Hermes",
                    subtitle = "账号管理工具",
                    icon = "ℹ️"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 版权信息
            Text(
                text = "© 2026 Hermes Team",
                style = MaterialTheme.typography.bodySmall,
                color = HermesColors.TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = HermesColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = HermesColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = HermesColors.TextSecondary
            )
        }
    }
}