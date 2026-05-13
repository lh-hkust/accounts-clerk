package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.model.WarningRecord
import com.hermes.presentation.ui.component.WarningCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.WarningListState

/**
 * 预警列表页面
 */
@Composable
fun WarningListScreen(
    uiState: WarningListState,
    handledWarnings: List<WarningRecord>,
    onWarningClick: (Long) -> Unit,
    onHandleClick: (Long) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHandled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background)
    ) {
        // 标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "提醒列表",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${uiState.let { if (it is WarningListState.Success) it.warnings.size else 0 }}条待处理",
                style = MaterialTheme.typography.bodyMedium,
                color = HermesColors.Warning
            )
        }

        // 内容
        when (uiState) {
            is WarningListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HermesColors.Primary)
                }
            }
            is WarningListState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 未处理预警
                    if (uiState.warnings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无待处理提醒",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = HermesColors.Success
                                )
                            }
                        }
                    } else {
                        items(uiState.warnings) { warning ->
                            WarningCard(
                                warning = warning,
                                onClick = { onWarningClick(warning.id!!) },
                                onHandleClick = { onHandleClick(warning.id!!) }
                            )
                        }
                    }

                    // 已处理预警（折叠）
                    if (handledWarnings.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = { showHandled = !showHandled }
                            ) {
                                Text(
                                    text = if (showHandled) "隐藏已处理 (${handledWarnings.size})" else "查看已处理 (${handledWarnings.size})",
                                    color = HermesColors.TextSecondary
                                )
                            }
                        }

                        if (showHandled) {
                            items(handledWarnings) { warning ->
                                WarningCard(
                                    warning = warning,
                                    onClick = { onWarningClick(warning.id!!) },
                                    onHandleClick = { onHandleClick(warning.id!!) }
                                )
                            }
                        }
                    }
                }
            }
            is WarningListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
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