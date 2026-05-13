package com.hermes.presentation.usecase.warning

import com.hermes.domain.repository.WarningRecordRepository

/**
 * 处理预警用例
 */
class HandleWarningUseCase(
    private val warningRepository: WarningRecordRepository
) {
    /**
     * 处理预警
     *
     * @param warningId 预警ID
     */
    suspend operator fun invoke(warningId: Long) {
        warningRepository.markAsHandled(warningId)
    }
}