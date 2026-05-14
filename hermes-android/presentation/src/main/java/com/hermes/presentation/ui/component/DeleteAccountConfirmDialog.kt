package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 删除账号确认弹窗（防呆设计）
 *
 * 设计规范：
 * - 弹窗标题："确认删除账号？"
 * - 弹窗内容：显示账号名称和昵称
 * - 输入提示："请输入「{accountName}」确认删除"
 * - 删除按钮默认禁用，输入正确名称后启用
 *
 * @param accountName 账号名称
 * @param applicationName 应用名称
 * @param nickname 账号昵称（可选）
 * @param status 账号状态
 * @param onConfirm 确认删除回调
 * @param onDismiss 取消回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountConfirmDialog(
    accountName: String,
    applicationName: String,
    nickname: String? = null,
    status: AccountStatus = AccountStatus.ACTIVE,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 用户输入的确认名称
    var inputText by remember { mutableStateOf("") }

    // 验证输入是否正确（需要输入accountName才能确认删除）
    val isInputValid = inputText.trim() == accountName

    // 显示名称（优先昵称）
    val displayName = nickname ?: accountName

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // 警告图标
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HermesColors.Danger.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "警告",
                        tint = HermesColors.Danger,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 标题
                Text(
                    text = "确认删除账号？",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 账号信息显示
                Text(
                    text = "账号：$applicationName - $displayName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextSecondary
                )

                // 显示状态信息（如果有绑定渠道则提示）
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "删除后将自动解绑所有验证渠道",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 输入提示
                Text(
                    text = "请输入「$accountName」确认删除",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 输入框
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = accountName,
                            fontSize = 14.sp,
                            color = HermesColors.TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isInputValid) HermesColors.Danger else HermesColors.Primary,
                        unfocusedBorderColor = HermesColors.CardBorder,
                        focusedContainerColor = HermesColors.Background,
                        unfocusedContainerColor = HermesColors.Background,
                        cursorColor = HermesColors.Primary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.SurfaceLight
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    // 确认删除按钮（仅当输入正确时启用）
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInputValid) HermesColors.Danger else HermesColors.TextMuted.copy(alpha = 0.3f)
                        ),
                        enabled = isInputValid
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "确认删除",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}