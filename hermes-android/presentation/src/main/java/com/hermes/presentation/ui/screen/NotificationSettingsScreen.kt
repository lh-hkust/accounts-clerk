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
 * 通知设置页面（与原型一致，包含触发条件和通知内容说明）
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
    var enable30DayReminder by remember { mutableStateOf(true) }
    var enable7DayReminder by remember { mutableStateOf(true) }
    var enable3DayReminder by remember { mutableStateOf(true) }
    var enable1DayReminder by remember { mutableStateOf(false) }

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
            // 到期提醒卡片 - 包含触发条件和通知内容说明
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // 通知方式说明
                        Text(
                            text = "通知方式: 系统通知",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 提前30天提醒
                        NotificationTimingRow(
                            title = "提前30天提醒",
                            description = "渠道到期前30天发送首次提醒",
                            enabled = enable30DayReminder,
                            onToggle = { enable30DayReminder = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 提前7天提醒
                        NotificationTimingRow(
                            title = "提前7天提醒",
                            description = "渠道到期前7天发送加强提醒",
                            enabled = enable7DayReminder,
                            onToggle = { enable7DayReminder = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 提前3天提醒
                        NotificationTimingRow(
                            title = "提前3天提醒",
                            description = "渠道到期前3天发送紧急提醒",
                            enabled = enable3DayReminder,
                            onToggle = { enable3DayReminder = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 提前1天提醒
                        NotificationTimingRow(
                            title = "提前1天提醒",
                            description = "渠道到期前1天发送最终提醒",
                            enabled = enable1DayReminder,
                            onToggle = { enable1DayReminder = it }
                        )
                    }
                }
            }

            // 推送通知卡片
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
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = HermesColors.Warning
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "推送通知",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        NotificationToggleRow(
                            title = "启用系统推送",
                            description = "到期提醒将通过系统通知推送",
                            enabled = enableDeactivationReminder,
                            onToggle = {
                                enableDeactivationReminder = it
                                onToggle("deactivation_reminder", it)
                            }
                        )
                    }
                }
            }

            // 每周报告卡片
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
                                imageVector = Icons.Filled.Assessment,
                                contentDescription = null,
                                tint = HermesColors.Secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "每周报告",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        NotificationToggleRow(
                            title = "启用每周报告",
                            description = "每周生成账号资产变化报告",
                            enabled = enableWeeklyReport,
                            onToggle = {
                                enableWeeklyReport = it
                                onToggle("weekly_report", it)
                            }
                        )
                    }
                }
            }

            // 通知内容示例
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = HermesColors.TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "通知内容示例",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = HermesColors.TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "【Hermes提醒】您的手机号 138****8000 将于X天后到期，关联Y个账号，请及时处理。",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 带说明文字的通知时间设置行
 */
@Composable
private fun NotificationTimingRow(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit = {}
) {
    var isEnabled by remember { mutableStateOf(enabled) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = HermesColors.TextPrimary
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

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            color = HermesColors.TextMuted
        )
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    description: String = "",
    enabled: Boolean,
    onToggle: (Boolean) -> Unit = {}
) {
    var isEnabled by remember { mutableStateOf(enabled) }

    Column {
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

        if (description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = HermesColors.TextMuted
            )
        }
    }
}