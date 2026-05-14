package com.hermes.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 欢迎页 - 首次启动引导（与原型一致）
 */
@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        visible = true
        delay(1500)
        showButton = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HermesColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo动画
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(1000, easing = EaseOutBack)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Logo图标 - 原型样式：渐变背景+盾牌图标+微倾斜
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(HermesColors.Primary, HermesColors.Secondary)
                                )
                            )
                            .rotate(6f),
                        contentAlignment = Alignment.Center
                    ) {
                        // 盾牌图标用文字或矢量图表示
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Hermes Shield",
                            modifier = Modifier.size(36.dp),
                            tint = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 应用名称
                    Text(
                        text = "Hermes",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 副标题 - 原型样式
                    Text(
                        text = "2026 智能账号资产守护专家",
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary
                    )
                    Text(
                        text = "管理数百账号，从不丢失",
                        fontSize = 12.sp,
                        color = HermesColors.TextMuted
                    )
                }
            }

            // 功能介绍卡片 - 原型样式：两列卡片布局
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 500))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 渠道绑定卡片
                    FeatureCard(
                        icon = "link",
                        iconBgColor = HermesColors.Primary.copy(alpha = 0.2f),
                        iconColor = HermesColors.Primary,
                        title = "渠道绑定",
                        subtitle = "智能追踪账号绑定关系",
                        modifier = Modifier.weight(1f)
                    )
                    // 到期提醒卡片
                    FeatureCard(
                        icon = "bell",
                        iconBgColor = HermesColors.Warning.copy(alpha = 0.2f),
                        iconColor = HermesColors.Warning,
                        title = "到期提醒",
                        subtitle = "提前提醒验证渠道失效风险",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 开始按钮 - 原型样式：渐变背景
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500)
                )
            ) {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "立即开启",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 当前日期 - 原型样式
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 1000))
            ) {
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
                Text(
                    text = "当前日期: ${today.format(formatter)}",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    iconBgColor: androidx.compose.ui.graphics.Color,
    iconColor: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HermesColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 图标背景
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                // 使用Font Awesome风格的图标映射
                when (icon) {
                    "link" -> Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    "bell" -> Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HermesColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = HermesColors.TextMuted
            )
        }
    }
}