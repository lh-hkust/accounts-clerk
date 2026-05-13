package com.hermes.presentation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hermes.presentation.ui.theme.HermesColors
import kotlinx.coroutines.delay

/**
 * 欢迎页 - 首次启动引导
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
            verticalArrangement = Arrangement.Center
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
                    // Logo图标
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                HermesColors.Primary,
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "H",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 应用名称
                    Text(
                        text = "Hermes",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.Primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 副标题
                    Text(
                        text = "账号管理工具",
                        style = MaterialTheme.typography.bodyLarge,
                        color = HermesColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // 功能介绍
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 500))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FeatureItem(
                                icon = "📱",
                                text = "管理手机号、邮箱验证渠道"
                            )
                            FeatureItem(
                                icon = "👤",
                                text = "记录各应用账户绑定关系"
                            )
                            FeatureItem(
                                icon = "⚠️",
                                text = "停用计划预警提醒"
                            )
                            FeatureItem(
                                icon = "🔐",
                                text = "本地加密存储，数据安全"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 开始按钮
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
                        .padding(horizontal = 32.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Primary
                    )
                ) {
                    Text(
                        text = "开始使用",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 版本信息
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 1000)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = HermesColors.TextMuted,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            color = HermesColors.Accent
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = HermesColors.TextSecondary
        )
    }
}