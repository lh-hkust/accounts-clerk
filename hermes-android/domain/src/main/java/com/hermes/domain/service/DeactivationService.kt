package com.hermes.domain.service

import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.model.IdentityIdentifier
import java.time.Instant

/**
 * 停用计划服务
 * 协调停用计划的业务逻辑
 */
interface DeactivationService {

    /**
     * 创建停用计划
     *
     * @param identifierId 标识ID
     * @param scheduledTime 计划停用时间
     * @param reason 停用原因
     * @param type 停用类型
     * @return 创建的停用计划实体
     * @throws IllegalArgumentException 如果标识状态不允许设置计划
     */
    fun createDeactivationPlan(
        identifierId: Long,
        scheduledTime: Instant,
        reason: String,
        type: com.hermes.domain.valueobject.DeactivationType
    ): IdentifierDeactivation

    /**
     * 取消停用计划
     *
     * @param identifierId 标识ID
     * @param cancelReason 取消原因
     * @throws IllegalArgumentException 如果标识状态不是PENDING_DEACTIVATION
     */
    fun cancelDeactivationPlan(identifierId: Long, cancelReason: String)

    /**
     * 修改停用日期
     *
     * @param identifierId 标识ID
     * @param newScheduledTime 新的计划停用时间
     * @throws IllegalArgumentException 如果新时间不合法
     */
    fun updateDeactivationDate(identifierId: Long, newScheduledTime: Instant)

    /**
     * 获取停用计划详情
     *
     * @param identifierId 标识ID
     * @return 停用计划实体（如果存在）
     */
    fun getDeactivationByIdentifierId(identifierId: Long): IdentifierDeactivation?

    /**
     * 执行停用
     * 当计划时间到达时执行
     *
     * @param identifierId 标识ID
     */
    fun executeDeactivation(identifierId: Long)

    /**
     * 获取即将停用的标识列表
     * 返回计划时间在未来N天内的标识
     *
     * @param days 天数范围
     * @return 待停用的标识列表
     */
    fun getUpcomingDeactivations(days: Int): List<IdentityIdentifier>
}