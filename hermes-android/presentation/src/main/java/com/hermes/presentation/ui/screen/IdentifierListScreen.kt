package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.presentation.ui.component.IdentifierCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.IdentifierListItem
import com.hermes.presentation.viewmodel.IdentifierListState

/**
 * 渠道列表页面
 */
@Composable
fun IdentifierListScreen(
    uiState: IdentifierListState,
    onIdentifierClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatus by remember { mutableStateOf<IdentifierStatus?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = HermesColors.Primary,
                contentColor = HermesColors.TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加验证渠道")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background)
        ) {
            // 标题
            Text(
                text = "验证渠道",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp),
                color = HermesColors.TextPrimary
            )

            // 状态筛选
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = {
                        selectedStatus = null
                        onRefresh()
                    },
                    label = { Text("全部") }
                )
                FilterChip(
                    selected = selectedStatus == IdentifierStatus.ACTIVE,
                    onClick = {
                        selectedStatus = IdentifierStatus.ACTIVE
                        // TODO: 调用筛选方法
                    },
                    label = { Text("活跃") }
                )
                FilterChip(
                    selected = selectedStatus == IdentifierStatus.PENDING_DEACTIVATION,
                    onClick = {
                        selectedStatus = IdentifierStatus.PENDING_DEACTIVATION
                        // TODO: 调用筛选方法
                    },
                    label = { Text("待停用") }
                )
                FilterChip(
                    selected = selectedStatus == IdentifierStatus.DEACTIVATED,
                    onClick = {
                        selectedStatus = IdentifierStatus.DEACTIVATED
                        // TODO: 调用筛选方法
                    },
                    label = { Text("已停用") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 内容列表
            when (uiState) {
                is IdentifierListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HermesColors.Primary)
                    }
                }
                is IdentifierListState.Success -> {
                    if (uiState.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无渠道",
                                style = MaterialTheme.typography.bodyLarge,
                                color = HermesColors.TextMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.items) { item ->
                                IdentifierCard(
                                    item = item,
                                    onClick = { onIdentifierClick(item.identifier.id!!) }
                                )
                            }
                        }
                    }
                }
                is IdentifierListState.Error -> {
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
}