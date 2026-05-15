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
 * 多渠道绑定数据结构：渠道ID -> 用途集合
 */
typealias MultiChannelBindings = Map<Long, Set<BindingPurpose>>

/**
 * 添加账号页面（与原型一致）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    onBackClick: () -> Unit,
    onSaveClick: (Long, String, String?, String?, MultiChannelBindings) -> Unit,
    onAddIdentifierClick: () -> Unit = {},
    operationState: OperationState = OperationState.Idle,
    availableIdentifiers: List<IdentifierOption> = emptyList(),
    loadApplications: () -> Unit = {}, // 加载应用列表的回调
    availableApps: List<AppOption> = emptyList(), // 从数据库动态获取的应用列表
    modifier: Modifier = Modifier
) {
    // 进入页面时加载应用列表
    LaunchedEffect(Unit) {
        loadApplications()
    }

    var selectedAppId by remember { mutableStateOf<Long?>(null) }
    var selectedAppName by remember { mutableStateOf<String?>(null) }
    var accountName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    // 多渠道绑定数据：每个渠道ID对应其用途集合
    var channelBindings by remember { mutableStateOf<MultiChannelBindings>(emptyMap()) }
    // 当前正在编辑用途的渠道ID
    var editingChannelId by remember { mutableStateOf<Long?>(null) }
    var showBindingDialog by remember { mutableStateOf(false) }

    // 渠道按状态分组排序（需求规格第84-89行）
    // 组间顺序: ACTIVE > PENDING_DEACTIVATION > DEACTIVATED
    // 组内排序: newest first (createdAt降序)
    val activeIdentifiers = remember(availableIdentifiers) {
        availableIdentifiers
            .filter { it.status == IdentifierStatus.ACTIVE }
            .sortedByDescending { it.createdAt }
    }
    val pendingIdentifiers = remember(availableIdentifiers) {
        availableIdentifiers
            .filter { it.status == IdentifierStatus.PENDING_DEACTIVATION }
            .sortedByDescending { it.createdAt }
    }
    val deactivatedIdentifiers = remember(availableIdentifiers) {
        availableIdentifiers
            .filter { it.status == IdentifierStatus.DEACTIVATED || it.status == IdentifierStatus.INVALIDATED }
            .sortedByDescending { it.createdAt }
    }

    // 已失效分组默认折叠
    var deactivatedExpanded by remember { mutableStateOf(false) }

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
            // 选择应用标题（必填提示）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择应用",
                    fontSize = 12.sp,
                    color = HermesColors.TextSecondary
                )
                Text(
                    text = "（必填）",
                    fontSize = 12.sp,
                    color = HermesColors.Danger
                )
                if (selectedAppId == null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "请选择应用",
                        fontSize = 10.sp,
                        color = HermesColors.Warning
                    )
                }
            }

            // 应用图标横向滑动网格 - LazyRow（从数据库动态获取）
            val appRowState = rememberLazyListState()
            LazyRow(
                state = appRowState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableApps) { app ->
                    DynamicAppIconItem(
                        app = app,
                        selected = selectedAppName == app.name,
                        onClick = {
                            selectedAppId = app.id
                            selectedAppName = app.name
                        }
                    )
                }
            }

            // 账号ID输入框（必填提示）
            OutlinedTextField(
                value = accountName,
                onValueChange = { accountName = it },
                label = { Text("账号 ID（必填）", color = HermesColors.TextSecondary) },
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

            // 渠道列表（按状态分组，短横线分隔，已失效默认折叠）
            if (activeIdentifiers.isEmpty() && pendingIdentifiers.isEmpty() && deactivatedIdentifiers.isEmpty()) {
                // 空状态提示
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HermesColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "暂无可用验证渠道",
                            fontSize = 14.sp,
                            color = HermesColors.TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+ 添加渠道",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HermesColors.Primary,
                            modifier = Modifier.clickable { onAddIdentifierClick() }
                        )
                    }
                }
            } else {
                // 渠道卡片列表（分组显示）
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 正常使用分组
                    activeIdentifiers.forEach { identifier ->
                        ChannelCard(
                            identifier = identifier,
                            isSelected = channelBindings.containsKey(identifier.id),
                            selectedPurposes = channelBindings[identifier.id] ?: emptySet(),
                            onClick = {
                                if (channelBindings.containsKey(identifier.id)) {
                                    // 已选中渠道，弹出用途选择对话框
                                    editingChannelId = identifier.id
                                    showBindingDialog = true
                                } else {
                                    // 未选中渠道，添加选中并默认设置"验证"用途
                                    channelBindings = channelBindings + (identifier.id to setOf(BindingPurpose.VERIFICATION))
                                }
                            }
                        )
                    }

                    // 即将到期分组（如有）
                    if (pendingIdentifiers.isNotEmpty()) {
                        // 状态组之间用短横线分隔（无文字）
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = HermesColors.CardBorder,
                            thickness = 1.dp
                        )
                        pendingIdentifiers.forEach { identifier ->
                            ChannelCard(
                                identifier = identifier,
                                isSelected = channelBindings.containsKey(identifier.id),
                                selectedPurposes = channelBindings[identifier.id] ?: emptySet(),
                                onClick = {
                                    if (channelBindings.containsKey(identifier.id)) {
                                        // 已选中渠道，弹出用途选择对话框
                                        editingChannelId = identifier.id
                                        showBindingDialog = true
                                    } else {
                                        // 未选中渠道，添加选中并默认设置"验证"用途
                                        channelBindings = channelBindings + (identifier.id to setOf(BindingPurpose.VERIFICATION))
                                    }
                                }
                            )
                        }
                    }

                    // 已失效分组（默认折叠）
                    if (deactivatedIdentifiers.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = HermesColors.CardBorder,
                            thickness = 1.dp
                        )
                        if (deactivatedExpanded) {
                            // 展开显示已失效渠道
                            deactivatedIdentifiers.forEach { identifier ->
                                ChannelCard(
                                    identifier = identifier,
                                    isSelected = channelBindings.containsKey(identifier.id),
                                    selectedPurposes = channelBindings[identifier.id] ?: emptySet(),
                                    onClick = {
                                        if (channelBindings.containsKey(identifier.id)) {
                                            // 已选中渠道，弹出用途选择对话框
                                            editingChannelId = identifier.id
                                            showBindingDialog = true
                                        } else {
                                            // 未选中渠道，添加选中并默认设置"验证"用途
                                            channelBindings = channelBindings + (identifier.id to setOf(BindingPurpose.VERIFICATION))
                                        }
                                    }
                                )
                            }
                        } else {
                            // 折叠时显示"查看全部"按钮
                            Text(
                                text = "查看全部 (${deactivatedIdentifiers.size})",
                                fontSize = 12.sp,
                                color = HermesColors.TextMuted,
                                modifier = Modifier
                                    .clickable { deactivatedExpanded = true }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    // 添加渠道链接
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+ 添加渠道",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HermesColors.Primary,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onAddIdentifierClick() }
                            .padding(vertical = 8.dp)
                    )
                }
            }

            // 使用weight填充空白，确保按钮固定在底部
            Spacer(modifier = Modifier.weight(1f))

            // 保存按钮 - 原型样式（绑定验证渠道可选，不强制）
            Button(
                onClick = {
                    if (selectedAppId != null && accountName.isNotEmpty()) {
                        onSaveClick(
                            selectedAppId!!,
                            accountName,
                            if (nickname.isNotEmpty()) nickname else null,
                            null, // 备注暂不传递
                            channelBindings // 多渠道绑定数据：Map<Long, Set<BindingPurpose>>
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HermesColors.Primary
                ),
                // 绑定验证渠道可选，只需应用和账号名称即可保存
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
    if (showBindingDialog && editingChannelId != null) {
        // 获取当前编辑渠道的用途集合
        val currentPurposes = channelBindings[editingChannelId] ?: emptySet()
        var tempPurposes by remember { mutableStateOf(currentPurposes) }

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
                    // 显示渠道名称
                    val editingIdentifier = availableIdentifiers.find { it.id == editingChannelId }
                    Text(
                        text = "设置用途 - ${editingIdentifier?.value ?: "渠道"}",
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
                            selected = tempPurposes.contains(BindingPurpose.LOGIN),
                            onToggle = {
                                tempPurposes = if (tempPurposes.contains(BindingPurpose.LOGIN))
                                    tempPurposes - BindingPurpose.LOGIN
                                else
                                    tempPurposes + BindingPurpose.LOGIN
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.VERIFICATION,
                            label = "验证",
                            color = HermesColors.Primary, // 主题色
                            selected = tempPurposes.contains(BindingPurpose.VERIFICATION),
                            onToggle = {
                                tempPurposes = if (tempPurposes.contains(BindingPurpose.VERIFICATION))
                                    tempPurposes - BindingPurpose.VERIFICATION
                                else
                                    tempPurposes + BindingPurpose.VERIFICATION
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.RECOVERY,
                            label = "找回",
                            color = Color(0xFF2196F3), // 蓝色
                            selected = tempPurposes.contains(BindingPurpose.RECOVERY),
                            onToggle = {
                                tempPurposes = if (tempPurposes.contains(BindingPurpose.RECOVERY))
                                    tempPurposes - BindingPurpose.RECOVERY
                                else
                                    tempPurposes + BindingPurpose.RECOVERY
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
                            selected = tempPurposes.contains(BindingPurpose.NOTIFICATION),
                            onToggle = {
                                tempPurposes = if (tempPurposes.contains(BindingPurpose.NOTIFICATION))
                                    tempPurposes - BindingPurpose.NOTIFICATION
                                else
                                    tempPurposes + BindingPurpose.NOTIFICATION
                            }
                        )
                        PurposeChip(
                            purpose = BindingPurpose.SECONDARY_AUTH,
                            label = "二次验证",
                            color = Color(0xFF9C27B0), // 紫色
                            selected = tempPurposes.contains(BindingPurpose.SECONDARY_AUTH),
                            onToggle = {
                                tempPurposes = if (tempPurposes.contains(BindingPurpose.SECONDARY_AUTH))
                                    tempPurposes - BindingPurpose.SECONDARY_AUTH
                                else
                                    tempPurposes + BindingPurpose.SECONDARY_AUTH
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 取消选中按钮
                        Button(
                            onClick = {
                                // 移除该渠道的绑定
                                editingChannelId?.let { channelId ->
                                    channelBindings = channelBindings - channelId
                                }
                                showBindingDialog = false
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HermesColors.Danger.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "取消选中",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HermesColors.Danger
                            )
                        }
                        // 确认按钮
                        Button(
                            onClick = {
                                // 更新该渠道的用途
                                editingChannelId?.let { channelId ->
                                    if (tempPurposes.isNotEmpty()) {
                                        channelBindings = channelBindings + (channelId to tempPurposes)
                                    } else {
                                        // 如果用途为空，移除该渠道
                                        channelBindings = channelBindings - channelId
                                    }
                                }
                                showBindingDialog = false
                            },
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

/**
 * 动态应用图标组件（从数据库获取的应用）
 */
@Composable
private fun DynamicAppIconItem(
    app: AppOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val appColor = getAppColor(app.name)
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(appColor)
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

/**
 * 应用选项数据类（从数据库获取）
 */
data class AppOption(
    val id: Long,
    val name: String,
    val category: String? = null,
    val iconUrl: String? = null
)

/**
 * 获取应用颜色（根据名称匹配）
 */
private fun getAppColor(appName: String): Color = when {
    appName.contains("微信") -> Color(0xFF07c160)
    appName.contains("QQ") -> Color(0xFF12b7f5)
    appName.contains("微博") -> Color(0xFFe6162d)
    appName.contains("抖音") -> Color(0xFF000000)
    appName.contains("支付宝") -> Color(0xFF1677ff)
    appName.contains("淘宝") -> Color(0xFFff4400)
    appName.contains("京东") -> Color(0xFFe53935)
    appName.contains("GitHub") -> Color(0xFF333333)
    appName.contains("银行") -> Color(0xFF1677ff)
    else -> HermesColors.Primary
}