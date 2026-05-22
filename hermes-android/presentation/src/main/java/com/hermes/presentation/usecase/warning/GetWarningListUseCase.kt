package com.hermes.presentation.usecase.warning

import com.hermes.domain.model.WarningRecord
import com.hermes.domain.repository.WarningRecordRepository

/**
 * 获取预警列表用例
 */
class GetWarningListUseCase(
    private val warningRepository: WarningRecordRepository
) {
    /**
     * 获取所有预警（按级别排序）
     *
     * @return 预警列表
     */
    suspend operator fun invoke(): List<WarningRecord> {
        return warningRepository.getUnhandled()
    }

    /**
     * 获取已处理预警
     *
     * @return 已处理预警列表
     */
    suspend fun getHandled(): List<WarningRecord> {
        return warningRepository.getHandled()
    }

    /**
     * 获取快速处理列表（最多3条）
     *
     * @return 预警列表（最多3条）
     */
    suspend fun getQuickHandleList(): List<WarningRecord> {
        return warningRepository.getQuickHandleList(3)
    }

    /**
     * 获取未处理预警数量
     *
     * @return 未处理预警数量
     */
    suspend fun getUnhandledCount(): Int {
        return warningRepository.getUnhandledCount()
    }
}