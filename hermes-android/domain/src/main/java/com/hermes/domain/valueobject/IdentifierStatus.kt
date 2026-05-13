package com.hermes.domain.valueobject

/**
 * 身份标识状态
 * IdentityIdentifier Status - 身份标识的生命周期状态
 */
enum class IdentifierStatus {
    /**
     * 正常使用 - ACTIVE
     * 标识可正常使用
     * UI界面用语：正常使用
     */
    ACTIVE,

    /**
     * 待停用 - PENDING_DEACTIVATION
     * 已设置计划停用时间
     * UI界面用语：即将到期
     */
    PENDING_DEACTIVATION,

    /**
     * 已停用 - DEACTIVATED
     * 标识已停用（换号、注销）
     * UI界面用语：已失效
     */
    DEACTIVATED,

    /**
     * 已失效 - INVALIDATED
     * 标识验证失败（如手机号被运营商回收）
     * UI界面用语：已失效
     */
    INVALIDATED
}