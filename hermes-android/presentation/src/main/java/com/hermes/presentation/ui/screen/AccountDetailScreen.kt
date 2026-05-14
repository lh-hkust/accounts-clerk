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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.AccountDetailState
import com.hermes.presentation.usecase.account.AccountDetail
import com.hermes.presentation.usecase.account.IdentifierBindingInfo
import com.hermes.presentation.usecase.account.RelatedAccountInfo

/**
 * 账号详情页面（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    uiState: AccountDetailState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onChangeChannelClick: () -> Unit,
    onChangeStatusClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRelatedAccountClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("账号详情", fontWeight = FontWeight.Bold) },
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
            is AccountDetailState.Loading -> {
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
            is AccountDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 账号信息卡片 - 原型样式
                    item {
                        AccountInfoCard(
                            detail = uiState.detail,
                            onEditClick = onEditClick
                        )
                    }

                    // 验证渠道列表
                    if (uiState.detail.boundIdentifiers.isNotEmpty()) {
                        item {
                            Text(
                                text = "验证渠道",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        items(uiState.detail.boundIdentifiers) { identifier ->
                            BoundIdentifierCard(
                                identifier = identifier,
                                onChangeClick = onChangeChannelClick
                            )
                        }
                    }

                    // 关联账户列表
                    if (uiState.detail.relatedAccounts.isNotEmpty()) {
                        item {
                            Text(
                                text = "关联账户",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        items(uiState.detail.relatedAccounts) { related ->
                            RelatedAccountCard(
                                related = related,
                                onClick = { onRelatedAccountClick(related.accountId) }
                            )
                        }
                    }

                    // 操作按钮区域
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // 编辑账号按钮
                            OutlinedButton(
                                onClick = onEditClick,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = HermesColors.TextPrimary
                                )
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("编辑账号", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // 更换验证渠道按钮
                            OutlinedButton(
                                onClick = onChangeChannelClick,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = HermesColors.TextPrimary
                                )
                            ) {
                                Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("更换验证渠道", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // 变更账号状态按钮 - 原型黄色警告样式
                            OutlinedButton(
                                onClick = onChangeStatusClick,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = HermesColors.Warning
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(HermesColors.Warning.copy(alpha = 0.3f), HermesColors.Warning.copy(alpha = 0.3f))
                                    )
                                )
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = HermesColors.Warning)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("变更账号状态", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = HermesColors.Warning)
                            }

                            // 删除账号按钮 - 原型红色危险样式
                            OutlinedButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = HermesColors.Danger
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(HermesColors.Danger.copy(alpha = 0.3f), HermesColors.Danger.copy(alpha = 0.3f))
                                    )
                                )
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = HermesColors.Danger)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("删除账号", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = HermesColors.Danger)
                            }
                        }
                    }
                }
            }
            is AccountDetailState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "账号不存在",
                        fontSize = 14.sp,
                        color = HermesColors.TextMuted
                    )
                }
            }
            is AccountDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        fontSize = 14.sp,
                        color = HermesColors.Danger
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountInfoCard(
    detail: AccountDetail,
    onEditClick: () -> Unit
) {
    val statusText = getStatusText(detail.account.status)
    val statusColor = getStatusColor(detail.account.status)
    val appColor = getAppIconColor(detail.applicationName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 应用图标和账号名 - 原型样式
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = detail.applicationName.take(1),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${detail.applicationName} - ${detail.account.accountName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(statusColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 账号详情信息 - 原型样式
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "账号ID",
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary
                    )
                    Text(
                        text = detail.account.accountIdentifier ?: "未设置",
                        fontSize = 14.sp,
                        color = HermesColors.TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "应用分类",
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary
                    )
                    Text(
                        text = detail.applicationCategory ?: "其他",
                        fontSize = 14.sp,
                        color = HermesColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun BoundIdentifierCard(
    identifier: IdentifierBindingInfo,
    onChangeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onChangeClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (identifier.identifierValue.contains("@"))
                    Icons.Filled.Email
                else
                    Icons.Filled.Phone,
                contentDescription = null,
                tint = HermesColors.Primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = identifier.identifierValue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextPrimary
                )

                // 用途标签 - 原型样式
                if (identifier.purposes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        identifier.purposes.forEach { purpose ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(HermesColors.Primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = getPurposeText(purpose),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.Primary
                                )
                            }
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = HermesColors.TextMuted
            )
        }
    }
}

@Composable
private fun RelatedAccountCard(
    related: RelatedAccountInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getAppIconColor(related.applicationName).copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = related.applicationName.take(1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${related.applicationName} - ${related.accountName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "关联类型: ${related.relationType}",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = HermesColors.TextMuted
            )
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
        appName.contains("银行") -> Color(0xFF1677ff)
        else -> HermesColors.Primary
    }
}

private fun getPurposeText(purpose: BindingPurpose): String {
    return when (purpose) {
        BindingPurpose.LOGIN -> "登录"
        BindingPurpose.VERIFICATION -> "验证"
        BindingPurpose.RECOVERY -> "找回"
        BindingPurpose.NOTIFICATION -> "通知"
        BindingPurpose.SECONDARY_AUTH -> "二次验证"
    }
}