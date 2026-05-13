package com.hermes.domain.valueobject

/**
 * 停用计划状态
 * DeactivationStatus - 停用计划的执行状态
 */
enum class DeactivationStatus {
    /**
     * 已计划 - SCHEDULED
     * 停用计划已创建，等待执行
     */
    SCHEDULED,

    /**
     * 已执行 - EXECUTED
     * 停用计划已执行完成
     */
    EXECUTED,

    /**
     * 已取消 - CANCELLED
     * 停用计划被用户取消
     */
    CANCELLED
}