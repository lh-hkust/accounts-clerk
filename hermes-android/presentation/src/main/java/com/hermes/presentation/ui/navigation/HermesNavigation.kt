package com.hermes.presentation.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.hermes.presentation.ui.screen.*
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.viewmodel.*

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

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                HermesBottomBar(navController)
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
                    onExportClick = { navController.navigate(Screen.DataManagement.route) }
                )
            }

            // 渠道列表 - 使用IdentifierViewModel
            composable(Screen.IdentifierList.route) {
                val viewModel: IdentifierViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                IdentifierListScreen(
                    uiState = uiState,
                    onIdentifierClick = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddIdentifier.route) }
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

                // 加载详情
                LaunchedEffect(id) {
                    viewModel.loadIdentifierDetail(id)
                }

                IdentifierDetailScreen(
                    uiState = uiState,
                    deactivationDetail = deactivationDetail,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = { viewModel.deleteIdentifier(id) },
                    onAccountClick = { accountId -> navController.navigate(Screen.AccountDetail.createRoute(accountId)) },
                    onCancelDeactivation = { viewModel.cancelDeactivation(id) },
                    onModifyDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onScheduleDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    onBatchChange = { /* TODO: 批量更换 */ },
                    onMarkHandled = { viewModel.handleWarning(id) },
                    canDelete = false
                )
            }

            // 添加验证渠道 - 使用IdentifierViewModel
            composable(Screen.AddIdentifier.route) {
                val viewModel: IdentifierViewModel = hiltViewModel()
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

                // 成功后返回
                LaunchedEffect(operationState) {
                    if (operationState is OperationState.Success) {
                        navController.popBackStack()
                        viewModel.resetOperationState()
                    }
                }
            }

            // 账号列表 - 使用AccountViewModel
            composable(Screen.AccountList.route) {
                val viewModel: AccountViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                AccountListScreen(
                    uiState = uiState,
                    onAccountClick = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddAccount.route) },
                    onRefresh = { viewModel.loadAccounts() }
                )
            }

            // 账号详情 - 使用AccountDetailViewModel
            composable(
                Screen.AccountDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val viewModel: AccountDetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(id) {
                    viewModel.loadAccountDetail(id)
                }

                AccountDetailScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { /* TODO */ },
                    onChangeChannelClick = { /* TODO */ },
                    onChangeStatusClick = { /* TODO */ },
                    onDeleteClick = { /* TODO */ },
                    onRelatedAccountClick = { relatedId -> navController.navigate(Screen.AccountDetail.createRoute(relatedId)) }
                )
            }

            // 添加账号 - 使用AccountViewModel
            composable(Screen.AddAccount.route) {
                val viewModel: AccountViewModel = hiltViewModel()
                val identifierViewModel: IdentifierViewModel = hiltViewModel()
                val identifierState by identifierViewModel.uiState.collectAsStateWithLifecycle()
                val operationState by viewModel.operationState.collectAsStateWithLifecycle()

                // 加载可用渠道列表
                LaunchedEffect(Unit) {
                    identifierViewModel.loadIdentifiers()
                }

                AddAccountScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { applicationId, accountName, identifier, nickname ->
                        viewModel.addAccount(applicationId, accountName, identifier, nickname)
                    },
                    availableIdentifiers = when (identifierState) {
                        is IdentifierListState.Success -> (identifierState as IdentifierListState.Success).items.map { item ->
                            com.hermes.presentation.ui.component.IdentifierOption(
                                id = item.identifier.id ?: 0L,
                                type = item.identifier.type,
                                value = item.identifier.value,
                                status = item.identifier.status
                            )
                        }
                        else -> emptyList()
                    }
                )

                // 成功后返回
                LaunchedEffect(operationState) {
                    if (operationState is OperationState.Success) {
                        navController.popBackStack()
                        viewModel.resetOperationState()
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

                ScheduleDeactivationScreen(
                    identifierId = id,
                    onBackClick = { navController.popBackStack() },
                    onScheduled = {
                        viewModel.scheduleDeactivation(id)
                        // 导航在LaunchedEffect中处理
                    }
                )

                // 成功后返回
                LaunchedEffect(operationState) {
                    if (operationState is OperationState.Success) {
                        navController.popBackStack()
                        viewModel.resetOperationState()
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
        }
    }
}

@Composable
fun HermesBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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