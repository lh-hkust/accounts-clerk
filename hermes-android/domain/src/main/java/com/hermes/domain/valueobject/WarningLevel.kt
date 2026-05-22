package com.hermes.domain.valueobject

/**
 * 预警级别
 * WarningLevel - 预警的紧急程度
 */
enum class WarningLevel {
    /**
     * 高 - HIGH
     * 紧急预警，需立即处理
     * 适用场景：标识即将停用，影响敏感账户
     * UI界面用语：紧急处理
     */
    HIGH,

    /**
     * 中 - MEDIUM
     * 中等预警，建议尽快处理
     * 适用场景：标识即将停用，影响普通账户
     * UI界面用语：建议处理
     */
    MEDIUM,

    /**
     * 低 - LOW
     * 低级别预警，可稍后处理
     * 适用场景：标识状态变更通知
     * UI界面用语：可稍后处理
     */
    LOW
}