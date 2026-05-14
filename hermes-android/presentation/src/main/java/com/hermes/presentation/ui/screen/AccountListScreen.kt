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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.presentation.ui.component.AccountCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.account.AccountListItem
import com.hermes.presentation.viewmodel.AccountListState

/**
 * 账号页面（支持卡片手势交互）
 */
@Composable
fun AccountListScreen(
    uiState: AccountListState,
    onAccountClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onEditAccount: (Long) -> Unit = {},
    onChangeIdentifier: (Long) -> Unit = {},
    onChangeStatus: (Long) -> Unit = {},
    onDeleteAccount: (Long) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background)
    ) {
        // 标题+添加按钮 - 原型样式
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "账号",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "+ 添加",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HermesColors.Primary,
                modifier = Modifier.clickable { onAddClick() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 搜索框 - 原型样式
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "搜索账号、应用或备注...",
                    fontSize = 14.sp,
                    color = HermesColors.TextMuted
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HermesColors.Primary,
                unfocusedBorderColor = HermesColors.CardBorder,
                focusedContainerColor = HermesColors.Surface,
                unfocusedContainerColor = HermesColors.Surface
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "搜索",
                    tint = HermesColors.TextMuted
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                // 搜索过滤
                val filteredItems = if (searchQuery.isEmpty()) {
                    uiState.items
                } else {
                    uiState.items.filter { item ->
                        item.account.accountName.contains(searchQuery, ignoreCase = true) ||
                        item.applicationName.contains(searchQuery, ignoreCase = true) ||
                        item.account.accountIdentifier?.contains(searchQuery, ignoreCase = true) == true
                    }
                }

                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "暂无账号" else "未找到匹配的账号",
                            fontSize = 14.sp,
                            color = HermesColors.TextMuted
                        )
                    }
                } else {
                    // 按应用分组 - 原型样式
                    val groupedItems = groupByCategory(filteredItems)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedItems.forEach { (category, accounts) ->
                            // 分组标题 - 原型样式
                            item {
                                Text(
                                    text = "${category.name} (${accounts.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (category.isDanger) HermesColors.Danger
                                    else HermesColors.TextSecondary,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                                )
                            }

                            // 分组内的账号卡片（使用带手势的AccountCard）
                            items(accounts) { item ->
                                val accountId = item.account.id
                                if (accountId != null) {
                                    AccountCard(
                                        item = item,
                                        onClick = { onAccountClick(accountId) },
                                        onEdit = { onEditAccount(accountId) },
                                        onChangeIdentifier = { onChangeIdentifier(accountId) },
                                        onChangeStatus = { onChangeStatus(accountId) },
                                        onDelete = { onDeleteAccount(accountId) }
                                    )
                                }
                            }
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
private fun AccountListCard(
    item: AccountListItem,
    onClick: () -> Unit,
    isDangerGroup: Boolean = false
) {
    val statusText = getStatusText(item.account.status)
    val statusColor = getStatusColor(item.account.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isDangerGroup)
                    Modifier.border(1.dp, HermesColors.TextMuted.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                else
                    Modifier.border(1.dp, HermesColors.CardBorder.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 应用图标 - 原型样式：圆角图标背景
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getAppIconColor(item.applicationName)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.applicationName.take(2),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 账号信息 - 原型样式
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${item.applicationName} - ${item.account.accountName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDangerGroup) HermesColors.TextSecondary else HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${item.account.id}",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
            }

            // 状态徽章 - 原型样式
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}

private fun getStatusText(status: AccountStatus): String {
    return when (status) {
        AccountStatus.ACTIVE -> "正常使用"
        AccountStatus.FROZEN -> "已冻结"
        AccountStatus.LOST -> "已丢失"
        AccountStatus.ARCHIVED -> "已归档"
    }
}

private fun getStatusColor(status: AccountStatus): Color {
    return when (status) {
        AccountStatus.ACTIVE -> HermesColors.Success
        AccountStatus.FROZEN -> HermesColors.Danger
        AccountStatus.LOST -> HermesColors.TextMuted
        AccountStatus.ARCHIVED -> HermesColors.TextMuted
    }
}

private fun getAppIconColor(appName: String): Color {
    return when {
        appName.contains("微信") -> Color(0xFF07c160)
        appName.contains("支付宝") -> Color(0xFF1677ff)
        appName.contains("微博") -> Color(0xFFe6162d)
        appName.contains("抖音") -> Color(0xFF000000)
        appName.contains("淘宝") -> Color(0xFFff4400)
        appName.contains("京东") -> Color(0xFFe53935)
        appName.contains("QQ") -> Color(0xFF12b7f5)
        appName.contains("招商银行") || appName.contains("银行") -> Color(0xFF1677ff)
        else -> HermesColors.Primary
    }
}

private data class AccountCategory(
    val name: String,
    val isDanger: Boolean = false
)

private fun groupByCategory(items: List<AccountListItem>): Map<AccountCategory, List<AccountListItem>> {
    // 原型分组：社交、金融支付、已失联/已回收
    val categories = mutableMapOf<AccountCategory, MutableList<AccountListItem>>()

    items.forEach { item ->
        val category = when {
            item.account.status == AccountStatus.LOST -> AccountCategory("已失联/已回收", true)
            item.account.status == AccountStatus.FROZEN -> AccountCategory("已冻结", false)
            item.applicationName.contains("支付宝") ||
            item.applicationName.contains("银行") ||
            item.applicationName.contains("支付") -> AccountCategory("金融支付", false)
            item.applicationName.contains("微信") ||
            item.applicationName.contains("微博") ||
            item.applicationName.contains("QQ") ||
            item.applicationName.contains("抖音") ||
            item.applicationName.contains("社交") -> AccountCategory("社交", false)
            else -> AccountCategory("其他", false)
        }

        categories.getOrPut(category) { mutableListOf() }.add(item)
    }

    // 排序：社交、金融支付、其他、已冻结、已失联
    val orderedCategories = listOf(
        AccountCategory("社交"),
        AccountCategory("金融支付"),
        AccountCategory("其他"),
        AccountCategory("已冻结"),
        AccountCategory("已失联/已回收", true)
    )

    return orderedCategories
        .filter { categories.containsKey(it) }
        .associateWith { categories[it] ?: emptyList() }
}