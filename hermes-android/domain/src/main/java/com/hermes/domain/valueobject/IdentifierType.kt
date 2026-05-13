package com.hermes.domain.valueobject

/**
 * 身份标识类型
 * IdentityIdentifier Type - 用于声明身份的唯一标识分类
 *
 * @see NIST SP 800-63B Digital Identity Guidelines
 */
enum class IdentifierType {
    /**
     * 手机号 - PHONE
     * UI界面用语：手机号
     */
    PHONE,

    /**
     * 邮箱 - EMAIL
     * UI界面用语：邮箱
     */
    EMAIL
}