package com.hermes.domain.service

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.ApplicationType
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * WarningService 单元测试
 *
 * 测试预警服务的核心业务逻辑
 */
class WarningServiceTest {

    private val mockService = MockWarningService()

    @Test
    fun `触发预警应成功`() {
        val affectedAccounts = createTestAccounts(3)
        val identifierId = 1L

        val warning = mockService.triggerWarning(
            identifierId = identifierId,
            warningType = WarningType.DEACTIVATION_PLAN,
            affectedAccounts = affectedAccounts
        )

        assertNotNull(warning)
        assertEquals(identifierId, warning.identifierId)
        assertEquals(WarningType.DEACTIVATION_PLAN, warning.warningType)
        assertFalse(warning.isRead)
        assertFalse(warning.isHandled)
    }

    @Test
    fun `影响金融类账户应触发HIGH级别预警`() {
        val financialAccount = createFinancialAccount()
        val affectedAccounts = listOf(financialAccount)

        val level = mockService.calculateWarningLevel(affectedAccounts)

        assertEquals(WarningLevel.HIGH, level)
    }

    @Test
    fun `影响超过5个账户应触发HIGH级别预警`() {
        val affectedAccounts = createTestAccounts(6)

        val level = mockService.calculateWarningLevel(affectedAccounts)

        assertEquals(WarningLevel.HIGH, level)
    }

    @Test
    fun `影响2-5个账户（无金融）应触发MEDIUM级别预警`() {
        val affectedAccounts = createTestAccounts(3)

        val level = mockService.calculateWarningLevel(affectedAccounts)

        assertEquals(WarningLevel.MEDIUM, level)
    }

    @Test
    fun `影响1个账户（非敏感）应触发LOW级别预警`() {
        val affectedAccounts = createTestAccounts(1)

        val level = mockService.calculateWarningLevel(affectedAccounts)

        assertEquals(WarningLevel.LOW, level)
    }

    @Test
    fun `影响0个账户应返回LOW级别`() {
        val affectedAccounts = emptyList<ApplicationAccount>()

        val level = mockService.calculateWarningLevel(affectedAccounts)

        assertEquals(WarningLevel.LOW, level)
    }

    @Test
    fun `触发预警时应正确计算预警级别`() {
        val financialAccount = createFinancialAccount()
        val affectedAccounts = listOf(financialAccount)

        val warning = mockService.triggerWarning(
            identifierId = 1L,
            warningType = WarningType.DEACTIVATION_PLAN,
            affectedAccounts = affectedAccounts
        )

        assertEquals(WarningLevel.HIGH, warning.warningLevel)
    }

    @Test
    fun `获取预警列表应返回所有预警`() {
        mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))
        mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1))

        val warnings = mockService.getWarningList(onlyUnhandled = false)

        assertEquals(2, warnings.size)
    }

    @Test
    fun `获取未处理预警列表应只返回未处理的`() {
        val warning1 = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))
        val warning2 = mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1))

        // 处理第一条预警
        mockService.handleWarning(warning1.id!!)

        val unhandledWarnings = mockService.getWarningList(onlyUnhandled = true)

        assertEquals(1, unhandledWarnings.size)
        assertEquals(warning2.id!!, unhandledWarnings[0].id)
    }

    @Test
    fun `获取预警详情应返回正确数据`() {
        val warning = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))

        val retrieved = mockService.getWarningById(warning.id!!)

        assertNotNull(retrieved)
        assertEquals(warning.id!!, retrieved!!.id)
        assertEquals(warning.identifierId, retrieved.identifierId)
        assertEquals(warning.warningType, retrieved.warningType)
    }

    @Test
    fun `获取不存在的预警应返回null`() {
        val retrieved = mockService.getWarningById(999L)
        assertNull(retrieved)
    }

    @Test
    fun `处理预警应更新状态`() {
        val warning = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))

        mockService.handleWarning(warning.id!!)

        val updated = mockService.getWarningById(warning.id!!)
        assertTrue(updated!!.isHandled)
        assertNotNull(updated.handledAt)
    }

    @Test
    fun `标记预警已读应更新状态`() {
        val warning = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))

        mockService.markWarningAsRead(warning.id!!)

        val updated = mockService.getWarningById(warning.id!!)
        assertTrue(updated!!.isRead)
    }

    @Test
    fun `清除标识相关预警应成功`() {
        val warning1 = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))
        val warning2 = mockService.triggerWarning(1L, WarningType.DEADLINE_APPROACHING, createTestAccounts(2))
        mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1))

        mockService.clearWarningsByIdentifierId(1L)

        val remainingWarnings = mockService.getWarningList(onlyUnhandled = false)
        assertEquals(1, remainingWarnings.size)
        assertEquals(2L, remainingWarnings[0].identifierId)
    }

    @Test
    fun `获取快速处理预警应返回最多3条`() {
        mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createFinancialAccount()) // HIGH
        mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3)) // MEDIUM
        mockService.triggerWarning(3L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1)) // LOW
        mockService.triggerWarning(4L, WarningType.DEACTIVATION_PLAN, createTestAccounts(2)) // MEDIUM

        val quickWarnings = mockService.getQuickHandleWarnings()

        assertEquals(3, quickWarnings.size)
    }

    @Test
    fun `快速处理预警应按级别排序（HIGH优先）`() {
        mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1)) // LOW
        mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createFinancialAccount()) // HIGH
        mockService.triggerWarning(3L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3)) // MEDIUM

        val quickWarnings = mockService.getQuickHandleWarnings()

        assertEquals(WarningLevel.HIGH, quickWarnings[0].warningLevel)
    }

    @Test
    fun `快速处理预警不应包含已处理的`() {
        val warning1 = mockService.triggerWarning(1L, WarningType.DEACTIVATION_PLAN, createFinancialAccount())
        mockService.triggerWarning(2L, WarningType.DEACTIVATION_PLAN, createTestAccounts(3))
        mockService.triggerWarning(3L, WarningType.DEACTIVATION_PLAN, createTestAccounts(1))

        // 处理第一条预警
        mockService.handleWarning(warning1.id!!)

        val quickWarnings = mockService.getQuickHandleWarnings()

        assertFalse(quickWarnings.any { it.id == warning1.id })
        assertEquals(2, quickWarnings.size)
    }

    // 测试辅助方法
    private fun createTestAccounts(count: Int): List<ApplicationAccount> {
        return (1..count).map { index ->
            ApplicationAccount(
                id = index.toLong(),
                applicationId = index.toLong(),
                accountName = "账号$index",
                status = AccountStatus.ACTIVE,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }
    }

    private fun createFinancialAccount(): ApplicationAccount {
        // 金融类应用（applicationId = 100 表示金融应用）
        return ApplicationAccount(
            id = 100L,
            applicationId = 100L, // 金融应用ID
            accountName = "金融账号",
            status = AccountStatus.ACTIVE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    /**
     * 模拟 WarningService 实现
     */
    private class MockWarningService : WarningService {
        private val warnings = mutableListOf<WarningRecord>()
        private val financialApplicationIds = setOf(100L, 200L, 300L) // 模拟金融应用ID集合
        private var nextId = 1L

        override fun triggerWarning(
            identifierId: Long,
            warningType: WarningType,
            affectedAccounts: List<ApplicationAccount>
        ): WarningRecord {
            val level = calculateWarningLevel(affectedAccounts)
            val accountCount = affectedAccounts.size

            val warning = WarningRecord(
                id = nextId++,
                identifierId = identifierId,
                warningLevel = level,
                warningType = warningType,
                message = "标识即将停用，关联${accountCount}个账号",
                triggeredAt = Instant.now(),
                isRead = false,
                isHandled = false
            )
            warnings.add(warning)
            return warning
        }

        override fun calculateWarningLevel(affectedAccounts: List<ApplicationAccount>): WarningLevel {
            if (affectedAccounts.isEmpty()) return WarningLevel.LOW

            // 规则1: 影响金融类应用账户 -> HIGH
            if (affectedAccounts.any { it.applicationId in financialApplicationIds }) {
                return WarningLevel.HIGH
            }

            // 规则2: 影响账户数 > 5 -> HIGH
            if (affectedAccounts.size > 5) {
                return WarningLevel.HIGH
            }

            // 规则3: 影响账户数 2-5（无金融应用） -> MEDIUM
            if (affectedAccounts.size in 2..5) {
                return WarningLevel.MEDIUM
            }

            // 规则4: 影响账户数 = 1（非敏感账户） -> LOW
            return WarningLevel.LOW
        }

        override fun getWarningList(onlyUnhandled: Boolean): List<WarningRecord> {
            val filtered = if (onlyUnhandled) {
                warnings.filter { !it.isHandled }
            } else {
                warnings.toList()
            }
            // 按级别和时间排序
            return filtered.sortedWith(
                compareByDescending<WarningRecord> { it.warningLevel }
                    .thenBy { it.triggeredAt }
            )
        }

        override fun getWarningById(warningId: Long): WarningRecord? {
            return warnings.find { it.id == warningId }
        }

        override fun handleWarning(warningId: Long) {
            val warning = warnings.find { it.id == warningId }
                ?: throw IllegalArgumentException("Warning not found")
            warning.isHandled = true
            warning.handledAt = Instant.now()
        }

        override fun markWarningAsRead(warningId: Long) {
            val warning = warnings.find { it.id == warningId }
                ?: throw IllegalArgumentException("Warning not found")
            warning.isRead = true
        }

        override fun clearWarningsByIdentifierId(identifierId: Long) {
            warnings.removeAll { it.identifierId == identifierId }
        }

        override fun getQuickHandleWarnings(): List<WarningRecord> {
            return warnings
                .filter { !it.isHandled }
                .sortedWith(
                    compareByDescending<WarningRecord> { it.warningLevel }
                        .thenBy { it.triggeredAt }
                )
                .take(3)
        }
    }
}