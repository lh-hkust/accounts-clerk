package com.hermes.presentation.usecase.application

import com.hermes.domain.model.Application
import com.hermes.domain.repository.ApplicationRepository
import com.hermes.domain.valueobject.ApplicationType
import java.time.Instant

/**
 * 自定义添加应用用例
 */
class AddCustomApplicationUseCase(
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 添加自定义应用
     *
     * @param name 应用名称
     * @param type 应用类型
     * @param officialUrl 官网地址（可选）
     * @param iconUrl 图标地址（可选）
     * @param category 分类（可选）
     * @return 创建的应用
     * @throws IllegalArgumentException 如果应用名称重复
     */
    suspend operator fun invoke(
        name: String,
        type: ApplicationType,
        officialUrl: String? = null,
        iconUrl: String? = null,
        category: String? = null
    ): Application {
        if (name.isBlank()) {
            throw IllegalArgumentException("Application name must not be empty")
        }

        if (applicationRepository.checkDuplicate(name)) {
            throw IllegalArgumentException("Application already exists")
        }

        val now = Instant.now()
        val application = Application(
            id = null,
            name = name,
            type = type,
            officialUrl = officialUrl,
            iconUrl = iconUrl,
            category = category,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        return applicationRepository.insert(application)
    }
}