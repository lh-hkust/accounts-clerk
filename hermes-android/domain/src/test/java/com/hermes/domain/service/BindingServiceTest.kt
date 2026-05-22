package com.hermes.domain.service

import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.valueobject.BindingPurpose
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

/**
 * BindingService 单元测试
 *
 * 测试绑定服务的核心业务逻辑
 */
class BindingServiceTest {

    private val mockService = MockBindingService()

    @Test
    fun `绑定标识到账户应成功`() {
        val accountId = 1L
        val identifierId = 100L
        val purposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION)

        val binding = mockService.bindIdentifier(
            accountId = accountId,
            identifierId = identifierId,
            purposes = purposes,
            isPrimary = true
        )

        assertNotNull(binding)
        assertEquals(accountId, binding.accountId)
        assertEquals(identifierId, binding.identifierId)
        assertEquals(purposes, binding.purposes)
        assertTrue(binding.isPrimary)
    }

    @Test
    fun `绑定时用途列表为空应抛出异常`() {
        try {
            mockService.bindIdentifier(
                accountId = 1L,
                identifierId = 100L,
                purposes = emptyList(),
                isPrimary = false
            )
            fail("Should throw IllegalArgumentException for empty purposes")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("empty"))
        }
    }

    @Test
    fun `重复绑定相同标识到账户应抛出异常`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        try {
            mockService.bindIdentifier(
                accountId = 1L,
                identifierId = 100L,
                purposes = listOf(BindingPurpose.VERIFICATION),
                isPrimary = false
            )
            fail("Should throw IllegalArgumentException for duplicate binding")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("already bound") || e.message!!.contains("duplicate"))
        }
    }

    @Test
    fun `解绑标识应成功`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        val history = mockService.unbindIdentifier(accountId = 1L, identifierId = 100L)

        assertNotNull(history)
        assertEquals(ActionType.UNBIND, history.actionType)
        assertEquals(1L, history.accountId)
        assertEquals(100L, history.identifierId)
    }

    @Test
    fun `解绑后绑定应不存在`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        mockService.unbindIdentifier(accountId = 1L, identifierId = 100L)

        val bindings = mockService.getBindingsByAccountId(1L)
        assertTrue(bindings.isEmpty())
    }

    @Test
    fun `修改绑定用途应成功`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        val newPurposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION)
        val history = mockService.changeBindingPurpose(
            accountId = 1L,
            identifierId = 100L,
            newPurposes = newPurposes
        )

        assertNotNull(history)
        assertEquals(ActionType.CHANGE_PURPOSE, history.actionType)
        assertEquals(listOf(BindingPurpose.LOGIN), history.previousPurposes)
        assertEquals(newPurposes, history.newPurposes)
    }

    @Test
    fun `修改绑定用途后用途应更新`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        val newPurposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION)
        mockService.changeBindingPurpose(
            accountId = 1L,
            identifierId = 100L,
            newPurposes = newPurposes
        )

        val binding = mockService.getBindingsByAccountId(1L).first()
        assertEquals(newPurposes, binding.purposes)
    }

    @Test
    fun `修改用途为空列表应抛出异常`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        try {
            mockService.changeBindingPurpose(
                accountId = 1L,
                identifierId = 100L,
                newPurposes = emptyList()
            )
            fail("Should throw IllegalArgumentException for empty purposes")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("empty"))
        }
    }

    @Test
    fun `更换绑定标识应成功`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        val history = mockService.switchBindingIdentifier(
            accountId = 1L,
            oldIdentifierId = 100L,
            newIdentifierId = 200L
        )

        assertNotNull(history)
        assertEquals(ActionType.SWITCH_IDENTIFIER, history.actionType)
        assertEquals(100L, history.previousIdentifierId)
        assertEquals(200L, history.newIdentifierId)
    }

    @Test
    fun `更换绑定标识后旧标识应解绑`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN),
            isPrimary = false
        )

        mockService.switchBindingIdentifier(
            accountId = 1L,
            oldIdentifierId = 100L,
            newIdentifierId = 200L
        )

        val bindings = mockService.getBindingsByAccountId(1L)
        assertEquals(1, bindings.size)
        assertEquals(200L, bindings.first().identifierId)
    }

    @Test
    fun `更换绑定标识应保留原用途`() {
        mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION),
            isPrimary = true
        )

        mockService.switchBindingIdentifier(
            accountId = 1L,
            oldIdentifierId = 100L,
            newIdentifierId = 200L
        )

        val binding = mockService.getBindingsByAccountId(1L).first()
        assertEquals(listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION), binding.purposes)
        assertTrue(binding.isPrimary)
    }

    @Test
    fun `获取账户绑定列表应返回正确数据`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), false)
        mockService.bindIdentifier(1L, 200L, listOf(BindingPurpose.VERIFICATION), false)

        val bindings = mockService.getBindingsByAccountId(1L)

        assertEquals(2, bindings.size)
        assertTrue(bindings.any { it.identifierId == 100L })
        assertTrue(bindings.any { it.identifierId == 200L })
    }

    @Test
    fun `获取标识绑定列表应返回正确数据`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), false)
        mockService.bindIdentifier(2L, 100L, listOf(BindingPurpose.VERIFICATION), false)
        mockService.bindIdentifier(1L, 200L, listOf(BindingPurpose.LOGIN), false)

        val bindings = mockService.getBindingsByIdentifierId(100L)

        assertEquals(2, bindings.size)
        assertTrue(bindings.any { it.accountId == 1L })
        assertTrue(bindings.any { it.accountId == 2L })
    }

    @Test
    fun `创建绑定历史记录应成功`() {
        val history = mockService.createHistoryRecord(
            accountId = 1L,
            identifierId = 100L,
            actionType = ActionType.BIND,
            previousPurposes = null,
            newPurposes = listOf(BindingPurpose.LOGIN),
            previousIdentifierId = null,
            newIdentifierId = null
        )

        assertNotNull(history)
        assertEquals(1L, history.accountId)
        assertEquals(100L, history.identifierId)
        assertEquals(ActionType.BIND, history.actionType)
        assertEquals(listOf(BindingPurpose.LOGIN), history.newPurposes)
    }

    @Test
    fun `获取绑定历史记录应返回正确数据`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), false)
        mockService.changeBindingPurpose(1L, 100L, listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION))

        val histories = mockService.getBindingHistory(accountId = 1L)

        assertEquals(2, histories.size)
        assertEquals(ActionType.BIND, histories[0].actionType)
        assertEquals(ActionType.CHANGE_PURPOSE, histories[1].actionType)
    }

    @Test
    fun `按标识ID获取历史记录应返回正确数据`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), false)
        mockService.bindIdentifier(2L, 100L, listOf(BindingPurpose.VERIFICATION), false)
        mockService.bindIdentifier(1L, 200L, listOf(BindingPurpose.LOGIN), false)

        val histories = mockService.getBindingHistory(identifierId = 100L)

        assertEquals(2, histories.size)
        assertTrue(histories.all { it.identifierId == 100L })
    }

    @Test
    fun `同一账户可绑定多个不同标识`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), true)
        mockService.bindIdentifier(1L, 200L, listOf(BindingPurpose.VERIFICATION), false)

        val bindings = mockService.getBindingsByAccountId(1L)

        assertEquals(2, bindings.size)
    }

    @Test
    fun `同一标识可绑定到多个不同账户`() {
        mockService.bindIdentifier(1L, 100L, listOf(BindingPurpose.LOGIN), false)
        mockService.bindIdentifier(2L, 100L, listOf(BindingPurpose.VERIFICATION), false)

        val bindings = mockService.getBindingsByIdentifierId(100L)

        assertEquals(2, bindings.size)
    }

    @Test
    fun `绑定用途应支持所有类型`() {
        val allPurposes = listOf(
            BindingPurpose.LOGIN,
            BindingPurpose.VERIFICATION,
            BindingPurpose.RECOVERY,
            BindingPurpose.NOTIFICATION,
            BindingPurpose.SECONDARY_AUTH
        )

        val binding = mockService.bindIdentifier(
            accountId = 1L,
            identifierId = 100L,
            purposes = allPurposes,
            isPrimary = false
        )

        assertEquals(5, binding.purposes.size)
        assertTrue(binding.purposes.containsAll(allPurposes))
    }

    /**
     * 模拟 BindingService 实现
     */
    private class MockBindingService : BindingService {
        private val bindings = mutableListOf<IdentifierBinding>()
        private val histories = mutableListOf<BindingHistoryRecord>()
        private var nextBindingId = 1L
        private var nextHistoryId = 1L

        override fun bindIdentifier(
            accountId: Long,
            identifierId: Long,
            purposes: List<BindingPurpose>,
            isPrimary: Boolean
        ): IdentifierBinding {
            if (purposes.isEmpty()) {
                throw IllegalArgumentException("Binding purposes must not be empty")
            }

            // 检查是否已存在绑定
            if (bindings.any { it.accountId == accountId && it.identifierId == identifierId }) {
                throw IllegalArgumentException("Identifier already bound to this account")
            }

            val binding = IdentifierBinding(
                id = nextBindingId++,
                accountId = accountId,
                identifierId = identifierId,
                purposes = purposes,
                isPrimary = isPrimary,
                boundAt = Instant.now()
            )
            bindings.add(binding)

            // 记录历史
            createHistoryRecord(
                accountId = accountId,
                identifierId = identifierId,
                actionType = ActionType.BIND,
                newPurposes = purposes
            )

            return binding
        }

        override fun unbindIdentifier(accountId: Long, identifierId: Long): BindingHistoryRecord {
            val binding = bindings.find { it.accountId == accountId && it.identifierId == identifierId }
                ?: throw IllegalArgumentException("Binding not found")

            val history = createHistoryRecord(
                accountId = accountId,
                identifierId = identifierId,
                actionType = ActionType.UNBIND,
                previousPurposes = binding.purposes
            )

            bindings.removeAll { it.accountId == accountId && it.identifierId == identifierId }

            return history
        }

        override fun changeBindingPurpose(
            accountId: Long,
            identifierId: Long,
            newPurposes: List<BindingPurpose>
        ): BindingHistoryRecord {
            if (newPurposes.isEmpty()) {
                throw IllegalArgumentException("New purposes must not be empty")
            }

            val binding = bindings.find { it.accountId == accountId && it.identifierId == identifierId }
                ?: throw IllegalArgumentException("Binding not found")

            val previousPurposes = binding.purposes.toList()
            binding.updatePurposes(newPurposes)

            return createHistoryRecord(
                accountId = accountId,
                identifierId = identifierId,
                actionType = ActionType.CHANGE_PURPOSE,
                previousPurposes = previousPurposes,
                newPurposes = newPurposes
            )
        }

        override fun switchBindingIdentifier(
            accountId: Long,
            oldIdentifierId: Long,
            newIdentifierId: Long
        ): BindingHistoryRecord {
            val oldBinding = bindings.find { it.accountId == accountId && it.identifierId == oldIdentifierId }
                ?: throw IllegalArgumentException("Old binding not found")

            val previousPurposes = oldBinding.purposes.toList()
            val wasPrimary = oldBinding.isPrimary

            // 解绑旧标识
            bindings.removeAll { it.accountId == accountId && it.identifierId == oldIdentifierId }

            // 绑定新标识（保留原用途）
            val newBinding = IdentifierBinding(
                id = nextBindingId++,
                accountId = accountId,
                identifierId = newIdentifierId,
                purposes = previousPurposes,
                isPrimary = wasPrimary,
                boundAt = Instant.now()
            )
            bindings.add(newBinding)

            return createHistoryRecord(
                accountId = accountId,
                identifierId = newIdentifierId,
                actionType = ActionType.SWITCH_IDENTIFIER,
                previousPurposes = previousPurposes,
                newPurposes = previousPurposes,
                previousIdentifierId = oldIdentifierId,
                newIdentifierId = newIdentifierId
            )
        }

        override fun getBindingsByAccountId(accountId: Long): List<IdentifierBinding> {
            return bindings.filter { it.accountId == accountId }
        }

        override fun getBindingsByIdentifierId(identifierId: Long): List<IdentifierBinding> {
            return bindings.filter { it.identifierId == identifierId }
        }

        override fun createHistoryRecord(
            accountId: Long,
            identifierId: Long,
            actionType: ActionType,
            previousPurposes: List<BindingPurpose>?,
            newPurposes: List<BindingPurpose>?,
            previousIdentifierId: Long?,
            newIdentifierId: Long?
        ): BindingHistoryRecord {
            val history = BindingHistoryRecord(
                id = nextHistoryId++,
                accountId = accountId,
                identifierId = identifierId,
                actionType = actionType,
                previousPurposes = previousPurposes,
                newPurposes = newPurposes,
                previousIdentifierId = previousIdentifierId,
                newIdentifierId = newIdentifierId,
                actionAt = Instant.now()
            )
            histories.add(history)
            return history
        }

        override fun getBindingHistory(accountId: Long?, identifierId: Long?): List<BindingHistoryRecord> {
            return histories.filter { history ->
                (accountId == null || history.accountId == accountId) &&
                (identifierId == null || history.identifierId == identifierId)
            }
        }
    }
}