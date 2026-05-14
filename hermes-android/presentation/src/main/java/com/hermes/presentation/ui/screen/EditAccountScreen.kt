package com.hermes.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.IdentifierOption
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.OperationState

/**
 * 编辑账号页面
 *
 * 功能：
 * - 可编辑字段：accountName、accountIdentifier、nickname、status
 * - 应用字段不可编辑（创建后固定）
 * - 编辑时校验账号ID唯一性
 * - 可新增/删除绑定渠道
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    accountId: Long,
    applicationId: Long,
    applicationName: String,
    applicationCategory: String?,
    initialAccountName: String,
    initialAccountIdentifier: String?,
    initialNickname: String?,
    initialStatus: AccountStatus,
    initialBindings: List<BindingInfo>,
    onBackClick: () -> Unit,
    onSaveClick: (
        accountName: String,
        accountIdentifier: String?,
        nickname: String?,
        status: AccountStatus,
        bindings: List<BindingInfo>
    ) -> Unit,
    onCheckDuplicate: (applicationId: Long, accountIdentifier: String, excludeAccountId: Long, callback: (Boolean) -> Unit) -> Unit,
    availableIdentifiers: List<IdentifierOption> = emptyList(),
    operationState: OperationState = OperationState.Idle,
    modifier: Modifier = Modifier
) {
    var accountName by remember { mutableStateOf(initialAccountName) }
    var accountIdentifier by remember { mutableStateOf(initialAccountIdentifier ?: "") }
    var nickname by remember { mutableStateOf(initialNickname ?: "") }
    var selectedStatus by remember { mutableStateOf(initialStatus) }
    var bindings by remember { mutableStateOf(initialBindings.toMutableList()) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showBindingDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var deactivatedExpanded by remember { mutableStateOf(false) }
    var duplicateError by remember { mutableStateOf<String?>(null) }
    var hasCheckedDuplicate by remember { mutableStateOf(true) } // 初始值已通过

    val appColor = getAppIconColor(applicationName)

    // 渠道按状态分组排序: ACTIVE > PENDING_DEACTIVATION > DEACTIVATED/INVALIDATED
    val sortedIdentifiers = remember(availableIdentifiers) {
        availableIdentifiers.sortedBy { identifier ->
            when (identifier.status) {
                IdentifierStatus.ACTIVE -> 0
                IdentifierStatus.PENDING_DEACTIVATION -> 1
                IdentifierStatus.DEACTIVATED -> 2
                IdentifierStatus.INVALIDATED -> 3
            }
        }
    }

    // 搜索过滤后的渠道（排除已绑定的）
    val filteredIdentifiers = remember(sortedIdentifiers, searchQuery, bindings) {
        val boundIds = bindings.map { it.identifierId }
        sortedIdentifiers
            .filter { it.id !in boundIds }
            .filter { if (searchQuery.isEmpty()) true else it.value.contains(searchQuery, ignoreCase = true) }
    }

    // 分组渠道
    val activeIdentifiers = filteredIdentifiers.filter { it.status == IdentifierStatus.ACTIVE }
    val pendingIdentifiers = filteredIdentifiers.filter { it.status == IdentifierStatus.PENDING_DEACTIVATION }
    val deactivatedIdentifiers = filteredIdentifiers.filter {
        it.status == IdentifierStatus.DEACTIVATED || it.status == IdentifierStatus.INVALIDATED
    }

    val isLoading = operationState == OperationState.InProgress

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("编辑账号", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 应用信息卡片（不可编辑）
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(appColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = applicationName.take(1),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = applicationName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = HermesColors.TextPrimary
                        )
                        if (applicationCategory != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = applicationCategory,
                                fontSize = 12.sp,
                                color = HermesColors.TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 不可编辑提示
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "应用固定",
                        tint = HermesColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 账号名称输入框
            OutlinedTextField(
                value = accountName,
                onValueChange = {
                    accountName = it
                    hasCheckedDuplicate = false
                    duplicateError = null
                },
                label = { Text("账号名称", color = HermesColors.TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.CardBorder,
                    errorBorderColor = HermesColors.Danger
                ),
                isError = duplicateError != null,
                supportingText = if (duplicateError != null) {
                    { Text(duplicateError!!, color = HermesColors.Danger) }
                } else null
            )

            // 账号ID输入框
            OutlinedTextField(
                value = accountIdentifier,
                onValueChange = {
                    accountIdentifier = it
                    hasCheckedDuplicate = false
                    duplicateError = null
                },
                label = { Text("账号ID / 用户名", color = HermesColors.TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.CardBorder,
                    errorBorderColor = HermesColors.Danger
                ),
                isError = duplicateError != null,
                supportingText = if (duplicateError != null) {
                    { Text(duplicateError!!, color = HermesColors.Danger) }
                } else null
            )

            // 昵称输入框
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("昵称（可选）", color = HermesColors.TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.CardBorder
                )
            )

            // 状态选择（点击打开对话框）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HermesColors.Surface)
                    .border(1.dp, HermesColors.CardBorder, RoundedCornerShape(12.dp))
                    .clickable { showStatusDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "账号状态",
                    fontSize = 14.sp,
                    color = HermesColors.TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(getStatusColor(selectedStatus).copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = getStatusText(selectedStatus),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = getStatusColor(selectedStatus)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "选择状态",
                    tint = HermesColors.TextMuted
                )
            }

            // 绑定渠道标题
            Text(
                text = "绑定验证渠道",
                fontSize = 12.sp,
                color = HermesColors.TextSecondary
            )

            // 已绑定渠道列表
            if (bindings.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bindings) { binding ->
                        BindingItemCard(
                            binding = binding,
                            availableIdentifiers = availableIdentifiers,
                            onRemove = {
                                bindings = bindings.filter { it.identifierId != binding.identifierId }.toMutableList()
                            },
                            onEditPurpose = { newPurposes ->
                                bindings = bindings.map {
                                    if (it.identifierId == binding.identifierId) {
                                        it.copy(purposes = newPurposes)
                                    } else it
                                }.toMutableList()
                            }
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "暂无绑定渠道",
                        fontSize = 14.sp,
                        color = HermesColors.TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 添加渠道按钮
            OutlinedButton(
                onClick = { showBindingDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HermesColors.Primary
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加绑定渠道", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮
            Button(
                onClick = {
                    // 先校验唯一性
                    if (accountIdentifier.isNotEmpty() && !hasCheckedDuplicate) {
                        onCheckDuplicate(applicationId, accountIdentifier, accountId) { isDuplicate ->
                            if (isDuplicate) {
                                duplicateError = "账号ID已存在，请使用其他ID"
                            } else {
                                hasCheckedDuplicate = true
                                duplicateError = null
                                onSaveClick(
                                    accountName,
                                    if (accountIdentifier.isNotEmpty()) accountIdentifier else null,
                                    if (nickname.isNotEmpty()) nickname else null,
                                    selectedStatus,
                                    bindings
                                )
                            }
                        }
                    } else {
                        onSaveClick(
                            accountName,
                            if (accountIdentifier.isNotEmpty()) accountIdentifier else null,
                            if (nickname.isNotEmpty()) nickname else null,
                            selectedStatus,
                            bindings
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                ),
                enabled = !isLoading && accountName.isNotEmpty() && duplicateError == null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = HermesColors.TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "保存修改",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // 状态选择对话框
    if (showStatusDialog) {
        StatusSelectionDialog(
            currentStatus = selectedStatus,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }

    // 添加绑定渠道对话框
    if (showBindingDialog && filteredIdentifiers.isNotEmpty()) {
        AddBindingDialog(
            availableIdentifiers = filteredIdentifiers,
            activeIdentifiers = activeIdentifiers,
            pendingIdentifiers = pendingIdentifiers,
            deactivatedIdentifiers = deactivatedIdentifiers,
            deactivatedExpanded = deactivatedExpanded,
            onToggleDeactivated = { deactivatedExpanded = !deactivatedExpanded },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onBindingAdded = { identifierId, purposes ->
                val identifier = availableIdentifiers.find { it.id == identifierId }
                if (identifier != null) {
                    bindings.add(BindingInfo(identifierId, identifier.value, identifier.type, purposes))
                }
                showBindingDialog = false
                searchQuery = ""
            },
            onDismiss = {
                showBindingDialog = false
                searchQuery = ""
            }
        )
    }
}

/**
 * 绑定信息数据类
 */
data class BindingInfo(
    val identifierId: Long,
    val identifierValue: String,
    val identifierType: IdentifierType,
    val purposes: Set<BindingPurpose>
)

/**
 * 已绑定渠道卡片
 */
@Composable
private fun BindingItemCard(
    binding: BindingInfo,
    availableIdentifiers: List<IdentifierOption>,
    onRemove: () -> Unit,
    onEditPurpose: (Set<BindingPurpose>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPurposeDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型图标
            Icon(
                imageVector = if (binding.identifierType == IdentifierType.EMAIL)
                    Icons.Filled.Email
                else
                    Icons.Filled.Phone,
                contentDescription = null,
                tint = HermesColors.Primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = binding.identifierValue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextPrimary
                )

                // 用途色点
                if (binding.purposes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        binding.purposes.forEach { purpose ->
                            PurposeDot(purpose = purpose)
                        }
                    }
                }
            }

            // 编辑用途按钮
            IconButton(
                onClick = { showPurposeDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "编辑用途",
                    tint = HermesColors.TextSecondary
                )
            }

            // 删除按钮
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "删除绑定",
                    tint = HermesColors.Danger
                )
            }
        }
    }

    // 用途编辑对话框
    if (showPurposeDialog) {
        PurposeEditDialog(
            currentPurposes = binding.purposes,
            onPurposesSelected = { newPurposes ->
                onEditPurpose(newPurposes)
                showPurposeDialog = false
            },
            onDismiss = { showPurposeDialog = false }
        )
    }
}

/**
 * 用途色点
 */
@Composable
private fun PurposeDot(purpose: BindingPurpose) {
    val color = when (purpose) {
        BindingPurpose.LOGIN -> Color(0xFF4CAF50)
        BindingPurpose.VERIFICATION -> HermesColors.Primary
        BindingPurpose.RECOVERY -> Color(0xFF2196F3)
        BindingPurpose.NOTIFICATION -> Color(0xFFFF9800)
        BindingPurpose.SECONDARY_AUTH -> Color(0xFF9C27B0)
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * 状态选择对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusSelectionDialog(
    currentStatus: AccountStatus,
    onStatusSelected: (AccountStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val statuses = listOf(
        AccountStatus.ACTIVE,
        AccountStatus.FROZEN,
        AccountStatus.LOST,
        AccountStatus.ARCHIVED
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "选择账号状态",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                statuses.forEach { status ->
                    val isSelected = currentStatus == status
                    val statusText = getStatusText(status)
                    val statusColor = getStatusColor(status)
                    val statusDescription = getStatusDescription(status)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) HermesColors.Primary.copy(alpha = 0.1f)
                                else HermesColors.Surface.copy(alpha = 0.5f)
                            )
                            .then(
                                if (isSelected)
                                    Modifier.border(1.dp, HermesColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                else Modifier
                            )
                            .clickable(enabled = !isSelected) { onStatusSelected(status) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getStatusIcon(status),
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = statusText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) HermesColors.Primary else HermesColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusDescription,
                                fontSize = 12.sp,
                                color = HermesColors.TextMuted
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "当前状态",
                                tint = HermesColors.Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "关闭",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * 添加绑定渠道对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBindingDialog(
    availableIdentifiers: List<IdentifierOption>,
    activeIdentifiers: List<IdentifierOption>,
    pendingIdentifiers: List<IdentifierOption>,
    deactivatedIdentifiers: List<IdentifierOption>,
    deactivatedExpanded: Boolean,
    onToggleDeactivated: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBindingAdded: (Long, Set<BindingPurpose>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIdentifierId by remember { mutableStateOf<Long?>(null) }
    var selectedPurposes by remember { mutableStateOf(setOf(BindingPurpose.VERIFICATION)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "添加绑定渠道",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("搜索渠道...", color = HermesColors.TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "搜索", tint = HermesColors.TextMuted)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HermesColors.Primary,
                        unfocusedBorderColor = HermesColors.CardBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 渠道列表
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 正常使用分组
                    if (activeIdentifiers.isNotEmpty()) {
                        item {
                            ChannelGroupHeader(
                                title = "正常使用",
                                count = activeIdentifiers.size,
                                statusColor = HermesColors.Primary
                            )
                        }
                        items(activeIdentifiers) { identifier ->
                            SelectableChannelCard(
                                identifier = identifier,
                                isSelected = selectedIdentifierId == identifier.id,
                                onClick = {
                                    selectedIdentifierId = identifier.id
                                    selectedPurposes = setOf(BindingPurpose.VERIFICATION)
                                }
                            )
                        }
                    }

                    // 即将到期分组
                    if (pendingIdentifiers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = HermesColors.CardBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            ChannelGroupHeader(
                                title = "即将到期",
                                count = pendingIdentifiers.size,
                                statusColor = Color(0xFFFF9800)
                            )
                        }
                        items(pendingIdentifiers) { identifier ->
                            SelectableChannelCard(
                                identifier = identifier,
                                isSelected = selectedIdentifierId == identifier.id,
                                onClick = {
                                    selectedIdentifierId = identifier.id
                                    selectedPurposes = setOf(BindingPurpose.VERIFICATION)
                                }
                            )
                        }
                    }

                    // 已失效分组（默认折叠）
                    if (deactivatedIdentifiers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = HermesColors.CardBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            ChannelGroupHeader(
                                title = "已失效",
                                count = deactivatedIdentifiers.size,
                                statusColor = HermesColors.TextMuted,
                                isExpandable = true,
                                isExpanded = deactivatedExpanded,
                                onToggle = onToggleDeactivated
                            )
                        }
                        if (deactivatedExpanded) {
                            items(deactivatedIdentifiers) { identifier ->
                                SelectableChannelCard(
                                    identifier = identifier,
                                    isSelected = selectedIdentifierId == identifier.id,
                                    onClick = {
                                        selectedIdentifierId = identifier.id
                                        selectedPurposes = setOf(BindingPurpose.VERIFICATION)
                                    }
                                )
                            }
                        }
                    }
                }

                // 用途选择
                if (selectedIdentifierId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "绑定用途",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PurposeSelectChip(
                            purpose = BindingPurpose.LOGIN,
                            label = "登录",
                            selected = selectedPurposes.contains(BindingPurpose.LOGIN),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.LOGIN))
                                    selectedPurposes - BindingPurpose.LOGIN
                                else
                                    selectedPurposes + BindingPurpose.LOGIN
                            }
                        )
                        PurposeSelectChip(
                            purpose = BindingPurpose.VERIFICATION,
                            label = "验证",
                            selected = selectedPurposes.contains(BindingPurpose.VERIFICATION),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.VERIFICATION))
                                    selectedPurposes - BindingPurpose.VERIFICATION
                                else
                                    selectedPurposes + BindingPurpose.VERIFICATION
                            }
                        )
                        PurposeSelectChip(
                            purpose = BindingPurpose.RECOVERY,
                            label = "找回",
                            selected = selectedPurposes.contains(BindingPurpose.RECOVERY),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.RECOVERY))
                                    selectedPurposes - BindingPurpose.RECOVERY
                                else
                                    selectedPurposes + BindingPurpose.RECOVERY
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PurposeSelectChip(
                            purpose = BindingPurpose.NOTIFICATION,
                            label = "通知",
                            selected = selectedPurposes.contains(BindingPurpose.NOTIFICATION),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.NOTIFICATION))
                                    selectedPurposes - BindingPurpose.NOTIFICATION
                                else
                                    selectedPurposes + BindingPurpose.NOTIFICATION
                            }
                        )
                        PurposeSelectChip(
                            purpose = BindingPurpose.SECONDARY_AUTH,
                            label = "二次验证",
                            selected = selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH))
                                    selectedPurposes - BindingPurpose.SECONDARY_AUTH
                                else
                                    selectedPurposes + BindingPurpose.SECONDARY_AUTH
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedIdentifierId != null && selectedPurposes.isNotEmpty()) {
                                onBindingAdded(selectedIdentifierId!!, selectedPurposes)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Primary
                        ),
                        enabled = selectedIdentifierId != null && selectedPurposes.isNotEmpty()
                    ) {
                        Text(
                            text = "确认",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * 用途编辑对话框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurposeEditDialog(
    currentPurposes: Set<BindingPurpose>,
    onPurposesSelected: (Set<BindingPurpose>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPurposes by remember { mutableStateOf(currentPurposes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "编辑绑定用途",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PurposeSelectChip(
                        purpose = BindingPurpose.LOGIN,
                        label = "登录",
                        selected = selectedPurposes.contains(BindingPurpose.LOGIN),
                        onToggle = {
                            selectedPurposes = if (selectedPurposes.contains(BindingPurpose.LOGIN))
                                selectedPurposes - BindingPurpose.LOGIN
                            else
                                selectedPurposes + BindingPurpose.LOGIN
                        }
                    )
                    PurposeSelectChip(
                        purpose = BindingPurpose.VERIFICATION,
                        label = "验证",
                        selected = selectedPurposes.contains(BindingPurpose.VERIFICATION),
                        onToggle = {
                            selectedPurposes = if (selectedPurposes.contains(BindingPurpose.VERIFICATION))
                                selectedPurposes - BindingPurpose.VERIFICATION
                            else
                                selectedPurposes + BindingPurpose.VERIFICATION
                        }
                    )
                    PurposeSelectChip(
                        purpose = BindingPurpose.RECOVERY,
                        label = "找回",
                        selected = selectedPurposes.contains(BindingPurpose.RECOVERY),
                        onToggle = {
                            selectedPurposes = if (selectedPurposes.contains(BindingPurpose.RECOVERY))
                                selectedPurposes - BindingPurpose.RECOVERY
                            else
                                selectedPurposes + BindingPurpose.RECOVERY
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PurposeSelectChip(
                        purpose = BindingPurpose.NOTIFICATION,
                        label = "通知",
                        selected = selectedPurposes.contains(BindingPurpose.NOTIFICATION),
                        onToggle = {
                            selectedPurposes = if (selectedPurposes.contains(BindingPurpose.NOTIFICATION))
                                selectedPurposes - BindingPurpose.NOTIFICATION
                            else
                                selectedPurposes + BindingPurpose.NOTIFICATION
                        }
                    )
                    PurposeSelectChip(
                        purpose = BindingPurpose.SECONDARY_AUTH,
                        label = "二次验证",
                        selected = selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH),
                        onToggle = {
                            selectedPurposes = if (selectedPurposes.contains(BindingPurpose.SECONDARY_AUTH))
                                selectedPurposes - BindingPurpose.SECONDARY_AUTH
                            else
                                selectedPurposes + BindingPurpose.SECONDARY_AUTH
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Surface.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedPurposes.isNotEmpty()) {
                                onPurposesSelected(selectedPurposes)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HermesColors.Primary
                        ),
                        enabled = selectedPurposes.isNotEmpty()
                    ) {
                        Text(
                            text = "确认",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * 渠道分组标题
 */
@Composable
private fun ChannelGroupHeader(
    title: String,
    count: Int,
    statusColor: Color,
    isExpandable: Boolean = false,
    isExpanded: Boolean = true,
    onToggle: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isExpandable) Modifier.clickable { onToggle() }
                else Modifier
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title ($count)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = statusColor
        )
        if (isExpandable) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isExpanded) "折叠" else "展开",
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 可选渠道卡片
 */
@Composable
private fun SelectableChannelCard(
    identifier: IdentifierOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when (identifier.status) {
        IdentifierStatus.ACTIVE -> HermesColors.Surface
        IdentifierStatus.PENDING_DEACTIVATION -> Color(0xFFFFF3E0)
        IdentifierStatus.DEACTIVATED -> Color(0xFFF5F5F5)
        IdentifierStatus.INVALIDATED -> Color(0xFFF5F5F5)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (isSelected)
                    Modifier.border(1.dp, HermesColors.Primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (identifier.type == IdentifierType.EMAIL)
                Icons.Filled.Email
            else
                Icons.Filled.Phone,
            contentDescription = null,
            tint = when (identifier.status) {
                IdentifierStatus.ACTIVE -> HermesColors.Primary
                IdentifierStatus.PENDING_DEACTIVATION -> Color(0xFFFF9800)
                IdentifierStatus.DEACTIVATED -> HermesColors.TextMuted
                IdentifierStatus.INVALIDATED -> HermesColors.TextMuted
            },
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = identifier.value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = when (identifier.status) {
                IdentifierStatus.ACTIVE -> HermesColors.TextPrimary
                IdentifierStatus.PENDING_DEACTIVATION -> HermesColors.TextPrimary
                IdentifierStatus.DEACTIVATED -> HermesColors.TextMuted
                IdentifierStatus.INVALIDATED -> HermesColors.TextMuted
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "已选中",
                tint = HermesColors.Primary
            )
        }
    }
}

/**
 * 用途选择气泡
 */
@Composable
private fun PurposeSelectChip(
    purpose: BindingPurpose,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val color = when (purpose) {
        BindingPurpose.LOGIN -> Color(0xFF4CAF50)
        BindingPurpose.VERIFICATION -> HermesColors.Primary
        BindingPurpose.RECOVERY -> Color(0xFF2196F3)
        BindingPurpose.NOTIFICATION -> Color(0xFFFF9800)
        BindingPurpose.SECONDARY_AUTH -> Color(0xFF9C27B0)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) color.copy(alpha = 0.2f)
                else HermesColors.Surface.copy(alpha = 0.5f)
            )
            .then(
                if (selected)
                    Modifier.border(1.dp, color.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) color else HermesColors.TextSecondary
        )
    }
}

// Helper functions
private fun getStatusText(status: AccountStatus): String {
    return when (status) {
        AccountStatus.ACTIVE -> "正常使用"
        AccountStatus.FROZEN -> "已冻结"
        AccountStatus.LOST -> "已丢失"
        AccountStatus.ARCHIVED -> "已归档"
    }
}

private fun getStatusColor(status: AccountStatus): Color {
    return when (status) {
        AccountStatus.ACTIVE -> HermesColors.Success
        AccountStatus.FROZEN -> HermesColors.Info
        AccountStatus.LOST -> HermesColors.Warning
        AccountStatus.ARCHIVED -> HermesColors.TextMuted
    }
}

private fun getStatusDescription(status: AccountStatus): String {
    return when (status) {
        AccountStatus.ACTIVE -> "账号可正常使用"
        AccountStatus.FROZEN -> "账号暂时冻结，可恢复"
        AccountStatus.LOST -> "账号已丢失，无法恢复"
        AccountStatus.ARCHIVED -> "账号已归档保存"
    }
}

private fun getStatusIcon(status: AccountStatus): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        AccountStatus.ACTIVE -> Icons.Filled.CheckCircle
        AccountStatus.FROZEN -> Icons.Filled.AcUnit
        AccountStatus.LOST -> Icons.Filled.ReportProblem
        AccountStatus.ARCHIVED -> Icons.Filled.Inventory
    }
}

private fun getAppIconColor(appName: String): Color {
    return when {
        appName.contains("微信") -> Color(0xFF07c160)
        appName.contains("支付宝") -> Color(0xFF1677ff)
        appName.contains("微博") -> Color(0xFFe6162d)
        appName.contains("抖音") -> Color(0xFF000000)
        appName.contains("淘宝") -> Color(0xFFff4400)
        appName.contains("京东") -> Color(0xFFe53935)
        appName.contains("QQ") -> Color(0xFF12b7f5)
        appName.contains("银行") -> Color(0xFF1677ff)
        else -> HermesColors.Primary
    }
}