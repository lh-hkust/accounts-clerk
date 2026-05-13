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
import com.hermes.presentation.ui.component.AccountCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.account.AccountListItem
import com.hermes.presentation.viewmodel.AccountListState

/**
 * 账户列表页面
 */
@Composable
fun AccountListScreen(
    uiState: AccountListState,
    onAccountClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = HermesColors.Primary,
                contentColor = HermesColors.TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加账户")
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
                text = "账号资产库",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp),
                color = HermesColors.TextPrimary
            )

            // 内容列表
            when (uiState) {
                is AccountListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HermesColors.Primary)
                    }
                }
                is AccountListState.Success -> {
                    if (uiState.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无账号",
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
                                AccountCard(
                                    item = item,
                                    onClick = { onAccountClick(item.account.id!!) }
                                )
                            }
                        }
                    }
                }
                is AccountListState.Error -> {
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