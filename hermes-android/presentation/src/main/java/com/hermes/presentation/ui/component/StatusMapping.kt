package com.hermes.presentation.ui.component

import androidx.compose.ui.graphics.Color
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.presentation.ui.theme.HermesColors

/**
 * 状态徽章全域统一映射表
 *
 * 根据 specs/dashboard-navigation/spec.md 定义：
 * - Identifier状态: ACTIVE→正常使用, PENDING_DEACTIVATION→即将到期, DEACTIVATED→已失效, INVALIDATED→已失效
 * - Account状态: ACTIVE→正常使用, FROZEN→已冻结, LOST→已丢失, ARCHIVED→已归档
 * - Warning级别: CRITICAL→紧急, HIGH→建议, MEDIUM→提示, LOW→低
 *
 * 颜色规范：
 * - 正常使用: #22c55e (Success Green)
 * - 即将到期: #eab308 (Warning Yellow)
 * - 已失效/已冻结: #ef4444 (Danger Red)
 * - 已丢失/已归档/低: #6b7280 (TextMuted Gray)
 * - 紧急: badge-danger (Danger Red)
 * - 建议: badge-warning (Warning Yellow)
 * - 提示: badge-info (Info Blue)
 * - 低: badge-muted (TextMuted Gray)
 */

/**
 * 标识状态显示文本
 */
fun getIdentifierStatusText(status: IdentifierStatus): String {
    return when (status) {
        IdentifierStatus.ACTIVE -> "正常使用"
        IdentifierStatus.PENDING_DEACTIVATION -> "即将到期"
        IdentifierStatus.DEACTIVATED -> "已失效"
        IdentifierStatus.INVALIDATED -> "已失效"
    }
}

/**
 * 标识状态颜色
 */
fun getIdentifierStatusColor(status: IdentifierStatus): Color {
    return when (status) {
        IdentifierStatus.ACTIVE -> HermesColors.Success      // #22c55e
        IdentifierStatus.PENDING_DEACTIVATION -> HermesColors.Warning  // #eab308
        IdentifierStatus.DEACTIVATED -> HermesColors.Danger  // #ef4444
        IdentifierStatus.INVALIDATED -> HermesColors.TextMuted  // #6b7280
    }
}

/**
 * 账户状态显示文本
 */
fun getAccountStatusText(status: AccountStatus): String {
    return when (status) {
        AccountStatus.ACTIVE -> "正常使用"
        AccountStatus.FROZEN -> "已冻结"
        AccountStatus.LOST -> "已丢失"
        AccountStatus.ARCHIVED -> "已归档"
    }
}

/**
 * 账户状态颜色
 */
fun getAccountStatusColor(status: AccountStatus): Color {
    return when (status) {
        AccountStatus.ACTIVE -> HermesColors.Success      // #22c55e
        AccountStatus.FROZEN -> HermesColors.Danger       // #ef4444
        AccountStatus.LOST -> HermesColors.TextMuted      // #6b7280
        AccountStatus.ARCHIVED -> HermesColors.TextMuted  // #6b7280
    }
}

/**
 * 预警级别显示文本
 */
fun getWarningLevelText(level: WarningLevel): String {
    return when (level) {
        WarningLevel.HIGH -> "紧急"
        WarningLevel.MEDIUM -> "建议"
        WarningLevel.LOW -> "提示"
    }
}

/**
 * 预警级别颜色
 */
fun getWarningLevelColor(level: WarningLevel): Color {
    return when (level) {
        WarningLevel.HIGH -> HermesColors.Danger    // badge-danger
        WarningLevel.MEDIUM -> HermesColors.Warning // badge-warning
        WarningLevel.LOW -> HermesColors.Info       // badge-info
    }
}

/**
 * 预警级别颜色（用于Dashboard展示）
 */
fun getWarningLevelColorFromString(level: String): Color {
    return when (level) {
        "CRITICAL", "HIGH" -> HermesColors.Danger
        "MEDIUM" -> HermesColors.Warning
        "LOW" -> HermesColors.Success
        else -> HermesColors.TextMuted
    }
}