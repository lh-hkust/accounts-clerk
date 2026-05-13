package com.hermes.presentation.ui.navigation

sealed class Screen(
    val route: String,
    val title: String
) {
    object Dashboard : Screen("dashboard", "首页")
    object IdentifierList : Screen("identifiers", "渠道")
    object IdentifierDetail : Screen("identifier/{id}", "影响范围") {
        fun createRoute(id: Long) = "identifier/$id"
    }
    object AddIdentifier : Screen("add_identifier", "添加验证渠道")
    object AccountList : Screen("accounts", "账号")
    object AccountDetail : Screen("account/{id}", "账号详情") {
        fun createRoute(id: Long) = "account/$id"
    }
    object AddAccount : Screen("add_account", "添加账号")
    object WarningList : Screen("warnings", "提醒")
    object WarningDetail : Screen("warning/{id}", "提醒详情") {
        fun createRoute(id: Long) = "warning/$id"
    }
    object Settings : Screen("settings", "设置")
    object ScheduleDeactivation : Screen("schedule_deactivation/{id}", "设置到期提醒") {
        fun createRoute(id: Long) = "schedule_deactivation/$id"
    }
    object ImpactAnalysis : Screen("impact/{id}", "关联账号") {
        fun createRoute(id: Long) = "impact/$id"
    }
}

sealed class BottomNavItem(
    val screen: Screen,
    val iconRes: String,
    val label: String
) {
    object Dashboard : BottomNavItem(Screen.Dashboard, "ic_home", "首页")
    object Identifiers : BottomNavItem(Screen.IdentifierList, "ic_shield", "渠道")
    object Accounts : BottomNavItem(Screen.AccountList, "ic_box", "账号")
    object Settings : BottomNavItem(Screen.Settings, "ic_settings", "设置")
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Identifiers,
    BottomNavItem.Accounts,
    BottomNavItem.Settings
)