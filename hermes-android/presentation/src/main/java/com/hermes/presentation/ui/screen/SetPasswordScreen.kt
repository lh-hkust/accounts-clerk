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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 设置密码页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetPasswordScreen(
    onBackClick: () -> Unit,
    onSavePassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isPasswordValid = password.length >= 6
    val isConfirmValid = password == confirmPassword && confirmPassword.isNotEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("设置密码", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 密码说明
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = HermesColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "设置访问密码",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "密码用于保护您的数据安全，应用启动时需要输入密码解锁。",
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "密码长度至少6位",
                        fontSize = 12.sp,
                        color = HermesColors.TextMuted
                    )
                }
            }

            // 密码输入框
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "新密码",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("输入密码", color = HermesColors.TextMuted) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                                    tint = HermesColors.TextMuted
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isPasswordValid || password.isEmpty()) HermesColors.Primary else HermesColors.Danger,
                            unfocusedBorderColor = HermesColors.CardBorder
                        )
                    )

                    if (password.isNotEmpty() && !isPasswordValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "密码长度不足6位",
                            fontSize = 12.sp,
                            color = HermesColors.Danger
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "确认密码",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("再次输入密码", color = HermesColors.TextMuted) },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码",
                                    tint = HermesColors.TextMuted
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isConfirmValid || confirmPassword.isEmpty()) HermesColors.Primary else HermesColors.Danger,
                            unfocusedBorderColor = HermesColors.CardBorder
                        )
                    )

                    if (confirmPassword.isNotEmpty() && !isConfirmValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "两次密码不一致",
                            fontSize = 12.sp,
                            color = HermesColors.Danger
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            fontSize = 12.sp,
                            color = HermesColors.Danger
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮
            Button(
                onClick = {
                    if (!isPasswordValid) {
                        errorMessage = "密码长度不足6位"
                    } else if (!isConfirmValid) {
                        errorMessage = "两次密码不一致"
                    } else {
                        onSavePassword(password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                ),
                enabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "保存密码",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}