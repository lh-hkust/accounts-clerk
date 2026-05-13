package com.hermes.domain.service

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import java.time.Instant

/**
 * 预警服务
 * 协调预警系统的业务逻辑
 */
interface WarningService {

    /**
     * 触发预警
     * 当停用计划创建时自动触发
     *
     * @param identifierId 标识ID
     * @param warningType 预警类型
     * @param affectedAccounts 受影响的账户列表
     * @return 创建的预警记录
     */
    fun triggerWarning(
        identifierId: Long,
        warningType: WarningType,
        affectedAccounts: List<ApplicationAccount>
    ): WarningRecord

    /**
     * 计算预警级别
     * 基于受影响账户数量和账户分类
     *
     * 规则:
     * - 影响金融类应用账户 -> HIGH
     * - 影响账户数 > 5 -> HIGH
     * - 影响账户数 2-5（无金融应用） -> MEDIUM
     * - 影响账户数 = 1（非敏感账户） -> LOW
     *
     * @param affectedAccounts 受影响的账户列表
     * @return 预警级别
     */
    fun calculateWarningLevel(affectedAccounts: List<ApplicationAccount>): WarningLevel

    /**
     * 获取预警列表
     *
     * @param onlyUnhandled 是否只获取未处理的预警
     * @return 预警列表（按级别和时间排序）
     */
    fun getWarningList(onlyUnhandled: Boolean = true): List<WarningRecord>

    /**
     * 获取预警详情
     *
     * @param warningId 预警ID
     * @return 预警记录（如果存在）
     */
    fun getWarningById(warningId: Long): WarningRecord?

    /**
     * 处理预警
     *
     * @param warningId 预警ID
     */
    fun handleWarning(warningId: Long)

    /**
     * 标记预警已读
     *
     * @param warningId 预警ID
     */
    fun markWarningAsRead(warningId: Long)

    /**
     * 清除预警
     * 当停用计划取消时清除相关预警
     *
     * @param identifierId 标识ID
     */
    fun clearWarningsByIdentifierId(identifierId: Long)

    /**
     * 获取快速处理预警列表
     * 返回最多3条未处理预警，按级别和截止时间排序
     *
     * @return 预警列表（最多3条）
     */
    fun getQuickHandleWarnings(): List<WarningRecord>
}