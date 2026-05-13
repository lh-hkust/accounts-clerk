package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.DeactivationPlanCard
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.deactivation.DeactivationDetail
import com.hermes.presentation.usecase.identifier.IdentifierDetail
import com.hermes.presentation.viewmodel.IdentifierDetailState

/**
 * 标识详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifierDetailScreen(
    uiState: IdentifierDetailState,
    deactivationDetail: DeactivationDetail?,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAccountClick: (Long) -> Unit,
    onCancelDeactivation: () -> Unit,
    onModifyDeactivation: () -> Unit,
    onScheduleDeactivation: () -> Unit,
    canDelete: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("影响范围") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (canDelete) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HermesColors.Surface
                )
            )
        }
    ) { padding ->
        when (uiState) {
            is IdentifierDetailState.Loading -> {
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
            is IdentifierDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 渠道基本信息
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val typeIcon = if (uiState.detail.identifier.type == IdentifierType.PHONE) "📱" else "📧"
                                    Text(
                                        text = "$typeIcon ${uiState.detail.identifier.value}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = HermesColors.TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "绑定账号: ${uiState.detail.boundAccountCount}个",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = HermesColors.TextSecondary
                                )
                            }
                        }
                    }

                    // 到期提醒卡片（如果有）
                    if (deactivationDetail != null) {
                        item {
                            DeactivationPlanCard(
                                detail = deactivationDetail,
                                onCancelClick = onCancelDeactivation,
                                onModifyClick = onModifyDeactivation
                            )
                        }
                    } else if (uiState.detail.identifier.status == com.hermes.domain.valueobject.IdentifierStatus.ACTIVE) {
                        // 设置到期提醒按钮
                        item {
                            Button(
                                onClick = onScheduleDeactivation,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HermesColors.Warning
                                )
                            ) {
                                Text("设置到期提醒")
                            }
                        }
                    }

                    // 关联账号列表
                    if (uiState.detail.boundAccounts.isNotEmpty()) {
                        item {
                            Text(
                                text = "关联账号",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = HermesColors.TextPrimary
                            )
                        }
                        items(uiState.detail.boundAccounts) { account ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onAccountClick(account.accountId) },
                                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = account.applicationName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = HermesColors.TextPrimary
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (account.isPrimary) {
                                            Surface(
                                                color = HermesColors.Primary,
                                                shape = MaterialTheme.shapes.small
                                            ) {
                                                Text(
                                                    text = "主要",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(4.dp),
                                                    color = HermesColors.TextPrimary
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = account.accountName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = HermesColors.TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "用途: ${account.purposes.joinToString(", ") { getPurposeText(it) }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HermesColors.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is IdentifierDetailState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("渠道不存在", color = HermesColors.TextMuted)
                }
            }
            is IdentifierDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message, color = HermesColors.Danger)
                }
            }
        }
    }
}

private fun getPurposeText(purpose: com.hermes.domain.valueobject.BindingPurpose): String {
    return when (purpose) {
        com.hermes.domain.valueobject.BindingPurpose.LOGIN -> "登录"
        com.hermes.domain.valueobject.BindingPurpose.VERIFICATION -> "验证"
        com.hermes.domain.valueobject.BindingPurpose.RECOVERY -> "找回"
        com.hermes.domain.valueobject.BindingPurpose.NOTIFICATION -> "通知"
        com.hermes.domain.valueobject.BindingPurpose.SECONDARY_AUTH -> "二次认证"
    }
}