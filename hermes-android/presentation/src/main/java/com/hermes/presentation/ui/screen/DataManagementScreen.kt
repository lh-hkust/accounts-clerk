package com.hermes.presentation.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.presentation.ui.component.*
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.ExportImportViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数据管理页面（与原型一致）
 * 支持加密导出和安全导入
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExportImportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // SAF文件创建 launcher（用于导出）
    val createFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            viewModel.writeExportFile(uri, context)
        }
    }

    // SAF文件打开 launcher（用于导入）
    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.readImportFile(uri, context)
        }
    }

    // 显示导出模式选择对话框
    var showExportModeDialog by remember { mutableStateOf(false) }
    var showSecureExportDialog by remember { mutableStateOf(false) }
    var showPlainExportConfirm by remember { mutableStateOf(false) }

    // 显示导入相关对话框
    var showImportPasswordDialog by remember { mutableStateOf(false) }
    var showImportPreviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("数据管理", fontWeight = FontWeight.Bold) },
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
            // 数据存储状态卡片
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HermesColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = HermesColors.Success,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "数据存储",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HermesColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "已加密",
                                    fontSize = 12.sp,
                                    color = HermesColors.TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DataRow(label = "验证渠道", value = "${uiState.identifierCount} 个")
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRow(label = "账号数量", value = "${uiState.accountCount} 个")
                        Spacer(modifier = Modifier.height(8.dp))
                        DataRow(label = "数据库大小", value = uiState.databaseSize)
                    }
                }
            }

            // 数据导入标题
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = null,
                        tint = HermesColors.Success
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "数据导入",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
            }

            // 数据导入卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "支持格式: .hexport（加密） / .json（明文）",
                            fontSize = 12.sp,
                            color = HermesColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                openFileLauncher.launch(arrayOf("application/octet-stream", "application/json"))
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.UploadFile,
                                contentDescription = null,
                                tint = HermesColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "选择导入文件",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "导入模式: 合并 / 覆盖 / 跳过重复",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted
                        )
                    }
                }
            }

            // 数据导出标题
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = HermesColors.Secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "数据导出",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
            }

            // 数据导出卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "导出全部数据，可选择加密保护",
                            fontSize = 12.sp,
                            color = HermesColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 安全导出按钮（推荐）
                        Button(
                            onClick = {
                                showSecureExportDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HermesColors.Primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = HermesColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "安全导出（加密）",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 明文导出按钮
                        OutlinedButton(
                            onClick = {
                                showExportModeDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Code,
                                contentDescription = null,
                                tint = HermesColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "导出明文JSON",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 加密提示
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = HermesColors.Success.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = HermesColors.Success
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "安全导出使用AES-256-GCM加密",
                                    fontSize = 12.sp,
                                    color = HermesColors.Success
                                )
                            }
                        }
                    }
                }
            }

            // 危险操作卡片
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HermesColors.Danger.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = HermesColors.Danger
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "危险操作",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.Danger
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.showClearConfirm() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                            )
                        ) {
                            Text(
                                text = "清空所有数据",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.Danger
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "此操作不可撤销",
                            fontSize = 12.sp,
                            color = HermesColors.TextMuted,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

    // 导出模式选择对话框（安全警告）
    if (showExportModeDialog) {
        ExportModeSelectionDialog(
            onDismiss = { showExportModeDialog = false },
            onPlainJsonExport = {
                showExportModeDialog = false
                showPlainExportConfirm = true
            },
            onSecureExport = {
                showExportModeDialog = false
                showSecureExportDialog = true
            }
        )
    }

    // 明文导出风险确认
    if (showPlainExportConfirm) {
        PlainExportConfirmDialog(
            onDismiss = { showPlainExportConfirm = false },
            onConfirm = {
                showPlainExportConfirm = false
                val fileName = generateExportFileName(false)
                createFileLauncher.launch(fileName)
                viewModel.startPlainExport()
            }
        )
    }

    // 安全导出密码设置对话框
    if (showSecureExportDialog) {
        SecureExportPasswordDialog(
            onDismiss = { showSecureExportDialog = false },
            onExport = { password ->
                showSecureExportDialog = false
                viewModel.startSecureExport(password)
                val fileName = generateExportFileName(true)
                createFileLauncher.launch(fileName)
            }
        )
    }

    // 导出进度对话框
    if (uiState.showExportProgress) {
        ExportProgressDialog(
            progress = uiState.exportProgress,
            stage = uiState.exportStage,
            isComplete = uiState.exportComplete,
            filePath = uiState.exportFilePath,
            onDismiss = { viewModel.dismissExportProgress() },
            onCopyPath = { viewModel.copyExportPath(context) }
        )
    }

    // 导入密码输入对话框
    if (uiState.showImportPasswordDialog) {
        ImportPasswordDialog(
            fileName = uiState.importFileName,
            onDismiss = { viewModel.dismissImportPasswordDialog() },
            onDecrypt = { password -> viewModel.decryptImportFile(password) },
            errorMessage = uiState.importPasswordError
        )
    }

    // 导入预览对话框
    if (uiState.showImportPreview && uiState.importPreview != null) {
        ImportPreviewDialog(
            preview = uiState.importPreview!!,
            onDismiss = { viewModel.dismissImportPreview() },
            onImport = { mode -> viewModel.executeImport(mode) }
        )
    }

    // 导入进度对话框
    if (uiState.showImportProgress) {
        ImportProgressDialog(
            progress = uiState.importProgress,
            stage = uiState.importStage,
            isComplete = uiState.importComplete,
            result = uiState.importResult,
            onDismiss = { viewModel.dismissImportProgress() }
        )
    }

    // 清空数据确认对话框
    if (uiState.showClearConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearConfirm() },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmClearData() },
                    colors = ButtonDefaults.buttonColors(containerColor = HermesColors.Danger)
                ) {
                    Text("确认清空", color = HermesColors.TextPrimary)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissClearConfirm() }) {
                    Text("取消")
                }
            },
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = HermesColors.Danger)
            },
            title = { Text("确认清空所有数据？") },
            text = {
                Text("此操作将删除所有验证渠道、账号和绑定关系数据，且不可恢复。")
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = HermesColors.Surface
        )
    }
}

/**
 * 明文导出风险确认对话框
 */
@Composable
private fun PlainExportConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var riskConfirmed by remember { mutableStateOf(false) }

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
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "我已了解风险",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesColors.TextPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = riskConfirmed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Warning
                )
            ) {
                Text("继续导出")
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
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = HermesColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = HermesColors.TextPrimary
        )
    }
}

/**
 * 生成导出文件名
 */
private fun generateExportFileName(isEncrypted: Boolean): String {
    val dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    return if (isEncrypted) {
        "hermes_export_$dateStr.hexport"
    } else {
        "hermes_export_$dateStr.json"
    }
}