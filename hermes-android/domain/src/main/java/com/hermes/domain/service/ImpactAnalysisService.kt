package com.hermes.domain.service

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.WarningLevel

/**
 * 影响分析服务
 * 分析标识停用对账户的影响
 */
interface ImpactAnalysisService {

    /**
     * 分析标识停用的影响
     *
     * @param identifierId 标识ID
     * @return 影响分析结果
     */
    fun analyzeImpact(identifierId: Long): ImpactResult

    /**
     * 获取受影响的账户列表
     *
     * @param identifierId 标识ID
     * @return 受影响的账户列表
     */
    fun getAffectedAccounts(identifierId: Long): List<ApplicationAccount>

    /**
     * 计算影响风险级别
     * 基于账户数量和账户分类
     *
     * @param affectedAccounts 受影响的账户列表
     * @return 风险级别
     */
    fun calculateImpactLevel(affectedAccounts: List<ApplicationAccount>): WarningLevel

    /**
     * 获取账户的影响详情
     * 显示账户绑定的标识列表和用途
     *
     * @param accountId 账户ID
     * @return 账户影响详情
     */
    fun getAccountImpactDetail(accountId: Long): AccountImpactDetail?

    /**
     * 检查账户是否有金融类应用
     *
     * @param accountId 账户ID
     * @return 是否为金融类账户
     */
    fun isFinancialAccount(accountId: Long): Boolean

    /**
     * 获取标识的剩余有效天数
     *
     * @param identifierId 标识ID
     * @return 剩余天数（如果设置了停用计划）
     */
    fun getRemainingDays(identifierId: Long): Int?
}

/**
 * 影响分析结果
 */
data class ImpactResult(
    val identifier: IdentityIdentifier,
    val affectedAccounts: List<ApplicationAccount>,
    val impactLevel: WarningLevel,
    val remainingDays: Int?,
    val hasFinancialAccounts: Boolean,
    val financialAccountCount: Int
)

/**
 * 账户影响详情
 */
data class AccountImpactDetail(
    val account: ApplicationAccount,
    val boundIdentifiers: List<BoundIdentifierInfo>,
    val canOperateWithoutIdentifier: Boolean
)

/**
 * 绑定标识信息
 */
data class BoundIdentifierInfo(
    val identifierId: Long,
    val identifierType: com.hermes.domain.valueobject.IdentifierType,
    val identifierValue: String,
    val purposes: List<com.hermes.domain.valueobject.BindingPurpose>,
    val isPrimary: Boolean
)