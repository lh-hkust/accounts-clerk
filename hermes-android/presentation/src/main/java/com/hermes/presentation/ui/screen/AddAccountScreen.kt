package com.hermes.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.presentation.ui.component.IdentifierOption
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.OperationState

/**
 * 添加账号页面（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    onBackClick: () -> Unit,
    onSaveClick: (Long, String, String?, String?) -> Unit,
    operationState: OperationState = OperationState.Idle,
    availableIdentifiers: List<IdentifierOption> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedAppId by remember { mutableStateOf<Long?>(null) }
    var selectedAppName by remember { mutableStateOf<String?>(null) }
    var accountName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var selectedIdentifierId by remember { mutableStateOf<Long?>(null) }
    var selectedPurposes by remember { mutableStateOf(setOf<BindingPurpose>()) }
    var showBindingDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var deactivatedExpanded by remember { mutableStateOf(false) } // 已失效分组默认折叠

    // 预置应用列表 - 原型样式
    val apps = listOf(
        AppItem(1L, "微信", Color(0xFF07c160)),
        AppItem(2L, "QQ", Color(0xFF12b7f5)),
        AppItem(3L, "微博", Color(0xFFe6162d)),
        AppItem(4L, "抖音", Color(0xFF000000)),
        AppItem(5L, "支付宝", Color(0xFF1677ff)),
        AppItem(6L, "淘宝", Color(0xFFff4400)),
        AppItem(7L, "京东", Color(0xFFe53935)),
        AppItem(8L, "GitHub", Color(0xFF333333))
    )

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

    // 搜索过滤后的渠道
    val filteredIdentifiers = remember(sortedIdentifiers, searchQuery) {
        if (searchQuery.isEmpty()) {
            sortedIdentifiers
        } else {
            sortedIdentifiers.filter { it.value.contains(searchQuery, ignoreCase = true) }
        }
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
                title = { Text("添加账号", fontWeight = FontWeight.Bold) },
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
            // 选择应用标题
            Text(
                text = "选择应用",
                fontSize = 12.sp,
                color = HermesColors.TextSecondary
            )

            // 应用图标横向滑动网格 - LazyRow
            val appRowState = rememberLazyListState()
            LazyRow(
                state = appRowState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apps) { app ->
                    AppIconItem(
                        app = app,
                        selected = selectedAppName == app.name,
                        onClick = {
                            selectedAppId = app.id
                            selectedAppName = app.name
                        }
                    )
                }
            }

            // 账号ID输入框
            OutlinedTextField(
                value = accountName,
                onValueChange = { accountName = it },
                placeholder = { Text("账号 ID / 用户名", color = HermesColors.TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.CardBorder
                )
            )

            // 昵称输入框
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = { Text("昵称（可选，用于列表显示）", color = HermesColors.TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HermesColors.Primary,
                    unfocusedBorderColor = HermesColors.CardBorder
                )
            )

            // 绑定验证渠道标题
            Text(
                text = "绑定验证渠道",
                fontSize = 12.sp,
                color = HermesColors.TextSecondary
            )

            // 渠道搜索框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
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

            // 分组渠道列表
            if (filteredIdentifiers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "暂无可用验证渠道，请先添加验证渠道" else "未找到匹配的渠道",
                        fontSize = 14.sp,
                        color = HermesColors.TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                            ChannelCard(
                                identifier = identifier,
                                isSelected = selectedIdentifierId == identifier.id,
                                selectedPurposes = if (selectedIdentifierId == identifier.id) selectedPurposes else emptySet(),
                                onClick = {
                                    if (selectedIdentifierId == identifier.id) {
                                        // 再次点击弹出用途选择对话框
                                        showBindingDialog = true
                                    } else {
                                        // 第一次点击选中
                                        selectedIdentifierId = identifier.id
                                        selectedPurposes = setOf(BindingPurpose.VERIFICATION) // 默认选中验证
                                    }
                                }
                            )
                        }
                    }

                    // 即将到期分组
                    if (pendingIdentifiers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = HermesColors.CardBorder,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ChannelGroupHeader(
                                title = "即将到期",
                                count = pendingIdentifiers.size,
                                statusColor = Color(0xFFFF9800) // 橙色
                            )
                        }
                        items(pendingIdentifiers) { identifier ->
                            ChannelCard(
                                identifier = identifier,
                                isSelected = selectedIdentifierId == identifier.id,
                                selectedPurposes = if (selectedIdentifierId == identifier.id) selectedPurposes else emptySet(),
                                onClick = {
                                    if (selectedIdentifierId == identifier.id) {
                                        showBindingDialog = true
                                    } else {
                                        selectedIdentifierId = identifier.id
                                        selectedPurposes = setOf(BindingPurpose.VERIFICATION)
                                    }
                                }
                            )
                        }
                    }

                    // 已失效分组（默认折叠）
                    if (deactivatedIdentifiers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = HermesColors.CardBorder,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ChannelGroupHeader(
                                title = "已失效",
                                count = deactivatedIdentifiers.size,
                                statusColor = HermesColors.TextMuted,
                                isExpandable = true,
                                isExpanded = deactivatedExpanded,
                                onToggle = { deactivatedExpanded = !deactivatedExpanded }
                            )
                        }
                        // 根据折叠状态显示/隐藏已失效渠道
                        if (deactivatedExpanded) {
                            items(deactivatedIdentifiers) { identifier ->
                                ChannelCard(
                                    identifier = identifier,
                                    isSelected = selectedIdentifierId == identifier.id,
                                    selectedPurposes = if (selectedIdentifierId == identifier.id) selectedPurposes else emptySet(),
                                    onClick = {
                                        if (selectedIdentifierId == identifier.id) {
                                            showBindingDialog = true
                                        } else {
                                            selectedIdentifierId = identifier.id
                                            selectedPurposes = setOf(BindingPurpose.VERIFICATION)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮 - 原型样式
            Button(
                onClick = {
                    if (selectedAppId != null && accountName.isNotEmpty()) {
                        onSaveClick(
                            selectedAppId!!,
                            accountName,
                            if (nickname.isNotEmpty()) nickname else null,
                            null // 备注暂不传递
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                ),
                enabled = !isLoading && selectedAppId != null && accountName.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = HermesColors.TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "保存账号",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // 绑定用途选择弹窗（点击已选渠道时显示）
    if (showBindingDialog && selectedIdentifierId != null) {
        AlertDialog(
            onDismissRequest = { showBindingDialog = false },
            modifier = Modifier
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "选择绑定用途",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 用途选择气泡样式 - 不同底色，选中加边框
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PurposeChip(
                            purpose = BindingPurpose.LOGIN,
                            label = "登录",
                            color = Color(0xFF4CAF50), // 绿色
                            selected = selectedPurposes.contains(BindingPurpose.LOGIN),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.LOGIN))
                                    selectedPurposes - BindingPurpose.LOGIN
                                else
                                    selectedPurposes + BindingPurpose.LOGIN
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.VERIFICATION,
                            label = "验证",
                            color = HermesColors.Primary, // 主题色
                            selected = selectedPurposes.contains(BindingPurpose.VERIFICATION),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.VERIFICATION))
                                    selectedPurposes - BindingPurpose.VERIFICATION
                                else
                                    selectedPurposes + BindingPurpose.VERIFICATION
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.RECOVERY,
                            label = "找回",
                            color = Color(0xFF2196F3), // 蓝色
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
                        PurposeChip(
                            purpose = BindingPurpose.NOTIFICATION,
                            label = "通知",
                            color = Color(0xFFFF9800), // 橙色
                            selected = selectedPurposes.contains(BindingPurpose.NOTIFICATION),
                            onToggle = {
                                selectedPurposes = if (selectedPurposes.contains(BindingPurpose.NOTIFICATION))
                                    selectedPurposes - BindingPurpose.NOTIFICATION
                                else
                                    selectedPurposes + BindingPurpose.NOTIFICATION
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.SECONDARY_AUTH,
                            label = "二次验证",
                            color = Color(0xFF9C27B0), // 紫色
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
                            onClick = { showBindingDialog = false },
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
                            onClick = { showBindingDialog = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HermesColors.Primary
                            )
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
 * 渠道卡片 - 简约设计
 */
@Composable
private fun ChannelCard(
    identifier: IdentifierOption,
    isSelected: Boolean,
    selectedPurposes: Set<BindingPurpose>,
    onClick: () -> Unit
) {
    // 底色区分状态
    val backgroundColor = when (identifier.status) {
        IdentifierStatus.ACTIVE -> HermesColors.Surface
        IdentifierStatus.PENDING_DEACTIVATION -> Color(0xFFFFF3E0) // 浅橙色背景
        IdentifierStatus.DEACTIVATED -> Color(0xFFF5F5F5) // 灰色背景
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
        // 图标区分类型
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

        // 已选中渠道右侧显示用途色点（小圆圈，无文字）
        if (isSelected && selectedPurposes.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                selectedPurposes.forEach { purpose ->
                    PurposeDot(purpose = purpose)
                }
            }
        }
    }
}

/**
 * 用途色点（小圆圈，无文字）
 */
@Composable
private fun PurposeDot(purpose: BindingPurpose) {
    val color = when (purpose) {
        BindingPurpose.LOGIN -> Color(0xFF4CAF50) // 绿色
        BindingPurpose.VERIFICATION -> HermesColors.Primary // 主题色
        BindingPurpose.RECOVERY -> Color(0xFF2196F3) // 蓝色
        BindingPurpose.NOTIFICATION -> Color(0xFFFF9800) // 橙色
        BindingPurpose.SECONDARY_AUTH -> Color(0xFF9C27B0) // 紫色
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun AppIconItem(
    app: AppItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(app.color)
            .then(
                if (selected)
                    Modifier.border(2.dp, HermesColors.Primary, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = app.name.take(1),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HermesColors.TextPrimary
        )
    }
}

/**
 * 用途选择气泡 - 不同底色，选中加边框
 */
@Composable
private fun PurposeChip(
    purpose: BindingPurpose,
    label: String,
    color: Color,
    selected: Boolean,
    onToggle: () -> Unit
) {
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

private data class AppItem(
    val id: Long,
    val name: String,
    val color: Color
)