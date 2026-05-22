package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
 * 关于页面（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("关于", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(HermesColors.Background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo图标 - 原型样式
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(HermesColors.Primary, HermesColors.Secondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = "Hermes Shield",
                    modifier = Modifier.size(32.dp),
                    tint = HermesColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 应用名称 - 原型样式
            Text(
                text = "Hermes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HermesColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "账号资产守护专家",
                fontSize = 12.sp,
                color = HermesColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 版本信息卡片 - 原型样式
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "版本",
                        fontSize = 12.sp,
                        color = HermesColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1.0.0",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 已是最新按钮 - 原型样式
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = HermesColors.Success
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "已是最新",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 项目主页链接 - 原型样式
            Text(
                text = "项目主页: github.com/hermes-app",
                fontSize = 12.sp,
                color = HermesColors.Primary
            )
        }
    }
}