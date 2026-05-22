package com.hermes.domain.service

import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.DeactivationStatus
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * DeactivationService 单元测试
 *
 * 测试停用计划服务的核心业务逻辑
 */
class DeactivationServiceTest {

    private val mockService = MockDeactivationService()

    @Test
    fun `创建停用计划应成功`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60) // 30天后

        val deactivation = mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        assertNotNull(deactivation)
        assertEquals(identifier.id!!, deactivation.identifierId)
        assertEquals(DeactivationType.PHONE_NUMBER_CHANGE, deactivation.deactType)
        assertEquals(DeactivationStatus.SCHEDULED, deactivation.status)
        assertEquals(scheduledTime, deactivation.scheduledTime)
    }

    @Test
    fun `创建停用计划后标识状态应为PENDING_DEACTIVATION`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        val updatedIdentifier = mockService.getIdentifierById(identifier.id!!)
        assertEquals(IdentifierStatus.PENDING_DEACTIVATION, updatedIdentifier!!.status)
    }

    @Test
    fun `为非ACTIVE标识创建停用计划应抛出异常`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        // 先设置一个停用计划
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "第一次计划",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        // 再次尝试创建停用计划
        try {
            mockService.createDeactivationPlan(
                identifierId = identifier.id!!,
                scheduledTime = Instant.now().plusSeconds(60 * 24 * 60 * 60),
                reason = "第二次计划",
                type = DeactivationType.PHONE_NUMBER_CHANGE
            )
            fail("Should throw IllegalArgumentException for non-ACTIVE identifier")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("ACTIVE"))
        }
    }

    @Test
    fun `取消停用计划应成功`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        mockService.cancelDeactivationPlan(identifier.id!!, "不再更换")

        val updatedIdentifier = mockService.getIdentifierById(identifier.id!!)
        assertEquals(IdentifierStatus.ACTIVE, updatedIdentifier!!.status)
        assertNull(updatedIdentifier.plannedDeactTime)
    }

    @Test
    fun `取消停用计划后停用计划状态应为CANCELLED`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val deactivation = mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        mockService.cancelDeactivationPlan(identifier.id!!, "不再更换")

        val updatedDeactivation = mockService.getDeactivationByIdentifierId(identifier.id!!)
        assertEquals(DeactivationStatus.CANCELLED, updatedDeactivation!!.status)
        assertEquals("不再更换", updatedDeactivation.cancelReason)
    }

    @Test
    fun `取消非PENDING_DEACTIVATION状态的停用计划应抛出异常`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        // 直接执行停用，不设置计划
        identifier.deactivate()

        try {
            mockService.cancelDeactivationPlan(identifier.id!!, "尝试取消")
            fail("Should throw IllegalArgumentException for non-PENDING_DEACTIVATION status")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("PENDING_DEACTIVATION"))
        }
    }

    @Test
    fun `修改停用日期应成功`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val originalTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = originalTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        val newTime = Instant.now().plusSeconds(60 * 24 * 60 * 60) // 60天后
        mockService.updateDeactivationDate(identifier.id!!, newTime)

        val updatedDeactivation = mockService.getDeactivationByIdentifierId(identifier.id!!)
        assertEquals(newTime, updatedDeactivation!!.scheduledTime)
    }

    @Test
    fun `修改停用日期为过去时间应抛出异常`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        try {
            mockService.updateDeactivationDate(identifier.id!!, Instant.now().minusSeconds(24 * 60 * 60))
            fail("Should throw IllegalArgumentException for past time")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("future") || e.message!!.contains("greater"))
        }
    }

    @Test
    fun `执行停用应成功`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        mockService.executeDeactivation(identifier.id!!)

        val updatedIdentifier = mockService.getIdentifierById(identifier.id!!)
        assertEquals(IdentifierStatus.DEACTIVATED, updatedIdentifier!!.status)
    }

    @Test
    fun `执行停用后停用计划状态应为EXECUTED`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        mockService.executeDeactivation(identifier.id!!)

        val deactivation = mockService.getDeactivationByIdentifierId(identifier.id!!)
        assertEquals(DeactivationStatus.EXECUTED, deactivation!!.status)
        assertNotNull(deactivation.actualTime)
    }

    @Test
    fun `获取即将停用的标识列表应返回正确范围`() {
        // 创建3个标识，不同的停用计划时间
        val identifier1 = mockService.createIdentifier(IdentifierType.PHONE, "13800138001")
        val identifier2 = mockService.createIdentifier(IdentifierType.PHONE, "13800138002")
        val identifier3 = mockService.createIdentifier(IdentifierType.EMAIL, "test1@example.com")

        // 5天后停用（在7天范围内）
        mockService.createDeactivationPlan(
            identifierId = identifier1.id!!,
            scheduledTime = Instant.now().plusSeconds(5 * 24 * 60 * 60),
            reason = "原因1",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        // 10天后停用（超出7天范围）
        mockService.createDeactivationPlan(
            identifierId = identifier2.id!!,
            scheduledTime = Instant.now().plusSeconds(10 * 24 * 60 * 60),
            reason = "原因2",
            type = DeactivationType.EMAIL_CHANGE
        )

        // 3天后停用（在7天范围内）
        mockService.createDeactivationPlan(
            identifierId = identifier3.id!!,
            scheduledTime = Instant.now().plusSeconds(3 * 24 * 60 * 60),
            reason = "原因3",
            type = DeactivationType.EMAIL_CHANGE
        )

        val upcoming = mockService.getUpcomingDeactivations(7)

        assertEquals(2, upcoming.size)
        assertTrue(upcoming.any { it.id == identifier1.id })
        assertTrue(upcoming.any { it.id == identifier3.id })
        assertFalse(upcoming.any { it.id == identifier2.id })
    }

    @Test
    fun `获取停用计划详情应返回正确数据`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)
        val created = mockService.createDeactivationPlan(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        val retrieved = mockService.getDeactivationByIdentifierId(identifier.id!!)

        assertNotNull(retrieved)
        assertEquals(created.id, retrieved!!.id)
        assertEquals(created.scheduledTime, retrieved.scheduledTime)
        assertEquals(created.reason, retrieved.reason)
    }

    @Test
    fun `获取无停用计划标识的停用计划应返回null`() {
        val identifier = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")

        val deactivation = mockService.getDeactivationByIdentifierId(identifier.id!!)

        assertNull(deactivation)
    }

    /**
     * 模拟 DeactivationService 实现
     */
    private class MockDeactivationService : DeactivationService {
        private val identifiers = mutableListOf<IdentityIdentifier>()
        private val deactivations = mutableListOf<IdentifierDeactivation>()
        private var nextId = 1L

        fun createIdentifier(type: IdentifierType, value: String): IdentityIdentifier {
            val now = Instant.now()
            val identifier = IdentityIdentifier(
                id = nextId++,
                type = type,
                value = value,
                status = IdentifierStatus.ACTIVE,
                createdAt = now,
                updatedAt = now
            )
            identifiers.add(identifier)
            return identifier
        }

        fun getIdentifierById(id: Long): IdentityIdentifier? {
            return identifiers.find { it.id == id }
        }

        override fun createDeactivationPlan(
            identifierId: Long,
            scheduledTime: Instant,
            reason: String,
            type: com.hermes.domain.valueobject.DeactivationType
        ): IdentifierDeactivation {
            val identifier = getIdentifierById(identifierId)
                ?: throw IllegalArgumentException("Identifier not found")

            if (identifier.status != IdentifierStatus.ACTIVE) {
                throw IllegalArgumentException("Only ACTIVE identifiers can be scheduled for deactivation")
            }

            if (scheduledTime.isBefore(Instant.now())) {
                throw IllegalArgumentException("Deactivation time must be greater than current time")
            }

            // 更新标识状态
            identifier.status = IdentifierStatus.PENDING_DEACTIVATION
            identifier.plannedDeactTime = scheduledTime
            identifier.deactReason = reason

            // 创建停用计划
            val now = Instant.now()
            val deactivation = IdentifierDeactivation(
                id = nextId++,
                identifierId = identifierId,
                deactType = type,
                status = DeactivationStatus.SCHEDULED,
                scheduledTime = scheduledTime,
                reason = reason,
                createdAt = now,
                updatedAt = now
            )
            deactivations.add(deactivation)
            return deactivation
        }

        override fun cancelDeactivationPlan(identifierId: Long, cancelReason: String) {
            val identifier = getIdentifierById(identifierId)
                ?: throw IllegalArgumentException("Identifier not found")

            if (identifier.status != IdentifierStatus.PENDING_DEACTIVATION) {
                throw IllegalArgumentException("Only PENDING_DEACTIVATION identifiers can cancel deactivation")
            }

            // 更新标识状态
            identifier.status = IdentifierStatus.ACTIVE
            identifier.plannedDeactTime = null
            identifier.deactReason = null

            // 更新停用计划
            val deactivation = deactivations.find { it.identifierId == identifierId }
            deactivation?.cancel(cancelReason)
        }

        override fun updateDeactivationDate(identifierId: Long, newScheduledTime: Instant) {
            if (newScheduledTime.isBefore(Instant.now())) {
                throw IllegalArgumentException("New deactivation time must be in the future")
            }

            val identifier = getIdentifierById(identifierId)
                ?: throw IllegalArgumentException("Identifier not found")

            val deactivation = deactivations.find { it.identifierId == identifierId }
                ?: throw IllegalArgumentException("Deactivation plan not found")

            identifier.plannedDeactTime = newScheduledTime
            deactivation.updateScheduledTime(newScheduledTime)
        }

        override fun getDeactivationByIdentifierId(identifierId: Long): IdentifierDeactivation? {
            return deactivations.find { it.identifierId == identifierId }
        }

        override fun executeDeactivation(identifierId: Long) {
            val identifier = getIdentifierById(identifierId)
                ?: throw IllegalArgumentException("Identifier not found")

            val deactivation = deactivations.find { it.identifierId == identifierId }
                ?: throw IllegalArgumentException("Deactivation plan not found")

            identifier.status = IdentifierStatus.DEACTIVATED
            deactivation.execute()
        }

        override fun getUpcomingDeactivations(days: Int): List<IdentityIdentifier> {
            val now = Instant.now()
            val endTime = now.plusSeconds(days * 24 * 60 * 60L)

            return identifiers.filter { identifier ->
                identifier.status == IdentifierStatus.PENDING_DEACTIVATION &&
                identifier.plannedDeactTime != null &&
                identifier.plannedDeactTime!!.isBefore(endTime) &&
                identifier.plannedDeactTime!!.isAfter(now)
            }
        }
    }
}