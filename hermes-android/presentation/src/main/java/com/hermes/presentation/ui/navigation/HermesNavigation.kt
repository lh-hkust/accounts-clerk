package com.hermes.presentation.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.presentation.ui.component.AccountStatusSelectionDialog
import com.hermes.presentation.ui.component.SwitchBindingDialog
import com.hermes.presentation.ui.screen.*
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.*
import com.hermes.presentation.ui.screen.AppOption
import kotlinx.coroutines.launch

/**
 * 导航状态管理
 * 用于处理导航栏点击当前页刷新/滚动到顶部
 */
sealed class NavigationEvent {
    object ScrollToTop : NavigationEvent()
    object Refresh : NavigationEvent()
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : NavigationEvent()
}

@Composable
fun HermesNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.IdentifierList.route,
        Screen.AccountList.route,
        Screen.Settings.route
    )

    val showBottomBar = currentRoute in bottomNavRoutes

    // Snackbar 状态
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 显示Snackbar的辅助函数
    val showSnackbar: (String, Boolean) -> Unit = { message, isError ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (isError) "error" else null,
                duration = if (isError) SnackbarDuration.Long else SnackbarDuration.Short,
                withDismissAction = isError
            )
        }
    }

    // 导航事件状态 - 用于处理导航栏点击当前页
    var navigationEvent by remember { mutableStateOf<NavigationEvent?>(null) }

    // 列表滚动状态 - 用于滚动到顶部
    val identifierListState = rememberLazyListState()
    val accountListState = rememberLazyListState()
    val dashboardListState = rememberLazyListState()

    // 处理导航事件
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.ScrollToTop -> {
                when (currentRoute) {
                    Screen.IdentifierList.route -> identifierListState.animateScrollToItem(0)
                    Screen.AccountList.route -> accountListState.animateScrollToItem(0)
                    Screen.Dashboard.route -> dashboardListState.animateScrollToItem(0)
                }
                navigationEvent = null
            }
            is NavigationEvent.Refresh -> {
                // Refresh handled by individual composable screens via NavController
                // Navigate to same route to trigger reload
                currentRoute?.let { route ->
                    navController.popBackStack()
                    navController.navigate(route)
                }
                navigationEvent = null
            }
            is NavigationEvent.ShowSnackbar -> {
                val event = navigationEvent as NavigationEvent.ShowSnackbar
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = if (event.isError) SnackbarDuration.Long else SnackbarDuration.Short,
                        withDismissAction = event.isError
                    )
                }
                navigationEvent = null
            }
            null -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                HermesBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    onCurrentPageClick = {
                        // 点击当前页时滚动到顶部
                        navigationEvent = NavigationEvent.ScrollToTop
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                Snackbar(
                    shape = MaterialTheme.shapes.small,
                    containerColor = if (data.visuals.actionLabel == "error")
                        HermesColors.Danger.copy(alpha = 0.9f)
                    else
                        HermesColors.Surface.copy(alpha = 0.9f),
                    contentColor = HermesColors.TextPrimary
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        containerColor = HermesColors.Background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            // 首页概览 - 使用ViewModel
            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

                DashboardScreen(
                    identifierStats = uiState.identifierStats,
                    accountStats = uiState.accountStats,
                    warnings = uiState.warnings,
                    unhandledWarningCount = uiState.unhandledWarningCount,
                    onWarningClick = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onHandleClick = { id -> dashboardViewModel.handleWarning(id) },
                    onViewAllWarnings = { navController.navigate(Screen.WarningList.route) },
                    onQuickHandle = { navController.navigate(Screen.WarningList.route) },
                    onIdentifierListClick = { navController.navigate(Screen.IdentifierList.route) },
                    onAccountListClick = { navController.navigate(Screen.AccountList.route) },
                    onImportClick = { navController.navigate(Screen.DataManagement.route) },
                    onExportClick = { navController.navigate(Screen.DataManagement.route) },
                    onAddIdentifierClick = { navController.navigate(Screen.AddIdentifier.route) }
                )
            }

            // 渠道列表 - 使用IdentifierViewModel
            composable(Screen.IdentifierList.route) {
                val viewModel: IdentifierViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()
                val deleteCheckState by viewModel.deleteCheckState.collectAsStateWithLifecycle()
                val gestureHintShown by viewModel.gestureHintShown.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                IdentifierListScreen(
                    uiState = uiState,
                    deleteCheckState = deleteCheckState,
                    gestureHintShown = gestureHintShown,
                    searchQuery = searchQuery,
                    onIdentifierClick = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddIdentifier.route) },
                    onEditIdentifier = { id -> navController.navigate(Screen.EditIdentifier.createRoute(id)) },
                    onSetReminder = { id -> navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onModifyReminder = { id -> navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onCheckDelete = { id -> viewModel.checkDeleteState(id) },
                    onConfirmDelete = { id -> viewModel.deleteIdentifier(id) },
                    onViewBoundAccounts = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onAccountClick = { accountId -> navController.navigate(Screen.AccountDetail.createRoute(accountId)) },
                    onResetDeleteCheckState = { viewModel.resetDeleteCheckState() },
                    onGestureHintDismissed = { viewModel.markGestureHintShown() },
                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                )

                // 处理操作结果
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 影响范围（渠道详情）- 使用IdentifierDetailViewModel
            composable(
                Screen.IdentifierDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: IdentifierDetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val deactivationDetail by viewModel.deactivationDetail.collectAsStateWithLifecycle()
                val deleteCheckState by viewModel.deleteCheckState.collectAsStateWithLifecycle()

                // 加载详情
                LaunchedEffect(id) {
                    viewModel.loadIdentifierDetail(id)
                }

                IdentifierDetailScreen(
                    uiState = uiState,
                    deactivationDetail = deactivationDetail,
                    deleteCheckState = deleteCheckState,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = { viewModel.checkDeleteState(id) },
                    onCheckDelete = { viewModel.checkDeleteState(it) },
                    onConfirmDelete = { viewModel.deleteIdentifier(id) },
                    onAccountClick = { accountId -> navController.navigate(Screen.AccountDetail.createRoute(accountId)) },
                    onCancelDeactivation = { viewModel.cancelDeactivation(id) },
                    onModifyDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onScheduleDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onBatchChange = { /* TODO: 批量更换 */ },
                    onMarkHandled = { viewModel.handleWarning(id) },
                    onViewBoundAccounts = { navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    canDelete = false
                )
            }

            // 添加验证渠道 - 共享IdentifierList页面的ViewModel实例
            composable(Screen.AddIdentifier.route) { navBackStackEntry ->
                // 获取IdentifierList页面的ViewModel实例，确保添加后列表能自动刷新
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Screen.IdentifierList.route)
                }
                val viewModel: IdentifierViewModel = hiltViewModel(parentEntry)
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                AddIdentifierScreen(
                    operationState = operationState,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { type, value ->
                        viewModel.addIdentifier(type, value)
                        // 导航在LaunchedEffect中处理
                    },
                    onCheckDuplicate = { type, value, callback ->
                        viewModel.checkDuplicate(type, value, callback)
                    }
                )

                // 成功后返回并显示Snackbar
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("保存成功", false)
                            navController.popBackStack()
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 账号列表 - 使用AccountViewModel
            composable(Screen.AccountList.route) {
                val viewModel: AccountViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                // 变更状态对话框状态
                var showStatusDialog by remember { mutableStateOf(false) }
                var selectedAccountId by remember { mutableStateOf<Long?>(null) }
                var selectedAccountStatus by remember { mutableStateOf<AccountStatus?>(null) }

                AccountListScreen(
                    uiState = uiState,
                    onAccountClick = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddAccount.route) },
                    onRefresh = { viewModel.loadAccounts() },
                    onChangeStatus = { accountId ->
                        // 获取当前账号状态并显示对话框
                        if (uiState is AccountListState.Success) {
                            val account = (uiState as AccountListState.Success).items.find { it.account.id == accountId }
                            if (account != null) {
                                selectedAccountId = accountId
                                selectedAccountStatus = account.account.status
                                showStatusDialog = true
                            }
                        }
                    }
                )

                // 变更账号状态对话框
                if (showStatusDialog && selectedAccountStatus != null && selectedAccountId != null) {
                    AccountStatusSelectionDialog(
                        currentStatus = selectedAccountStatus!!,
                        onStatusSelected = { newStatus ->
                            viewModel.updateStatus(selectedAccountId!!, newStatus)
                            showStatusDialog = false
                            selectedAccountId = null
                            selectedAccountStatus = null
                        },
                        onDismiss = {
                            showStatusDialog = false
                            selectedAccountId = null
                            selectedAccountStatus = null
                        }
                    )
                }

                // 操作结果反馈
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("状态已更新", false)
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 账号详情 - 使用AccountDetailViewModel
            composable(
                Screen.AccountDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: AccountDetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()
                val availableIdentifiers by viewModel.availableIdentifiersForSwitch.collectAsStateWithLifecycle()
                val currentBinding by viewModel.currentBindingForSwitch.collectAsStateWithLifecycle()
                val switchBindingState by viewModel.switchBindingOperationState.collectAsStateWithLifecycle()
                val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

                // 对话框状态
                var showStatusDialog by remember { mutableStateOf(false) }
                var showSwitchBindingDialog by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }
                var selectedBindingId by remember { mutableStateOf<Long?>(null) }

                LaunchedEffect(id) {
                    viewModel.loadAccountDetail(id)
                }

                AccountDetailScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { navController.navigate(Screen.EditAccount.createRoute(id)) },
                    onChangeChannelClick = { bindingId ->
                        // 设置当前绑定ID并准备更换
                        selectedBindingId = bindingId
                        viewModel.prepareSwitchBinding(id, bindingId)
                        showSwitchBindingDialog = true
                    },
                    onChangeStatusClick = { showStatusDialog = true },
                    onDeleteClick = { showDeleteDialog = true },
                    onRelatedAccountClick = { relatedId -> navController.navigate(Screen.AccountDetail.createRoute(relatedId)) }
                )

                // 变更账号状态对话框
                if (showStatusDialog && uiState is AccountDetailState.Success) {
                    val currentStatus = (uiState as AccountDetailState.Success).detail.account.status
                    AccountStatusSelectionDialog(
                        currentStatus = currentStatus,
                        onStatusSelected = { newStatus ->
                            viewModel.updateStatus(id, newStatus)
                            showStatusDialog = false
                        },
                        onDismiss = { showStatusDialog = false }
                    )
                }

                // 更换验证渠道对话框
                if (showSwitchBindingDialog && currentBinding != null && switchBindingState is SwitchBindingOperationState.Prepared) {
                    SwitchBindingDialog(
                        currentBinding = currentBinding!!,
                        availableIdentifiers = availableIdentifiers,
                        onConfirm = { newIdentifierId, newPurposes ->
                            viewModel.switchBinding(
                                accountId = id,
                                oldIdentifierId = currentBinding!!.identifierId,
                                newIdentifierId = newIdentifierId,
                                newPurposes = newPurposes
                            )
                            showSwitchBindingDialog = false
                        },
                        onDismiss = {
                            showSwitchBindingDialog = false
                            viewModel.resetSwitchBindingState()
                        }
                    )
                }

                // 删除账号确认对话框
                if (showDeleteDialog && uiState is AccountDetailState.Success) {
                    val detail = (uiState as AccountDetailState.Success).detail
                    com.hermes.presentation.ui.component.DeleteAccountConfirmDialog(
                        accountName = detail.account.accountName,
                        applicationName = detail.applicationName,
                        nickname = detail.account.nickname,
                        status = detail.account.status,
                        onDismiss = {
                            showDeleteDialog = false
                            viewModel.resetDeleteState()
                        },
                        onConfirm = {
                            viewModel.deleteAccount(id)
                            showDeleteDialog = false
                        }
                    )
                }

                // 操作结果反馈
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("状态已更新", false)
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }

                // 更换绑定结果反馈
                LaunchedEffect(switchBindingState) {
                    when (switchBindingState) {
                        is SwitchBindingOperationState.Success -> {
                            showSnackbar("验证渠道已更换", false)
                            viewModel.resetSwitchBindingState()
                        }
                        is SwitchBindingOperationState.Error -> {
                            showSnackbar((switchBindingState as SwitchBindingOperationState.Error).message, true)
                            viewModel.resetSwitchBindingState()
                        }
                        else -> {}
                    }
                }

                // 删除结果反馈
                LaunchedEffect(deleteState) {
                    when (deleteState) {
                        is DeleteState.Success -> {
                            val success = deleteState as DeleteState.Success
                            showSnackbar("账号已删除，已解绑${success.unboundCount}个绑定关系", false)
                            navController.popBackStack()
                            viewModel.resetDeleteState()
                        }
                        is DeleteState.Error -> {
                            showSnackbar((deleteState as DeleteState.Error).message, true)
                            viewModel.resetDeleteState()
                        }
                        else -> {}
                    }
                }
            }

            // 添加账号 - 使用AccountViewModel
            composable(Screen.AddAccount.route) { navBackStackEntry ->
                val viewModel: AccountViewModel = hiltViewModel()
                // 共享IdentifierList页面的ViewModel实例，确保添加渠道后返回能自动刷新
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(Screen.IdentifierList.route)
                }
                val identifierViewModel: IdentifierViewModel = hiltViewModel(parentEntry)
                val identifierState by identifierViewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()
                val applicationListState by viewModel.applicationListState.collectAsStateWithLifecycle()

                // 使用DisposableEffect实现生命周期感知的刷新
                // 每次页面恢复时刷新渠道列表（解决从AddIdentifier返回后列表不更新问题）
                DisposableEffect(Unit) {
                    val lifecycleObserver = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            identifierViewModel.loadIdentifiers()
                        }
                    }
                    navBackStackEntry.lifecycle.addObserver(lifecycleObserver)
                    onDispose {
                        navBackStackEntry.lifecycle.removeObserver(lifecycleObserver)
                    }
                }

                // 转换应用列表为AppOption
                val appOptions = when (applicationListState) {
                    is ApplicationListState.Success -> {
                        (applicationListState as ApplicationListState.Success).items.map { app ->
                            AppOption(
                                id = app.id ?: 0L,
                                name = app.name,
                                category = app.category,
                                iconUrl = app.iconUrl
                            )
                        }
                    }
                    else -> emptyList()
                }

                AddAccountScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { applicationId, accountName, nickname, remark, channelBindings ->
                        viewModel.addAccount(
                            applicationId,
                            accountName,
                            nickname,
                            remark,
                            channelBindings // 多渠道绑定数据：Map<Long, Set<BindingPurpose>>
                        )
                    },
                    onAddIdentifierClick = { navController.navigate(Screen.AddIdentifier.route) },
                    availableIdentifiers = when (identifierState) {
                        is IdentifierListState.Success -> (identifierState as IdentifierListState.Success).items.map { item ->
                            com.hermes.presentation.ui.component.IdentifierOption(
                                id = item.identifier.id ?: 0L,
                                type = item.identifier.type,
                                value = item.identifier.value,
                                status = item.identifier.status,
                                createdAt = item.identifier.createdAt
                            )
                        }
                        else -> emptyList()
                    },
                    loadApplications = { viewModel.loadApplications() },
                    availableApps = appOptions,
                    operationState = operationState  // 传递operationState以显示加载状态
                )

                // 成功后返回并显示Snackbar
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("保存成功", false)
                            navController.popBackStack()
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 提醒列表 - 使用WarningViewModel
            composable(Screen.WarningList.route) {
                val viewModel: WarningViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val handledWarnings by viewModel.handledWarnings.collectAsStateWithLifecycle()

                WarningListScreen(
                    uiState = uiState,
                    handledWarnings = handledWarnings,
                    onWarningClick = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onHandleClick = { id -> viewModel.handleWarning(id) },
                    onBackClick = { navController.popBackStack() },
                    onRefresh = { viewModel.loadWarnings() }
                )
            }

            // 提醒详情 - 导航到渠道详情页
            composable(
                Screen.WarningDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                LaunchedEffect(id) {
                    navController.navigate(Screen.IdentifierDetail.createRoute(id)) {
                        popUpTo(Screen.WarningList.route) { inclusive = true }
                    }
                }
            }

            // 设置页面
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { },
                    onDataManageClick = { navController.navigate(Screen.DataManagement.route) },
                    onNotificationClick = { navController.navigate(Screen.NotificationSettings.route) },
                    onPrivacyClick = { navController.navigate(Screen.PrivacySecurity.route) },
                    onAboutClick = { navController.navigate(Screen.About.route) }
                )
            }

            // 数据管理页面 - 使用ExportImportViewModel
            composable(Screen.DataManagement.route) {
                DataManagementScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 通知设置页面
            composable(Screen.NotificationSettings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val notificationSettings by viewModel.notificationSettings.collectAsStateWithLifecycle()

                NotificationSettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    settings = notificationSettings,
                    onToggle = { key, enabled -> viewModel.updateNotificationSetting(key, enabled) }
                )
            }

            // 隐私安全页面
            composable(Screen.PrivacySecurity.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val securitySettings by viewModel.securitySettings.collectAsStateWithLifecycle()

                PrivacySecurityScreen(
                    onBackClick = { navController.popBackStack() },
                    settings = securitySettings,
                    onSetPasswordClick = { navController.navigate(Screen.SetPassword.route) },
                    onToggleBiometric = { enabled -> viewModel.updateSecuritySetting("biometric", enabled) }
                )
            }

            // 设置密码页面
            composable(Screen.SetPassword.route) {
                val viewModel: SettingsViewModel = hiltViewModel()

                SetPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onSavePassword = { password -> viewModel.setPassword(password) }
                )
            }

            // 关于页面
            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 设置到期提醒 - 使用DeactivationViewModel
            composable(
                Screen.ScheduleDeactivation.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: DeactivationViewModel = hiltViewModel()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()
                val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

                ScheduleDeactivationScreen(
                    identifierId = id,
                    onBackClick = { navController.popBackStack() },
                    onScheduled = {
                        viewModel.scheduleDeactivation(id)
                    },
                    initialDate = selectedDate,
                    onDateSelected = { date, type, reason ->
                        viewModel.setSelectedDate(date)
                        viewModel.setDeactivationType(type)
                    }
                )

                // 成功后返回并显示Snackbar
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("提醒已设置", false)
                            navController.popBackStack()
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 关联账号（影响分析）
            composable(
                Screen.ImpactAnalysis.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: ImpactAnalysisViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(id) {
                    viewModel.analyzeImpact(id)
                }

                ImpactAnalysisScreen(
                    identifierId = id,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 编辑渠道 - 使用EditIdentifierViewModel
            composable(
                Screen.EditIdentifier.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: EditIdentifierViewModel = hiltViewModel()
                val identifier by viewModel.identifier.collectAsStateWithLifecycle()
                val deactivationDetail by viewModel.deactivationDetail.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                LaunchedEffect(id) {
                    viewModel.loadIdentifier(id)
                }

                if (identifier != null) {
                    EditIdentifierScreen(
                        identifierType = identifier!!.type,
                        identifierValue = identifier!!.value,
                        identifierStatus = identifier!!.status,
                        plannedDeactTime = identifier!!.plannedDeactTime,
                        deactReason = identifier!!.deactReason,
                        currentRemark = identifier!!.remark,
                        deactivationDetail = deactivationDetail,
                        operationState = operationState,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { remark -> viewModel.updateRemark(remark) },
                        onCancelDeactivation = { viewModel.cancelDeactivation() },
                        onModifyDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) }
                    )
                }

                // 成功后返回并显示Snackbar
                LaunchedEffect(operationState) {
                    when (operationState) {
                        is OperationState.Success -> {
                            showSnackbar("已更新", false)
                            navController.popBackStack()
                            viewModel.resetOperationState()
                        }
                        is OperationState.Error -> {
                            showSnackbar((operationState as OperationState.Error).message, true)
                            viewModel.resetOperationState()
                        }
                        else -> {}
                    }
                }
            }

            // 编辑账号 - 使用AccountDetailViewModel
            composable(
                Screen.EditAccount.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: AccountDetailViewModel = hiltViewModel()
                val identifierViewModel: IdentifierViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()
                val identifierState by identifierViewModel.uiState.collectAsStateWithLifecycle()

                // 加载账号详情和可用渠道
                LaunchedEffect(id) {
                    viewModel.loadAccountDetail(id)
                    identifierViewModel.loadIdentifiers()
                }

                // 等待加载完成后再显示编辑页面
                if (uiState is AccountDetailState.Success) {
                    val detail = (uiState as AccountDetailState.Success).detail

                    val availableIdentifiers = when (identifierState) {
                        is IdentifierListState.Success -> (identifierState as IdentifierListState.Success).items.map { item ->
                            com.hermes.presentation.ui.component.IdentifierOption(
                                id = item.identifier.id ?: 0L,
                                type = item.identifier.type,
                                value = item.identifier.value,
                                status = item.identifier.status,
                                createdAt = item.identifier.createdAt
                            )
                        }
                        else -> emptyList()
                    }

                    // 转换绑定信息
                    val initialBindings = detail.boundIdentifiers.map { binding ->
                        com.hermes.presentation.ui.screen.BindingInfo(
                            identifierId = binding.identifierId,
                            identifierValue = binding.identifierValue,
                            identifierType = binding.identifierType,
                            purposes = binding.purposes.toSet()
                        )
                    }

                    EditAccountScreen(
                        accountId = id,
                        applicationId = detail.account.applicationId,
                        applicationName = detail.applicationName,
                        applicationCategory = detail.applicationCategory,
                        initialAccountName = detail.account.accountName,
                        initialAccountIdentifier = detail.account.accountIdentifier,
                        initialNickname = detail.account.nickname,
                        initialStatus = detail.account.status,
                        initialBindings = initialBindings,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { accountName, accountIdentifier, nickname, status, bindings ->
                            // 转换绑定信息为BindingUpdate
                            val bindingUpdates = bindings.map { binding ->
                                com.hermes.presentation.usecase.account.BindingUpdate(
                                    identifierId = binding.identifierId,
                                    purposes = binding.purposes.toSet()
                                )
                            }
                            viewModel.updateAccount(
                                accountId = id,
                                applicationId = detail.account.applicationId,
                                accountName = accountName,
                                accountIdentifier = accountIdentifier,
                                nickname = nickname,
                                status = status,
                                bindings = bindingUpdates
                            )
                        },
                        onCheckDuplicate = { applicationId, accountIdentifier, excludeAccountId, callback ->
                            viewModel.checkDuplicate(applicationId, accountIdentifier, excludeAccountId, callback)
                        },
                        availableIdentifiers = availableIdentifiers,
                        operationState = operationState
                    )

                    // 成功后返回并显示Snackbar
                    LaunchedEffect(operationState) {
                        when (operationState) {
                            is OperationState.Success -> {
                                showSnackbar("账号已更新", false)
                                navController.popBackStack()
                                viewModel.resetOperationState()
                            }
                            is OperationState.Error -> {
                                showSnackbar((operationState as OperationState.Error).message, true)
                                viewModel.resetOperationState()
                            }
                            else -> {}
                        }
                    }
                } else if (uiState is AccountDetailState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HermesColors.Primary)
                    }
                } else if (uiState is AccountDetailState.NotFound) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("账号不存在", color = HermesColors.TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun HermesBottomBar(
    navController: NavHostController,
    currentRoute: String?,
    onCurrentPageClick: () -> Unit
) {
    val navIcons = listOf(
        Triple(Screen.Dashboard, Icons.Filled.Home, "首页"),
        Triple(Screen.IdentifierList, Icons.Filled.Security, "渠道"),
        Triple(Screen.AccountList, Icons.Filled.Inventory2, "账号"),
        Triple(Screen.Settings, Icons.Filled.Settings, "设置")
    )

    NavigationBar(
        containerColor = HermesColors.Surface.copy(alpha = 0.9f),
        contentColor = HermesColors.TextPrimary,
        tonalElevation = 8.dp
    ) {
        navIcons.forEach { (screen, icon, label) ->
            val selected = currentRoute == screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // 如果点击当前页，执行滚动到顶部
                    if (selected) {
                        onCurrentPageClick()
                    } else {
                        // 导航到目标页面，不使用popUpTo以避免导航栈混乱
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp),
                        tint = if (selected) HermesColors.Primary else HermesColors.TextSecondary.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) HermesColors.Primary else HermesColors.TextSecondary.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}