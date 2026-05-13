package com.hermes.presentation.usecase.warning

import com.hermes.domain.repository.WarningRecordRepository

/**
 * 清除预警用例
 */
class ClearWarningUseCase(
    private val warningRepository: WarningRecordRepository
) {
    /**
     * 清除标识相关的所有预警
     *
     * @param identifierId 标识ID
     */
    suspend operator fun invoke(identifierId: Long) {
        warningRepository.deleteByIdentifierId(identifierId)
    }
}