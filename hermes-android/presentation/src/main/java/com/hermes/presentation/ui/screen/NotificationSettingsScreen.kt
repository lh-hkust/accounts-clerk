package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.NotificationSettings

/**
 * 通知设置页面（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    settings: NotificationSettings,
    onToggle: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var enableDeactivationReminder by remember { mutableStateOf(settings.enableDeactivationReminder) }
    var enableWeeklyReport by remember { mutableStateOf(settings.enableWeeklyReport) }

    // 同步外部状态变化
    LaunchedEffect(settings) {
        enableDeactivationReminder = settings.enableDeactivationReminder
        enableWeeklyReport = settings.enableWeeklyReport
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("通知设置", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 到期提醒卡片 - 原型样式
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = HermesColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "到期提醒",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        NotificationToggleRow(
                            title = "到期提醒",
                            enabled = enableDeactivationReminder,
                            onToggle = {
                                enableDeactivationReminder = it
                                onToggle("deactivation_reminder", it)
                            }
                        )
                        NotificationToggleRow(
                            title = "每周报告",
                            enabled = enableWeeklyReport,
                            onToggle = {
                                enableWeeklyReport = it
                                onToggle("weekly_report", it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit = {}
) {
    var isEnabled by remember { mutableStateOf(enabled) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = HermesColors.TextSecondary
        )

        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isEnabled) HermesColors.Success
                    else HermesColors.TextMuted.copy(alpha = 0.3f)
                )
                .clickable {
                    isEnabled = !isEnabled
                    onToggle(isEnabled)
                },
            contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(50))
                    .background(HermesColors.TextPrimary)
            )
        }
    }
}