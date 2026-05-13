package com.hermes.domain.model

import com.hermes.domain.valueobject.ApplicationType
import java.time.Instant

/**
 * 应用聚合根
 * Application (App) - 用户拥有账户的第三方网络服务
 *
 * @see aggregates.md 四、应用聚合
 */
class Application(
    val id: Long?,
    var name: String,
    var type: ApplicationType = ApplicationType.BOTH,
    var officialUrl: String? = null,
    var iconUrl: String? = null,
    var category: String? = null,
    var isActive: Boolean = true,
    val createdAt: Instant,
    var updatedAt: Instant
) {
    /**
     * 更新图标
     */
    fun updateIcon(newIconUrl: String?) {
        iconUrl = newIconUrl
        updatedAt = Instant.now()
    }

    /**
     * 禁用应用
     */
    fun deactivate() {
        isActive = false
        updatedAt = Instant.now()
    }

    /**
     * 启用应用
     */
    fun activate() {
        isActive = true
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Application) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}