package com.hermes.domain.model

import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * IdentityIdentifier 单元测试
 */
class IdentityIdentifierTest {

    @Test
    fun `创建身份标识应成功`() {
        val now = Instant.now()
        val identifier = IdentityIdentifier(
            id = 1L,
            type = IdentifierType.PHONE,
            value = "13800138000",
            status = IdentifierStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(1L, identifier.id)
        assertEquals(IdentifierType.PHONE, identifier.type)
        assertEquals("13800138000", identifier.value)
        assertEquals(IdentifierStatus.ACTIVE, identifier.status)
    }

    @Test
    fun `创建邮箱标识应成功`() {
        val now = Instant.now()
        val identifier = IdentityIdentifier(
            id = 2L,
            type = IdentifierType.EMAIL,
            value = "test@example.com",
            status = IdentifierStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(IdentifierType.EMAIL, identifier.type)
        assertEquals("test@example.com", identifier.value)
    }

    @Test
    fun `设置停用计划应更新状态`() {
        val now = Instant.now()
        val plannedTime = now.plusSeconds(30 * 24 * 60 * 60) // 30天后
        val identifier = IdentityIdentifier(
            id = 1L,
            type = IdentifierType.PHONE,
            value = "13800138000",
            status = IdentifierStatus.PENDING_DEACTIVATION,
            plannedDeactTime = plannedTime,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(IdentifierStatus.PENDING_DEACTIVATION, identifier.status)
        assertEquals(plannedTime, identifier.plannedDeactTime)
    }

    @Test
    fun `标识停用后状态应为DEACTIVATED`() {
        val now = Instant.now()
        val identifier = IdentityIdentifier(
            id = 1L,
            type = IdentifierType.PHONE,
            value = "13800138000",
            status = IdentifierStatus.DEACTIVATED,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(IdentifierStatus.DEACTIVATED, identifier.status)
        assertNull(identifier.plannedDeactTime)
    }

    @Test
    fun `标识失效后状态应为INVALIDATED`() {
        val now = Instant.now()
        val identifier = IdentityIdentifier(
            id = 1L,
            type = IdentifierType.EMAIL,
            value = "lost@example.com",
            status = IdentifierStatus.INVALIDATED,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(IdentifierStatus.INVALIDATED, identifier.status)
    }
}