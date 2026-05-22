package com.hermes.presentation.usecase.warning

import com.hermes.domain.repository.WarningRecordRepository

/**
 * 标记预警已读用例
 */
class MarkWarningReadUseCase(
    private val warningRepository: WarningRecordRepository
) {
    /**
     * 标记预警已读
     *
     * @param warningId 预警ID
     */
    suspend operator fun invoke(warningId: Long) {
        warningRepository.markAsRead(warningId)
    }
}