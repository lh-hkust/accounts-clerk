package com.hermes.presentation.ui.component

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.usecase.identifier.BoundAccountInfo

/**
 * 删除渠道阻止弹窗
 *
 * 当渠道绑定了账号时，阻止删除并显示绑定账号列表
 *
 * @see spec.md Requirement: User cannot delete identifier with bindings
 */
@Composable
fun DeleteIdentifierBlockDialog(
    boundAccountCount: Int,
    boundAccounts: List<BoundAccountInfo>,
    onDismiss: () -> Unit,
    onViewBoundAccounts: () -> Unit,
    onAccountClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Block,
                contentDescription = null,
                tint = HermesColors.Danger,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "无法删除渠道",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HermesColors.TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "此渠道绑定了 $boundAccountCount 个账号，无法删除",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "请先解绑以下账号后再删除",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 绑定账号列表
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HermesColors.SurfaceLight
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(boundAccounts) { account ->
                            BoundAccountItem(
                                account = account,
                                onClick = { onAccountClick(account.accountId) }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onViewBoundAccounts,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "查看绑定账号",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HermesColors.TextSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface,
        modifier = modifier
    )
}

/**
 * 绑定账号列表项（可点击跳转详情）
 */
@Composable
private fun BoundAccountItem(
    account: BoundAccountInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 应用图标占位
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(HermesColors.Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Apps,
                    contentDescription = null,
                    tint = HermesColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.applicationName,
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.accountName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextPrimary
                )
            }

            // 跳转箭头
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "查看详情",
                tint = HermesColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 删除渠道确认弹窗（无绑定账号时使用）
 */
@Composable
fun DeleteIdentifierConfirmDialog(
    identifierValue: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Delete,
                contentDescription = null,
                tint = HermesColors.Danger,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "确认删除渠道？",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = HermesColors.TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "删除后将无法恢复，请确认操作。",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HermesColors.SurfaceLight
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            tint = HermesColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = identifierValue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HermesColors.TextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Danger
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "确认删除",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HermesColors.TextSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface,
        modifier = modifier
    )
}