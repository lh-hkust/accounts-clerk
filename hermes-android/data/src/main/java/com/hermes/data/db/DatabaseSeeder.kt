package com.hermes.data.db

import com.hermes.data.dao.ApplicationDao
import com.hermes.data.entity.ApplicationEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据库种子化器 - 初始化预置数据
 *
 * 种子化时机：首次启动时（应用表为空时）
 * 种子化策略：INSERT IGNORE，不覆盖已存在的数据
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val applicationDao: ApplicationDao
) {
    /**
     * 种子化预置应用数据
     *
     * 条件：仅在应用表为空时执行（首次安装）
     * 策略：INSERT IGNORE，不覆盖用户自定义应用
     */
    suspend fun seedApplicationsIfNeeded() {
        // 检查是否已有应用数据
        val count = applicationDao.getCount()
        if (count > 0) {
            // 已有数据，不执行种子化（避免覆盖用户数据）
            return
        }

        val now = System.currentTimeMillis()
        val presetApps = listOf(
            ApplicationEntity(id = 1, name = "微信", type = "BOTH", category = "社交", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 2, name = "QQ", type = "BOTH", category = "社交", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 3, name = "微博", type = "BOTH", category = "社交", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 4, name = "抖音", type = "BOTH", category = "娱乐", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 5, name = "支付宝", type = "BOTH", category = "金融", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 6, name = "淘宝", type = "BOTH", category = "购物", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 7, name = "京东", type = "BOTH", category = "购物", isActive = true, createdAt = now, updatedAt = now),
            ApplicationEntity(id = 8, name = "GitHub", type = "BOTH", category = "开发", isActive = true, createdAt = now, updatedAt = now)
        )

        // 使用INSERT IGNORE策略，确保不覆盖已存在的数据
        applicationDao.insertOrIgnore(presetApps)
    }

    /**
     * 执行所有种子化操作（仅首次启动时）
     */
    suspend fun seedAllIfNeeded() {
        seedApplicationsIfNeeded()
    }
}