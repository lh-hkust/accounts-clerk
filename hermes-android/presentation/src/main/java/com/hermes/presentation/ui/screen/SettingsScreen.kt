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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 设置页面（与原型一致）
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onDataManageClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题 - 原型样式（底部导航页无需返回）
        item {
            Text(
                text = "设置",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp),
                color = HermesColors.TextPrimary
            )
        }

        // 用户信息卡片 - MVP单机版本：显示"本地用户（单机版）"，无退出登录功能
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 头像 - 原型样式：渐变圆形背景
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(HermesColors.Primary, HermesColors.Secondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "用户",
                            modifier = Modifier.size(24.dp),
                            tint = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "本地用户（单机版）",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "数据仅保存在本设备",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted
                        )
                    }
                }
            }
        }

        // 设置项卡片 - 原型样式
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column {
                    // 数据管理
                    SettingItemRow(
                        icon = Icons.Filled.Download,
                        iconColor = HermesColors.Primary,
                        title = "数据管理",
                        onClick = onDataManageClick
                    )
                    HorizontalDivider(color = HermesColors.CardBorder)
                    // 通知设置
                    SettingItemRow(
                        icon = Icons.Filled.Notifications,
                        iconColor = HermesColors.Warning,
                        title = "通知设置",
                        onClick = onNotificationClick
                    )
                    HorizontalDivider(color = HermesColors.CardBorder)
                    // 隐私安全
                    SettingItemRow(
                        icon = Icons.Filled.Lock,
                        iconColor = HermesColors.Success,
                        title = "隐私安全",
                        onClick = onPrivacyClick
                    )
                }
            }
        }

        // 关于卡片 - 原型样式
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onAboutClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "关于",
                        tint = HermesColors.TextMuted
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "关于",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "1.0.0",
                        fontSize = 12.sp,
                        color = HermesColors.TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = HermesColors.TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = HermesColors.TextMuted
        )
    }
}