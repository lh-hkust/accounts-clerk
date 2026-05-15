package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.SecuritySettings

/**
 * 隐私安全页面（与原型一致，包含完整权限用途和数据安全声明）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    settings: SecuritySettings,
    onSetPasswordClick: () -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var biometricEnabled by remember { mutableStateOf(settings.biometricEnabled) }

    // 同步外部状态变化
    LaunchedEffect(settings.biometricEnabled) {
        biometricEnabled = settings.biometricEnabled
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("隐私安全", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 访问密码卡片 - 原型样式
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Key,
                                contentDescription = null,
                                tint = HermesColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "访问密码",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "应用启动时需要密码解锁",
                                fontSize = 14.sp,
                                color = HermesColors.TextSecondary
                            )
                            Button(
                                onClick = onSetPasswordClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HermesColors.Primary.copy(alpha = 0.2f)
                                )
                            ) {
                                Text(
                                    text = "设置密码",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.Primary
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = HermesColors.CardBorder
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "指纹/面容解锁",
                                fontSize = 14.sp,
                                color = HermesColors.TextSecondary
                            )
                            ToggleSwitch(
                                enabled = biometricEnabled,
                                onToggle = {
                                    biometricEnabled = it
                                    onToggleBiometric(it)
                                }
                            )
                        }
                    }
                }
            }

            // 系统权限卡片 - 新增
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = HermesColors.Success
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "系统权限",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 文件访问权限
                        PermissionRow(
                            name = "文件访问",
                            purpose = "用于数据导入/导出文件读写",
                            isRequired = true
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = HermesColors.CardBorder
                        )

                        // 生物识别权限
                        PermissionRow(
                            name = "生物识别",
                            purpose = "用于指纹/面容快速解锁应用",
                            isRequired = false
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = HermesColors.CardBorder
                        )

                        // 通知推送权限
                        PermissionRow(
                            name = "通知推送",
                            purpose = "用于到期提醒及时送达",
                            isRequired = false
                        )
                    }
                }
            }

            // 数据安全声明卡片 - 新增
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = HermesColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "数据安全说明",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 数据安全声明项
                        SecurityStatementRow(
                            icon = Icons.Filled.CloudOff,
                            text = "所有数据仅存储于本设备，不上传云端"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SecurityStatementRow(
                            icon = Icons.Filled.Security,
                            text = "数据库采用AES-256加密存储"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SecurityStatementRow(
                            icon = Icons.Filled.EnhancedEncryption,
                            text = "导出文件可选择加密保护"
                        )
                    }
                }
            }

            // 无第三方共享声明卡片 - 新增
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = HermesColors.Success
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "隐私承诺",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "本应用不与任何第三方共享数据",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HermesColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "无广告、无追踪、无数据分析",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted
                        )
                    }
                }
            }
        }
    }
}

/**
 * 权限行组件
 */
@Composable
private fun PermissionRow(
    name: String,
    purpose: String,
    isRequired: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${name}权限",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = purpose,
                fontSize = 12.sp,
                color = HermesColors.TextMuted
            )
        }

        // 必要/可选标签
        Surface(
            modifier = Modifier.height(20.dp),
            shape = RoundedCornerShape(4.dp),
            color = if (isRequired) HermesColors.Success else HermesColors.TextMuted.copy(alpha = 0.3f)
        ) {
            Text(
                text = if (isRequired) "必要" else "可选",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = HermesColors.TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

/**
 * 安全声明行组件
 */
@Composable
private fun SecurityStatementRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HermesColors.Success,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = HermesColors.TextSecondary
        )
    }
}

@Composable
private fun ToggleSwitch(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(24.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (enabled) HermesColors.Success
                else HermesColors.TextMuted.copy(alpha = 0.3f)
            )
            .clickable { onToggle(!enabled) },
        contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .padding(2.dp)
                .clip(RoundedCornerShape(50))
                .background(HermesColors.TextPrimary)
        )
    }
}