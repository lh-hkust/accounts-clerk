package com.hermes.domain.valueobject

/**
 * 应用账户状态
 * ApplicationAccount Status - 账户的生命周期状态
 */
enum class AccountStatus {
    /**
     * 正常使用 - ACTIVE
     * 账户可正常使用
     * UI界面用语：正常使用
     */
    ACTIVE,

    /**
     * 冻结 - FROZEN
     * 因标识停用导致账户暂停使用
     * UI界面用语：已冻结
     */
    FROZEN,

    /**
     * 丢失 - LOST
     * 所有找回渠道不可用，无法找回
     * UI界面用语：已丢失
     */
    LOST,

    /**
     * 归档 - ARCHIVED
     * 已归档，隐藏显示
     * UI界面用语：已归档
     */
    ARCHIVED
}