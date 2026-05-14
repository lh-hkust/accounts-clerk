package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.component.WarningCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.WarningListState

/**
 * 全部提醒页面（支持卡片手势交互）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningListScreen(
    uiState: WarningListState,
    handledWarnings: Set<Long>,
    onWarningClick: (Long) -> Unit,
    onHandleClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onSetReminder: (Long) -> Unit = {},
    onViewAffectedAccounts: (Long) -> Unit = {}
) {
    var showHandled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("全部提醒", fontWeight = FontWeight.Bold) },
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
        when (uiState) {
            is WarningListState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
            }
            is WarningListState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 未处理提醒分组 - 原型样式
                    if (uiState.items.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, HermesColors.Warning.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "未处理 (${uiState.items.size})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = HermesColors.TextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    for (warning in uiState.items) {
                                        val warningId = warning.id
                                        if (warningId != null) {
                                            WarningCard(
                                                warning = warning,
                                                onClick = { onWarningClick(warningId) },
                                                onHandleClick = { onHandleClick(warningId) },
                                                onSetReminder = { onSetReminder(warningId) },
                                                onViewAffectedAccounts = { onViewAffectedAccounts(warningId) }
                                            )
                                        }
                                        if (warning != uiState.items.last()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 查看已处理按钮 - 原型样式
                    if (handledWarnings.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showHandled = !showHandled },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                                )
                            ) {
                                Icon(
                                    imageVector = if (showHandled) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "查看已处理的消息",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.TextPrimary
                                )
                            }
                        }

                        // 已处理提醒 - 原型样式：灰色显示
                        if (showHandled) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "已处理 (${handledWarnings.size})",
                                            fontSize = 14.sp,
                                            color = HermesColors.TextSecondary.copy(alpha = 0.6f)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        handledWarnings.forEachIndexed { index, id ->
                                            HandledWarningIdItem(id = id)
                                            if (index < handledWarnings.size - 1) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 无未处理提醒
                    if (uiState.items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无待处理提醒",
                                    fontSize = 14.sp,
                                    color = HermesColors.Success
                                )
                            }
                        }
                    }
                }
            }
            is WarningListState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.message,
                            fontSize = 14.sp,
                            color = HermesColors.Danger
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("重新加载")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HandledWarningIdItem(id: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = HermesColors.Success.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "标识 #$id 已处理",
            fontSize = 14.sp,
            color = HermesColors.TextMuted
        )
    }
}

@Composable
private fun HandledWarningItem(warning: WarningRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getMessageText(warning),
                fontSize = 14.sp,
                color = HermesColors.TextSecondary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "已处理于 ${getHandledDate(warning)}",
                fontSize = 12.sp,
                color = HermesColors.TextMuted.copy(alpha = 0.5f)
            )
        }

        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "已处理",
            tint = HermesColors.Success
        )
    }
}

private fun getLevelText(level: WarningLevel): String {
    return when (level) {
        WarningLevel.HIGH -> "紧急"
        WarningLevel.MEDIUM -> "建议"
        WarningLevel.LOW -> "低"
    }
}

private fun getLevelColor(level: WarningLevel): androidx.compose.ui.graphics.Color {
    return when (level) {
        WarningLevel.HIGH -> HermesColors.Danger
        WarningLevel.MEDIUM -> HermesColors.Warning
        WarningLevel.LOW -> HermesColors.TextMuted
    }
}

private fun getMessageText(warning: WarningRecord): String {
    return when {
        warning.message.contains("到期") -> "手机号即将到期"
        warning.message.contains("冻结") -> "账号已冻结"
        warning.message.contains("失效") -> "验证渠道已失效"
        else -> warning.message
    }
}

private fun getSubtitle(warning: WarningRecord): String {
    return warning.message
}

private fun getHandledDate(warning: WarningRecord): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .withZone(java.time.ZoneId.systemDefault())
    return warning.handledAt?.let { formatter.format(it) } ?: "未知"
}