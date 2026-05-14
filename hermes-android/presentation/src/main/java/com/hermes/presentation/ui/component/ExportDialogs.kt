package com.hermes.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.model.ImportMode
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 导出模式选择对话框
 * 用户可以选择明文JSON导出或安全导出
 *
 * @see spec.md Requirement: User can choose export mode
 */
@Composable
fun ExportModeSelectionDialog(
    onDismiss: () -> Unit,
    onPlainJsonExport: () -> Unit,
    onSecureExport: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = HermesColors.Warning,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "安全警告",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "导出为明文JSON文件，数据无加密保护。",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "任何人均可查看文件内容，存在隐私泄露风险。",
                    fontSize = 14.sp,
                    color = HermesColors.Danger
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HermesColors.Primary.copy(alpha = 0.1f)
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
                            text = "建议使用「安全导出」保护您的数据。",
                            fontSize = 12.sp,
                            color = HermesColors.Primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSecureExport,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                )
            ) {
                Icon(Icons.Filled.Security, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("使用安全导出")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onPlainJsonExport,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HermesColors.TextSecondary
                )
            ) {
                Text("继续导出JSON")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

/**
 * 安全导出密码设置对话框
 * 用户可以设置可选密码，无密码需勾选风险确认
 *
 * @see spec.md Requirement: User can set password for secure export
 */
@Composable
fun SecureExportPasswordDialog(
    onDismiss: () -> Unit,
    onExport: (password: String?) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var riskConfirmed by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }
    var showConfirmError by remember { mutableStateOf(false) }

    val isPasswordSet = password.isNotEmpty()
    val canExport = if (isPasswordSet) {
        password.length >= 6 && password == confirmPassword
    } else {
        riskConfirmed
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "安全导出",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "为导出文件设置密码可保护数据安全。",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "若不设置密码，文件仍加密但任何拥有本应用的人都能打开。",
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 密码输入框
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showPasswordError = false
                        showConfirmError = false
                    },
                    label = { Text("密码（可选）") },
                    placeholder = { Text("至少6个字符") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    isError = showPasswordError && password.isNotEmpty() && password.length < 6,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showPasswordError && password.isNotEmpty() && password.length < 6) {
                    Text(
                        text = "密码长度不足6个字符",
                        fontSize = 12.sp,
                        color = HermesColors.Danger
                    )
                }

                // 确认密码框（仅密码设置时显示）
                if (password.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            showConfirmError = false
                        },
                        label = { Text("确认密码") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        isError = showConfirmError && confirmPassword.isNotEmpty() && password != confirmPassword,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showConfirmError && confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Text(
                            text = "两次密码输入不一致",
                            fontSize = 12.sp,
                            color = HermesColors.Danger
                        )
                    }
                }

                // 无密码风险确认（仅无密码时显示）
                if (password.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                if (riskConfirmed) 1.dp else 2.dp,
                                HermesColors.Warning.copy(alpha = if (riskConfirmed) 0.3f else 0.5f),
                                RoundedCornerShape(8.dp)
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HermesColors.Warning.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = riskConfirmed,
                                onCheckedChange = { riskConfirmed = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = HermesColors.Warning
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "我已了解风险，不设置密码",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = HermesColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "文件可被任何拥有本应用的人打开",
                                    fontSize = 12.sp,
                                    color = HermesColors.Warning
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPasswordSet) {
                        if (password.length < 6) {
                            showPasswordError = true
                        } else if (password != confirmPassword) {
                            showConfirmError = true
                        } else {
                            onExport(password)
                        }
                    } else {
                        if (riskConfirmed) {
                            onExport(null)
                        }
                    }
                },
                enabled = canExport,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPasswordSet) HermesColors.Primary else HermesColors.Warning
                )
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("开始导出")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

/**
 * 导出进度对话框
 * 显示进度条、百分比和阶段文字
 *
 * @see spec.md Requirement: User can view export progress
 */
@Composable
fun ExportProgressDialog(
    progress: Int,
    stage: String,
    isComplete: Boolean = false,
    filePath: String? = null,
    onDismiss: () -> Unit,
    onCopyPath: () -> Unit = {},
    onOpenFile: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = if (isComplete) onDismiss else { {} },
        title = {
            Text(
                text = if (isComplete) "导出完成" else "正在导出",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                if (!isComplete) {
                    // 进度条
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = HermesColors.Primary,
                        trackColor = HermesColors.Surface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 百分比
                    Text(
                        text = "$progress%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.Primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 阶段文字
                    Text(
                        text = stage,
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    // 完成状态
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = HermesColors.Success,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "文件已加密保存",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.Success
                        )
                    }

                    if (filePath != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = HermesColors.Background
                            )
                        ) {
                            Text(
                                text = filePath,
                                fontSize = 12.sp,
                                color = HermesColors.TextMuted,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isComplete) {
                Row {
                    if (filePath != null) {
                        OutlinedButton(onClick = onCopyPath) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("复制路径")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(onClick = onDismiss) {
                        Text("关闭")
                    }
                }
            } else {
                // 导出过程中不显示按钮
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

/**
 * 导入密码输入对话框
 * 用于密码加密的.hexport文件解密
 *
 * @see spec.md Requirement: User can import encrypted and plain files
 */
@Composable
fun ImportPasswordDialog(
    fileName: String,
    onDismiss: () -> Unit,
    onDecrypt: (password: String) -> Unit,
    errorMessage: String? = null
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = HermesColors.Primary
            )
        },
        title = {
            Text(
                text = "输入密码",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "文件 \"$fileName\" 已加密，请输入密码解密。",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = HermesColors.Danger
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onDecrypt(password) },
                enabled = password.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                )
            ) {
                Icon(Icons.Filled.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("解密")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

/**
 * 导入预览对话框
 * 显示数据摘要、冲突检测和导入模式选择
 *
 * @see spec.md Requirement: User can preview import data
 */
@Composable
fun ImportPreviewDialog(
    preview: ImportPreviewData,
    onDismiss: () -> Unit,
    onImport: (ImportMode) -> Unit
) {
    var selectedMode by remember { mutableStateOf(ImportMode.MERGE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "导入预览",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                // 数据摘要
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HermesColors.Surface.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PreviewDataRow("验证渠道", "${preview.identifierCount} 个")
                        Spacer(modifier = Modifier.height(8.dp))
                        PreviewDataRow("账号", "${preview.accountCount} 个")
                        Spacer(modifier = Modifier.height(8.dp))
                        PreviewDataRow("绑定关系", "${preview.bindingCount} 个")
                        if (preview.applicationCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PreviewDataRow("应用平台", "${preview.applicationCount} 个")
                        }
                    }
                }

                // 冲突提示
                if (preview.hasConflicts) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HermesColors.Warning.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = HermesColors.Warning
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "发现 ${preview.conflictCount} 个重复数据",
                                fontSize = 14.sp,
                                color = HermesColors.Warning
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 导入模式选择
                Text(
                    text = "导入模式",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                ImportModeSelector(
                    selectedMode = selectedMode,
                    onModeChange = { selectedMode = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onImport(selectedMode) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                )
            ) {
                Icon(Icons.Filled.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("开始导入")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

@Composable
private fun PreviewDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = HermesColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HermesColors.TextPrimary
        )
    }
}

/**
 * 导入模式选择器
 */
@Composable
private fun ImportModeSelector(
    selectedMode: ImportMode,
    onModeChange: (ImportMode) -> Unit
) {
    Column {
        ImportModeCard(
            mode = ImportMode.MERGE,
            title = "合并",
            description = "新增不存在数据，保留已有数据",
            selected = selectedMode == ImportMode.MERGE,
            onClick = { onModeChange(ImportMode.MERGE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImportModeCard(
            mode = ImportMode.OVERWRITE,
            title = "覆盖",
            description = "新增不存在数据，替换已有数据",
            selected = selectedMode == ImportMode.OVERWRITE,
            onClick = { onModeChange(ImportMode.OVERWRITE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ImportModeCard(
            mode = ImportMode.SKIP_DUPLICATE,
            title = "跳过重复",
            description = "仅导入不存在数据，跳过所有重复",
            selected = selectedMode == ImportMode.SKIP_DUPLICATE,
            onClick = { onModeChange(ImportMode.SKIP_DUPLICATE) }
        )
    }
}

@Composable
private fun ImportModeCard(
    mode: ImportMode,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) HermesColors.Primary else HermesColors.CardBorder,
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) HermesColors.Primary.copy(alpha = 0.1f) else HermesColors.Surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = HermesColors.Primary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HermesColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = HermesColors.TextMuted
                )
            }
        }
    }
}

/**
 * 导入进度对话框
 */
@Composable
fun ImportProgressDialog(
    progress: Int,
    stage: String,
    isComplete: Boolean = false,
    result: ImportResultData? = null,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isComplete) onDismiss else { {} },
        title = {
            Text(
                text = if (isComplete) "导入完成" else "正在导入",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                if (!isComplete) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = HermesColors.Success,
                        trackColor = HermesColors.Surface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$progress%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.Success,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stage,
                        fontSize = 14.sp,
                        color = HermesColors.TextSecondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = HermesColors.Success,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "导入完成！",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.Success
                        )
                    }

                    if (result != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "新增 ${result.addedCount}条，更新 ${result.updatedCount}条，跳过 ${result.skippedCount}条",
                            fontSize = 14.sp,
                            color = HermesColors.TextSecondary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isComplete) {
                Button(onClick = onDismiss) {
                    Text("关闭")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = HermesColors.Surface
    )
}

/**
 * 导入预览数据
 */
data class ImportPreviewData(
    val identifierCount: Int,
    val accountCount: Int,
    val bindingCount: Int,
    val applicationCount: Int,
    val hasConflicts: Boolean,
    val conflictCount: Int
)

/**
 * 导入结果数据
 */
data class ImportResultData(
    val addedCount: Int,
    val updatedCount: Int,
    val skippedCount: Int
)

/**
 * 导入模式（使用domain层的ImportMode）
 */