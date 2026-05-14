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
 * 隐私安全页面（与原型一致）
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
        }
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