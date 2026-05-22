package com.hermes.domain.service

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * IdentifierService 单元测试
 *
 * 测试身份标识服务的核心业务逻辑
 */
class IdentifierServiceTest {

    // 模拟 IdentifierService 实现
    private val mockService = MockIdentifierService()

    @Test
    fun `创建手机号标识应成功`() {
        val identifier = mockService.createIdentifier(
            IdentifierType.PHONE,
            "13800138000"
        )

        assertNotNull(identifier)
        assertEquals(IdentifierType.PHONE, identifier.type)
        assertEquals("13800138000", identifier.value)
        assertEquals(IdentifierStatus.ACTIVE, identifier.status)
    }

    @Test
    fun `创建邮箱标识应成功`() {
        val identifier = mockService.createIdentifier(
            IdentifierType.EMAIL,
            "test@example.com"
        )

        assertNotNull(identifier)
        assertEquals(IdentifierType.EMAIL, identifier.type)
        assertEquals("test@example.com", identifier.value)
        assertEquals(IdentifierStatus.ACTIVE, identifier.status)
    }

    @Test
    fun `重复标识检测应返回true`() {
        // 先创建一个标识
        mockService.createIdentifier(IdentifierType.PHONE, "13800138000")

        // 检测相同标识
        val isDuplicate = mockService.checkDuplicate(IdentifierType.PHONE, "13800138000")

        assertTrue(isDuplicate)
    }

    @Test
    fun `非重复标识检测应返回false`() {
        // 先创建一个标识
        mockService.createIdentifier(IdentifierType.PHONE, "13800138000")

        // 检测不同标识
        val isDuplicate = mockService.checkDuplicate(IdentifierType.PHONE, "13800138001")

        assertFalse(isDuplicate)
    }

    @Test
    fun `创建重复标识应抛出异常`() {
        // 先创建一个标识
        mockService.createIdentifier(IdentifierType.PHONE, "13800138000")

        // 再次创建相同标识
        try {
            mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
            fail("Should throw IllegalArgumentException for duplicate identifier")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("duplicate"))
        }
    }

    @Test
    fun `获取标识详情应返回正确数据`() {
        val created = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        val retrieved = mockService.getIdentifierById(created.id!!)

        assertNotNull(retrieved)
        assertEquals(created.id, retrieved!!.id)
        assertEquals(created.type, retrieved.type)
        assertEquals(created.value, retrieved.value)
    }

    @Test
    fun `获取不存在的标识应返回null`() {
        val retrieved = mockService.getIdentifierById(999L)
        assertNull(retrieved)
    }

    @Test
    fun `获取所有标识列表应返回已创建的所有标识`() {
        mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.createIdentifier(IdentifierType.EMAIL, "test@example.com")

        val list = mockService.getAllIdentifiers()

        assertEquals(2, list.size)
    }

    @Test
    fun `删除无绑定标识应成功`() {
        val created = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")

        mockService.deleteIdentifier(created.id!!)

        assertNull(mockService.getIdentifierById(created.id!!))
    }

    @Test
    fun `删除有绑定标识应抛出异常`() {
        val created = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.simulateBinding(created.id!!, 2) // 模拟绑定2个账户

        try {
            mockService.deleteIdentifier(created.id!!)
            fail("Should throw IllegalArgumentException for identifier with bindings")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("binding"))
        }
    }

    @Test
    fun `获取绑定账户数量应返回正确值`() {
        val created = mockService.createIdentifier(IdentifierType.PHONE, "13800138000")
        mockService.simulateBinding(created.id!!, 3)

        val count = mockService.getBoundAccountCount(created.id!!)

        assertEquals(3, count)
    }

    /**
     * 模拟 IdentifierService 实现
     * 用于单元测试，不依赖真实 Repository
     */
    private class MockIdentifierService : IdentifierService {
        private val identifiers = mutableListOf<IdentityIdentifier>()
        private val bindingCounts = mutableMapOf<Long, Int>()
        private var nextId = 1L

        override fun createIdentifier(type: IdentifierType, value: String): IdentityIdentifier {
            if (checkDuplicate(type, value)) {
                throw IllegalArgumentException("Identifier already exists: duplicate $type value")
            }
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
            bindingCounts[identifier.id!!] = 0
            return identifier
        }

        override fun checkDuplicate(type: IdentifierType, value: String): Boolean {
            return identifiers.any { it.type == type && it.value == value }
        }

        override fun getBoundAccountCount(identifierId: Long): Int {
            return bindingCounts[identifierId] ?: 0
        }

        override fun getIdentifierById(identifierId: Long): IdentityIdentifier? {
            return identifiers.find { it.id == identifierId }
        }

        override fun getAllIdentifiers(): List<IdentityIdentifier> {
            return identifiers.toList()
        }

        override fun deleteIdentifier(identifierId: Long) {
            val count = getBoundAccountCount(identifierId)
            if (count > 0) {
                throw IllegalArgumentException("Cannot delete identifier with $count binding accounts")
            }
            identifiers.removeAll { it.id == identifierId }
            bindingCounts.remove(identifierId)
        }

        // 测试辅助方法：模拟绑定
        fun simulateBinding(identifierId: Long, count: Int) {
            bindingCounts[identifierId] = count
        }
    }
}