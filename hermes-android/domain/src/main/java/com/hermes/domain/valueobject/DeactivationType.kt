package com.hermes.domain.valueobject

/**
 * 停用类型
 * DeactivationType - 标识停用原因的分类
 */
enum class DeactivationType {
    /**
     * 手机号更换
     */
    PHONE_NUMBER_CHANGE,

    /**
     * 邮箱更换
     */
    EMAIL_CHANGE,

    /**
     * 账户关闭
     */
    ACCOUNT_CLOSURE,

    /**
     * 其他原因
     */
    OTHER
}