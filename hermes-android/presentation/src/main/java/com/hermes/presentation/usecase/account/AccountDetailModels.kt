package com.hermes.presentation.usecase.account

import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.IdentifierType

/**
 * 标识绑定信息（用于账号详情页面）
 */
data class IdentifierBindingInfo(
    val identifierId: Long,
    val identifierType: IdentifierType,
    val identifierValue: String,
    val purposes: List<BindingPurpose>
)

/**
 * 关联账号信息（用于账号详情页面）
 */
data class RelatedAccountInfo(
    val accountId: Long,
    val applicationName: String,
    val accountName: String,
    val relationType: String
)