package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.presentation.ui.theme.HermesColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

/**
 * 设置停用计划页面（集成DatePickerDialog）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDeactivationScreen(
    identifierId: Long,
    onBackClick: () -> Unit,
    onScheduled: () -> Unit,
    modifier: Modifier = Modifier,
    initialDate: LocalDate? = null,
    onDateSelected: (LocalDate, com.hermes.domain.valueobject.DeactivationType, String?) -> Unit = { _, _, _ -> }
) {
    var selectedDate by remember { mutableStateOf(initialDate ?: LocalDate.now().plusDays(30)) }
    var reason by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(com.hermes.domain.valueobject.DeactivationType.OTHER) }

    // DatePickerDialog状态
    var showDatePicker by remember { mutableStateOf(false) }

    // DatePicker状态
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // 只允许选择今天之后的日期
                val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                return utcTimeMillis >= today
            }
        }
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("设置到期提醒") },
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
            // 选择日期
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "到期日期",
                        style = MaterialTheme.typography.titleMedium,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 日期选择器按钮 - 点击打开DatePickerDialog
                    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EditCalendar,
                            contentDescription = "选择日期",
                            tint = HermesColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedDate.format(formatter))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 快速选择
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickDateButton("7天", { selectedDate = LocalDate.now().plusDays(7) })
                        QuickDateButton("30天", { selectedDate = LocalDate.now().plusDays(30) })
                        QuickDateButton("90天", { selectedDate = LocalDate.now().plusDays(90) })
                        QuickDateButton("180天", { selectedDate = LocalDate.now().plusDays(180) })
                    }
                }
            }

            // 原因类型
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "原因",
                        style = MaterialTheme.typography.titleMedium,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == com.hermes.domain.valueobject.DeactivationType.PHONE_NUMBER_CHANGE,
                            onClick = { selectedType = com.hermes.domain.valueobject.DeactivationType.PHONE_NUMBER_CHANGE },
                            label = { Text("更换手机号") }
                        )
                        FilterChip(
                            selected = selectedType == com.hermes.domain.valueobject.DeactivationType.EMAIL_CHANGE,
                            onClick = { selectedType = com.hermes.domain.valueobject.DeactivationType.EMAIL_CHANGE },
                            label = { Text("更换邮箱") }
                        )
                        FilterChip(
                            selected = selectedType == com.hermes.domain.valueobject.DeactivationType.OTHER,
                            onClick = { selectedType = com.hermes.domain.valueobject.DeactivationType.OTHER },
                            label = { Text("其他原因") }
                        )
                    }
                }
            }

            // 原因说明
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("原因说明（可选）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 影响提示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Warning.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ 影响提示",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = HermesColors.Warning
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "此渠道到期后，绑定的账号将无法使用此渠道登录或验证。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 确认按钮
            Button(
                onClick = {
                    onDateSelected(selectedDate, selectedType, if (reason.isNotEmpty()) reason else null)
                    onScheduled()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Warning
                )
            ) {
                Text("确认设置")
            }
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    }
                ) {
                    Text("确定", color = HermesColors.Primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("取消", color = HermesColors.TextSecondary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = { Text("选择到期日期", color = HermesColors.TextPrimary) }
            )
        }
    }
}

@Composable
private fun QuickDateButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}