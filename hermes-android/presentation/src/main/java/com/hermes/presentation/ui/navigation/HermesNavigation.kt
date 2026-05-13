package com.hermes.presentation.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.hermes.presentation.ui.theme.HermesColors
import com.hermes.presentation.ui.screen.*

@Composable
fun HermesNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            HermesBottomBar(navController)
        },
        containerColor = HermesColors.Background
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            // 首页概览
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    identifierStats = emptyMap(),
                    accountStats = 0,
                    warnings = emptyList(),
                    unhandledWarningCount = 0,
                    onWarningClick = { id -> navController.navigate(Screen.WarningDetail.createRoute(id)) },
                    onHandleClick = { },
                    onViewAllWarnings = { navController.navigate(Screen.WarningList.route) },
                    onQuickHandle = { navController.navigate(Screen.WarningList.route) }
                )
            }

            // 渠道列表
            composable(Screen.IdentifierList.route) {
                IdentifierListScreen(
                    uiState = com.hermes.presentation.viewmodel.IdentifierListState.Loading,
                    onIdentifierClick = { id -> navController.navigate(Screen.IdentifierDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddIdentifier.route) },
                    onRefresh = { }
                )
            }

            // 影响范围（渠道详情）
            composable(
                Screen.IdentifierDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                IdentifierDetailScreen(
                    uiState = com.hermes.presentation.viewmodel.IdentifierDetailState.Loading,
                    deactivationDetail = null,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = { },
                    onAccountClick = { accountId -> navController.navigate(Screen.AccountDetail.createRoute(accountId)) },
                    onCancelDeactivation = { },
                    onModifyDeactivation = { },
                    onScheduleDeactivation = { navController.navigate(Screen.ScheduleDeactivation.createRoute(id)) },
                    canDelete = false
                )
            }

            // 添加验证渠道
            composable(Screen.AddIdentifier.route) {
                AddIdentifierScreen(
                    operationState = com.hermes.presentation.viewmodel.OperationState.Idle,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { _, _ -> navController.popBackStack() },
                    onCheckDuplicate = { _, _, callback -> callback(false) }
                )
            }

            // 账号列表
            composable(Screen.AccountList.route) {
                AccountListScreen(
                    uiState = com.hermes.presentation.viewmodel.AccountListState.Loading,
                    onAccountClick = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) },
                    onAddClick = { navController.navigate(Screen.AddAccount.route) },
                    onRefresh = { }
                )
            }

            // 账号详情
            composable(
                Screen.AccountDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                Box(
                    modifier = Modifier.fillMaxSize().background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("账号详情: $id", color = HermesColors.TextPrimary)
                }
            }

            // 添加账号
            composable(Screen.AddAccount.route) {
                Box(
                    modifier = Modifier.fillMaxSize().background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("添加账号", color = HermesColors.TextPrimary)
                }
            }

            // 提醒列表
            composable(Screen.WarningList.route) {
                WarningListScreen(
                    uiState = com.hermes.presentation.viewmodel.WarningListState.Loading,
                    handledWarnings = emptyList(),
                    onWarningClick = { id -> navController.navigate(Screen.WarningDetail.createRoute(id)) },
                    onHandleClick = { },
                    onRefresh = { }
                )
            }

            // 提醒详情
            composable(
                Screen.WarningDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                Box(
                    modifier = Modifier.fillMaxSize().background(HermesColors.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text("提醒详情: $id", color = HermesColors.TextPrimary)
                }
            }

            // 设置
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 设置到期提醒
            composable(
                Screen.ScheduleDeactivation.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                ScheduleDeactivationScreen(
                    identifierId = id,
                    onBackClick = { navController.popBackStack() },
                    onScheduled = { navController.popBackStack() }
                )
            }

            // 关联账号（影响分析）
            composable(
                Screen.ImpactAnalysis.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                ImpactAnalysisScreen(
                    identifierId = id,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun HermesBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 底部导航图标（对应原型：fa-solid fa-home, fa-shield, fa-box, fa-gear）
    val navIcons = listOf(
        Triple(Screen.Dashboard, Icons.Filled.Home, "首页"),
        Triple(Screen.IdentifierList, Icons.Filled.Security, "渠道"),  // shield 对应 Security
        Triple(Screen.AccountList, Icons.Filled.Inventory2, "账号"),    // box 对应 Inventory2
        Triple(Screen.Settings, Icons.Filled.Settings, "设置")
    )

    NavigationBar(
        containerColor = HermesColors.Surface,
        contentColor = HermesColors.TextPrimary
    ) {
        navIcons.forEach { (screen, icon, label) ->
            val selected = currentRoute == screen.route ||
                (screen.route.contains("{id}") && currentRoute?.startsWith(screen.route.substringBefore("/")) == true)

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) HermesColors.Primary else HermesColors.TextSecondary
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) HermesColors.Primary else HermesColors.TextSecondary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}