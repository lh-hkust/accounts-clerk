package com.hermes.presentation.usecase.application

import com.hermes.domain.model.Application
import com.hermes.domain.repository.ApplicationRepository

/**
 * 获取应用列表用例
 */
class GetApplicationListUseCase(
    private val applicationRepository: ApplicationRepository
) {
    /**
     * 获取所有活跃应用列表
     *
     * @return 应用列表
     */
    suspend operator fun invoke(): List<Application> {
        return applicationRepository.getAllActive()
    }

    /**
     * 按分类获取应用列表
     *
     * @param category 分类名称
     * @return 应用列表
     */
    suspend fun getByCategory(category: String): List<Application> {
        return applicationRepository.getByCategory(category)
    }

    /**
     * 获取应用详情
     *
     * @param applicationId 应用ID
     * @return 应用详情（如果存在）
     */
    suspend fun getById(applicationId: Long): Application? {
        return applicationRepository.getById(applicationId)
    }
}