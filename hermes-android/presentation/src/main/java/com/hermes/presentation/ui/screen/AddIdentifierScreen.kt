package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.OperationState

/**
 * 添加标识页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIdentifierScreen(
    operationState: OperationState,
    onBackClick: () -> Unit,
    onSaveClick: (IdentifierType, String) -> Unit,
    onCheckDuplicate: (IdentifierType, String, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(IdentifierType.PHONE) }
    var value by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("添加验证渠道") },
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
                .padding(16.dp)
                .background(HermesColors.Background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 类型选择
            Text(
                text = "选择类型",
                style = MaterialTheme.typography.titleMedium,
                color = HermesColors.TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == IdentifierType.PHONE,
                    onClick = { selectedType = IdentifierType.PHONE },
                    label = { Text("📱 手机号") }
                )
                FilterChip(
                    selected = selectedType == IdentifierType.EMAIL,
                    onClick = { selectedType = IdentifierType.EMAIL },
                    label = { Text("📧 邮箱") }
                )
            }

            // 输入值
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    value = newValue
                    isError = false
                    errorMessage = ""
                },
                label = {
                    Text(if (selectedType == IdentifierType.PHONE) "手机号" else "邮箱地址")
                },
                placeholder = {
                    Text(if (selectedType == IdentifierType.PHONE) "请输入手机号" else "请输入邮箱地址")
                },
                isError = isError,
                supportingText = if (isError) {
                    { Text(errorMessage) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 操作状态提示
            when (operationState) {
                is OperationState.InProgress -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = HermesColors.Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("保存中...", color = HermesColors.TextSecondary)
                    }
                }
                is OperationState.Error -> {
                    Text(
                        text = operationState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.Danger
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮
            Button(
                onClick = {
                    if (value.isBlank()) {
                        isError = true
                        errorMessage = "请输入标识值"
                    } else {
                        onCheckDuplicate(selectedType, value) { isDuplicate ->
                            if (isDuplicate) {
                                isError = true
                                errorMessage = "标识已存在"
                            } else {
                                onSaveClick(selectedType, value)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is OperationState.InProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                )
            ) {
                Text("保存")
            }
        }
    }
}