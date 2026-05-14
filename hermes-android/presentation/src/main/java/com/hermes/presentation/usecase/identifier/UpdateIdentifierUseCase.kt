package com.hermes.presentation.usecase.identifier

import com.hermes.domain.repository.IdentityIdentifierRepository
import javax.inject.Inject

/**
 * 更新身份标识用例
 * 用于更新标识的备注信息
 */
class UpdateIdentifierUseCase @Inject constructor(
    private val identifierRepository: IdentityIdentifierRepository
) {
    /**
     * 更新标识备注
     *
     * @param identifierId 标识ID
     * @param remark 新备注内容（可为空）
     * @throws IllegalArgumentException 如果标识不存在
     */
    suspend operator fun invoke(identifierId: Long, remark: String?) {
        val identifier = identifierRepository.getById(identifierId)
            ?: throw IllegalArgumentException("Identifier not found")

        identifier.updateRemark(remark)
        identifierRepository.update(identifier)
    }
}