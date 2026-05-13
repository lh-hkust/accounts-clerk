package com.hermes.presentation.usecase.identifier

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.valueobject.IdentifierStatus

/**
 * 获取标识列表用例
 */
class GetIdentifierListUseCase(
    private val identifierRepository: IdentityIdentifierRepository,
    private val bindingRepository: IdentifierBindingRepository
) {
    /**
     * 获取所有标识列表（按状态分组排序）
     *
     * @return 标识列表，按状态和更新时间排序
     */
    suspend operator fun invoke(): List<IdentifierListItem> {
        val identifiers = identifierRepository.getAll()
        return identifiers.map { identifier ->
            val boundCount = bindingRepository.getCountByIdentifierId(identifier.id!!)
            IdentifierListItem(
                identifier = identifier,
                boundAccountCount = boundCount
            )
        }.sortedWith(compareBy(
            { getStatusOrder(it.identifier.status) },
            { it.identifier.updatedAt }
        ))
    }

    /**
     * 按状态获取标识列表
     *
     * @param status 状态筛选
     * @return 标识列表
     */
    suspend fun getByStatus(status: IdentifierStatus): List<IdentifierListItem> {
        val identifiers = identifierRepository.getByStatus(status)
        return identifiers.map { identifier ->
            val boundCount = bindingRepository.getCountByIdentifierId(identifier.id!!)
            IdentifierListItem(
                identifier = identifier,
                boundAccountCount = boundCount
            )
        }
    }

    private fun getStatusOrder(status: IdentifierStatus): Int {
        return when (status) {
            IdentifierStatus.ACTIVE -> 1
            IdentifierStatus.PENDING_DEACTIVATION -> 2
            IdentifierStatus.DEACTIVATED -> 3
            IdentifierStatus.INVALIDATED -> 4
        }
    }
}

/**
 * 标识列表项（包含绑定账户数量）
 */
data class IdentifierListItem(
    val identifier: IdentityIdentifier,
    val boundAccountCount: Int
)