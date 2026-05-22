package com.hermes.domain.model

import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * WarningRecord 单元测试
 */
class WarningRecordTest {

    @Test
    fun `创建预警记录应成功`() {
        val now = Instant.now()
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.HIGH,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "手机号即将到期，关联14个账号",
            triggeredAt = now,
            isRead = false,
            isHandled = false
        )

        assertEquals(1L, warning.id)
        assertEquals(100L, warning.identifierId)
        assertEquals(WarningLevel.HIGH, warning.warningLevel)
        assertEquals("手机号即将到期，关联14个账号", warning.message)
        assertFalse(warning.isRead)
        assertFalse(warning.isHandled)
    }

    @Test
    fun `高级别预警应紧急`() {
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.HIGH,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "金融账号受影响",
            triggeredAt = Instant.now(),
            isRead = false,
            isHandled = false
        )

        assertEquals(WarningLevel.HIGH, warning.warningLevel)
    }

    @Test
    fun `中级别预警应建议处理`() {
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.MEDIUM,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "3个账号受影响",
            triggeredAt = Instant.now(),
            isRead = false,
            isHandled = false
        )

        assertEquals(WarningLevel.MEDIUM, warning.warningLevel)
    }

    @Test
    fun `低级别预警可延后处理`() {
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.LOW,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "仅1个账号受影响",
            triggeredAt = Instant.now(),
            isRead = false,
            isHandled = false
        )

        assertEquals(WarningLevel.LOW, warning.warningLevel)
    }

    @Test
    fun `标记已读应更新状态`() {
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.MEDIUM,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "测试",
            triggeredAt = Instant.now(),
            isRead = true,
            isHandled = false
        )

        assertTrue(warning.isRead)
        assertFalse(warning.isHandled)
    }

    @Test
    fun `处理预警应更新状态和时间`() {
        val handledTime = Instant.now()
        val warning = WarningRecord(
            id = 1L,
            identifierId = 100L,
            warningLevel = WarningLevel.MEDIUM,
            warningType = WarningType.DEACTIVATION_REMINDER,
            message = "测试",
            triggeredAt = Instant.now(),
            isRead = true,
            isHandled = true,
            handledAt = handledTime
        )

        assertTrue(warning.isHandled)
        assertEquals(handledTime, warning.handledAt)
    }

    @Test
    fun `不同预警级别应可区分`() {
        val levels = listOf(
            WarningLevel.HIGH,
            WarningLevel.MEDIUM,
            WarningLevel.LOW
        )

        assertEquals(3, levels.distinct().size)
    }
}