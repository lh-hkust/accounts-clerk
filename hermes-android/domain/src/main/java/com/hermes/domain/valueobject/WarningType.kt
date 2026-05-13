package com.hermes.domain.valueobject

/**
 * 预警类型
 * WarningType - 预警的分类
 */
enum class WarningType {
    /**
     * 停用计划提醒
     */
    DEACTIVATION_PLAN,

    /**
     * 绑定账户变更
     */
    BOUND_ACCOUNT_CHANGE,

    /**
     * 截止日期临近
     */
    DEADLINE_APPROACHING
}