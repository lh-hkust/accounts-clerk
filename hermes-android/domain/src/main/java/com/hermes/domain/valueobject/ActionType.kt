package com.hermes.domain.valueobject

/**
 * 绑定操作类型
 * ActionType - 标识绑定历史记录的操作类型
 */
enum class ActionType {
    /**
     * 绑定 - BIND
     * 新增标识绑定
     */
    BIND,

    /**
     * 解绑 - UNBIND
     * 解除标识绑定
     */
    UNBIND,

    /**
     * 更改用途 - CHANGE_PURPOSE
     * 修改绑定用途
     */
    CHANGE_PURPOSE,

    /**
     * 更换标识 - SWITCH_IDENTIFIER
     * 更换绑定的身份标识
     */
    SWITCH_IDENTIFIER,

    /**
     * 重绑定 - REBIND
     * 重新绑定已解绑的标识
     */
    REBIND
}