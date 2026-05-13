package com.hermes.domain.valueobject

/**
 * 绑定用途
 * BindingPurpose - 身份标识在账户中的具体用途
 */
enum class BindingPurpose {
    /**
     * 登录 - LOGIN
     * 作为登录标识（用户名）
     * UI界面用语：登录
     */
    LOGIN,

    /**
     * 验证 - VERIFICATION
     * 安全验证（接收验证码）
     * UI界面用语：验证
     */
    VERIFICATION,

    /**
     * 找回 - RECOVERY
     * 找回密码/账户
     * UI界面用语：找回
     */
    RECOVERY,

    /**
     * 通知 - NOTIFICATION
     * 接收通知消息
     * UI界面用语：通知
     */
    NOTIFICATION,

    /**
     * 二次验证 - SECONDARY_AUTH
     * 双因素认证
     * UI界面用语：二次验证
     */
    SECONDARY_AUTH
}