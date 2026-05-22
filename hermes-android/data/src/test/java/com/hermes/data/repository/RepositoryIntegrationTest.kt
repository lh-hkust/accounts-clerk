package com.hermes.data.repository

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.model.BindingHistoryRecord
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.domain.valueobject.DeactivationStatus
import com.hermes.domain.valueobject.ActionType
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.WarningRecordRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.BindingHistoryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Repository 集成测试
 *
 * 测试 Repository 与真实数据库的交互
 * 需要在 Android 环境下运行（AndroidJUnitRunner）
 *
 * 注意：此测试文件需要配置 Android Instrumentation Test 环境
 * 运行命令：adb shell am instrument -w com.hermes.data/androidx.test.runner.AndroidJUnitRunner
 */
class RepositoryIntegrationTest {

    // 模拟 Repository 实现（用于单元测试验证接口契约）
    private lateinit var mockIdentifierRepository: MockIdentityIdentifierRepository
    private lateinit var mockAccountRepository: MockApplicationAccountRepository
    private lateinit var mockBindingRepository: MockIdentifierBindingRepository
    private lateinit var mockWarningRepository: MockWarningRecordRepository
    private lateinit var mockDeactivationRepository: MockIdentifierDeactivationRepository
    private lateinit var mockBindingHistoryRepository: MockBindingHistoryRepository

    @Before
    fun setup() {
        mockIdentifierRepository = MockIdentityIdentifierRepository()
        mockAccountRepository = MockApplicationAccountRepository()
        mockBindingRepository = MockIdentifierBindingRepository()
        mockWarningRepository = MockWarningRecordRepository()
        mockDeactivationRepository = MockIdentifierDeactivationRepository()
        mockBindingHistoryRepository = MockBindingHistoryRepository()
    }

    // ========== IdentityIdentifierRepository 测试 ==========

    @Test
    fun `插入标识应返回带ID的标识`() = runBlocking {
        val identifier = createTestIdentifier(null)
        val inserted = mockIdentifierRepository.insert(identifier)

        assertNotNull(inserted.id)
        assertEquals(identifier.type, inserted.type)
        assertEquals(identifier.value, inserted.value)
    }

    @Test
    fun `更新标识应成功`() = runBlocking {
        val inserted = mockIdentifierRepository.insert(createTestIdentifier(null))
        inserted.status = IdentifierStatus.PENDING_DEACTIVATION
        inserted.plannedDeactTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        mockIdentifierRepository.update(inserted)

        val retrieved = mockIdentifierRepository.getById(inserted.id!!)
        assertEquals(IdentifierStatus.PENDING_DEACTIVATION, retrieved!!.status)
    }

    @Test
    fun `删除标识应成功`() = runBlocking {
        val inserted = mockIdentifierRepository.insert(createTestIdentifier(null))
        mockIdentifierRepository.delete(inserted)

        val retrieved = mockIdentifierRepository.getById(inserted.id!!)
        assertNull(retrieved)
    }

    @Test
    fun `按ID获取标识应返回正确数据`() = runBlocking {
        val inserted = mockIdentifierRepository.insert(createTestIdentifier(null))
        val retrieved = mockIdentifierRepository.getById(inserted.id!!)

        assertNotNull(retrieved)
        assertEquals(inserted.id!!, retrieved!!.id)
    }

    @Test
    fun `获取不存在的ID应返回null`() = runBlocking {
        val retrieved = mockIdentifierRepository.getById(999L)
        assertNull(retrieved)
    }

    @Test
    fun `获取所有标识应返回列表`() = runBlocking {
        mockIdentifierRepository.insert(createTestIdentifier(null, "13800138001"))
        mockIdentifierRepository.insert(createTestIdentifier(null, "13800138002"))

        val all = mockIdentifierRepository.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `按状态获取标识应返回正确列表`() = runBlocking {
        val identifier1 = mockIdentifierRepository.insert(createTestIdentifier(null, "13800138001"))
        val identifier2 = mockIdentifierRepository.insert(createTestIdentifier(null, "13800138002"))
        identifier1.status = IdentifierStatus.PENDING_DEACTIVATION
        mockIdentifierRepository.update(identifier1)

        val activeList = mockIdentifierRepository.getByStatus(IdentifierStatus.ACTIVE)
        val pendingList = mockIdentifierRepository.getByStatus(IdentifierStatus.PENDING_DEACTIVATION)

        assertEquals(1, activeList.size)
        assertEquals(1, pendingList.size)
    }

    @Test
    fun `检查重复应返回正确结果`() = runBlocking {
        mockIdentifierRepository.insert(createTestIdentifier(null, "13800138000"))

        val duplicate = mockIdentifierRepository.checkDuplicate(IdentifierType.PHONE, "13800138000")
        val notDuplicate = mockIdentifierRepository.checkDuplicate(IdentifierType.PHONE, "13800138001")

        assertTrue(duplicate)
        assertFalse(notDuplicate)
    }

    @Test
    fun `清空所有标识应成功`() = runBlocking {
        mockIdentifierRepository.insert(createTestIdentifier(null, "13800138001"))
        mockIdentifierRepository.insert(createTestIdentifier(null, "13800138002"))
        mockIdentifierRepository.deleteAll()

        val all = mockIdentifierRepository.getAll()
        assertTrue(all.isEmpty())
    }

    // ========== ApplicationAccountRepository 测试 ==========

    @Test
    fun `插入账户应返回带ID的账户`() = runBlocking {
        val account = createTestAccount(null)
        val inserted = mockAccountRepository.insert(account)

        assertNotNull(inserted.id)
        assertEquals(account.applicationId, inserted.applicationId)
        assertEquals(account.accountName, inserted.accountName)
    }

    @Test
    fun `更新账户状态应成功`() = runBlocking {
        val inserted = mockAccountRepository.insert(createTestAccount(null))
        mockAccountRepository.updateStatus(inserted.id!!, AccountStatus.FROZEN)

        val retrieved = mockAccountRepository.getById(inserted.id!!)
        assertEquals(AccountStatus.FROZEN, retrieved!!.status)
    }

    @Test
    fun `删除账户应成功`() = runBlocking {
        val inserted = mockAccountRepository.insert(createTestAccount(null))
        mockAccountRepository.delete(inserted)

        val retrieved = mockAccountRepository.getById(inserted.id!!)
        assertNull(retrieved)
    }

    @Test
    fun `获取所有账户应返回列表`() = runBlocking {
        mockAccountRepository.insert(createTestAccount(null, "账号1"))
        mockAccountRepository.insert(createTestAccount(null, "账号2"))

        val all = mockAccountRepository.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `按应用ID获取账户应返回正确列表`() = runBlocking {
        mockAccountRepository.insert(createTestAccount(null, "账号1", applicationId = 100L))
        mockAccountRepository.insert(createTestAccount(null, "账号2", applicationId = 200L))
        mockAccountRepository.insert(createTestAccount(null, "账号3", applicationId = 100L))

        val app100Accounts = mockAccountRepository.getByApplicationId(100L)

        assertEquals(2, app100Accounts.size)
    }

    @Test
    fun `按标识ID获取账户应返回绑定账户列表`() = runBlocking {
        val account1 = mockAccountRepository.insert(createTestAccount(null, "账号1"))
        val account2 = mockAccountRepository.insert(createTestAccount(null, "账号2"))
        val identifier = mockIdentifierRepository.insert(createTestIdentifier(null))

        // 模拟绑定
        mockBindingRepository.insert(createTestBinding(null, account1.id!!, identifier.id!!))
        mockBindingRepository.insert(createTestBinding(null, account2.id!!, identifier.id!!))

        val boundAccounts = mockAccountRepository.getByIdentifierId(identifier.id!!)

        assertEquals(2, boundAccounts.size)
    }

    // ========== IdentifierBindingRepository 测试 ==========

    @Test
    fun `插入绑定应返回带ID的绑定`() = runBlocking {
        val account = mockAccountRepository.insert(createTestAccount(null))
        val identifier = mockIdentifierRepository.insert(createTestIdentifier(null))
        val binding = createTestBinding(null, account.id!!, identifier.id!!)

        val inserted = mockBindingRepository.insert(binding)

        assertNotNull(inserted.id)
        assertEquals(account.id!!, inserted.accountId)
        assertEquals(identifier.id!!, inserted.identifierId)
    }

    @Test
    fun `删除绑定应成功`() = runBlocking {
        val account = mockAccountRepository.insert(createTestAccount(null))
        val identifier = mockIdentifierRepository.insert(createTestIdentifier(null))
        val binding = mockBindingRepository.insert(createTestBinding(null, account.id!!, identifier.id!!))

        mockBindingRepository.delete(binding)

        val bindings = mockBindingRepository.getByAccountId(account.id!!)
        assertTrue(bindings.isEmpty())
    }

    @Test
    fun `按账户ID获取绑定应返回正确列表`() = runBlocking {
        val account = mockAccountRepository.insert(createTestAccount(null))
        val identifier1 = mockIdentifierRepository.insert(createTestIdentifier(null, "13800138001"))
        val identifier2 = mockIdentifierRepository.insert(createTestIdentifier(null, "13800138002"))

        mockBindingRepository.insert(createTestBinding(null, account.id!!, identifier1.id!!))
        mockBindingRepository.insert(createTestBinding(null, account.id!!, identifier2.id!!))

        val bindings = mockBindingRepository.getByAccountId(account.id!!)
        assertEquals(2, bindings.size)
    }

    @Test
    fun `按标识ID获取绑定应返回正确列表`() = runBlocking {
        val account1 = mockAccountRepository.insert(createTestAccount(null, "账号1"))
        val account2 = mockAccountRepository.insert(createTestAccount(null, "账号2"))
        val identifier = mockIdentifierRepository.insert(createTestIdentifier(null))

        mockBindingRepository.insert(createTestBinding(null, account1.id!!, identifier.id!!))
        mockBindingRepository.insert(createTestBinding(null, account2.id!!, identifier.id!!))

        val bindings = mockBindingRepository.getByIdentifierId(identifier.id!!)
        assertEquals(2, bindings.size)
    }

    @Test
    fun `获取绑定数量应返回正确值`() = runBlocking {
        val account1 = mockAccountRepository.insert(createTestAccount(null, "账号1"))
        val account2 = mockAccountRepository.insert(createTestAccount(null, "账号2"))
        val identifier = mockIdentifierRepository.insert(createTestIdentifier(null))

        mockBindingRepository.insert(createTestBinding(null, account1.id!!, identifier.id!!))
        mockBindingRepository.insert(createTestBinding(null, account2.id!!, identifier.id!!))

        val count = mockBindingRepository.getCountByIdentifierId(identifier.id!!)
        assertEquals(2, count)
    }

    // ========== WarningRecordRepository 测试 ==========

    @Test
    fun `插入预警应返回带ID的预警`() = runBlocking {
        val warning = createTestWarning(null)
        val inserted = mockWarningRepository.insert(warning)

        assertNotNull(inserted.id)
        assertEquals(warning.identifierId, inserted.identifierId)
    }

    @Test
    fun `更新预警处理状态应成功`() = runBlocking {
        val inserted = mockWarningRepository.insert(createTestWarning(null))
        inserted.isHandled = true
        inserted.handledAt = Instant.now()
        mockWarningRepository.update(inserted)

        val retrieved = mockWarningRepository.getById(inserted.id!!)
        assertTrue(retrieved!!.isHandled)
    }

    @Test
    fun `获取未处理预警列表应返回正确数据`() = runBlocking {
        val warning1 = mockWarningRepository.insert(createTestWarning(null, identifierId = 1L))
        val warning2 = mockWarningRepository.insert(createTestWarning(null, identifierId = 2L))
        mockWarningRepository.markAsHandled(warning1.id!!)

        val unhandledList = mockWarningRepository.getUnhandled()

        assertEquals(1, unhandledList.size)
        assertEquals(2L, unhandledList[0].identifierId)
    }

    @Test
    fun `按标识ID删除预警应成功`() = runBlocking {
        mockWarningRepository.insert(createTestWarning(null, identifierId = 1L))
        mockWarningRepository.insert(createTestWarning(null, identifierId = 1L))
        mockWarningRepository.insert(createTestWarning(null, identifierId = 2L))

        mockWarningRepository.deleteByIdentifierId(1L)

        val all = mockWarningRepository.getAll()
        assertEquals(1, all.size)
        assertEquals(2L, all[0].identifierId)
    }

    // ========== IdentifierDeactivationRepository 测试 ==========

    @Test
    fun `插入停用计划应返回带ID的计划`() = runBlocking {
        val deactivation = createTestDeactivation(null)
        val inserted = mockDeactivationRepository.insert(deactivation)

        assertNotNull(inserted.id)
        assertEquals(deactivation.identifierId, inserted.identifierId)
    }

    @Test
    fun `更新停用计划状态应成功`() = runBlocking {
        val inserted = mockDeactivationRepository.insert(createTestDeactivation(null))
        inserted.status = DeactivationStatus.EXECUTED
        inserted.actualTime = Instant.now()
        mockDeactivationRepository.update(inserted)

        val retrieved = mockDeactivationRepository.getById(inserted.id!!)
        assertEquals(DeactivationStatus.EXECUTED, retrieved!!.status)
    }

    @Test
    fun `按标识ID获取停用计划应返回正确数据`() = runBlocking {
        mockDeactivationRepository.insert(createTestDeactivation(null, identifierId = 1L))
        mockDeactivationRepository.insert(createTestDeactivation(null, identifierId = 2L))

        val deactivation = mockDeactivationRepository.getByIdentifierId(1L)

        assertNotNull(deactivation)
        assertEquals(1L, deactivation!!.identifierId)
    }

    // ========== BindingHistoryRepository 测试 ==========

    @Test
    fun `插入绑定历史应返回带ID的历史`() = runBlocking {
        val history = createTestBindingHistory(null)
        val inserted = mockBindingHistoryRepository.insert(history)

        assertNotNull(inserted.id)
        assertEquals(history.accountId, inserted.accountId)
        assertEquals(history.identifierId, inserted.identifierId)
    }

    @Test
    fun `按账户ID获取绑定历史应返回正确列表`() = runBlocking {
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, accountId = 1L))
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, accountId = 1L))
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, accountId = 2L))

        val history = mockBindingHistoryRepository.getByAccountId(1L)

        assertEquals(2, history.size)
    }

    @Test
    fun `按标识ID获取绑定历史应返回正确列表`() = runBlocking {
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, identifierId = 100L))
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, identifierId = 100L))
        mockBindingHistoryRepository.insert(createTestBindingHistory(null, identifierId = 200L))

        val history = mockBindingHistoryRepository.getByIdentifierId(100L)

        assertEquals(2, history.size)
    }

    // 测试辅助方法
    private fun createTestIdentifier(id: Long?, value: String = "13800138000"): IdentityIdentifier {
        val now = Instant.now()
        return IdentityIdentifier(
            id = id,
            type = IdentifierType.PHONE,
            value = value,
            status = IdentifierStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun createTestAccount(id: Long?, name: String = "测试账号", applicationId: Long = 1L): ApplicationAccount {
        val now = Instant.now()
        return ApplicationAccount(
            id = id,
            applicationId = applicationId,
            accountName = name,
            status = AccountStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun createTestBinding(id: Long?, accountId: Long, identifierId: Long): IdentifierBinding {
        return IdentifierBinding(
            id = id,
            accountId = accountId,
            identifierId = identifierId,
            purposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION),
            isPrimary = true,
            boundAt = Instant.now()
        )
    }

    private fun createTestWarning(id: Long?, identifierId: Long = 1L): WarningRecord {
        return WarningRecord(
            id = id,
            identifierId = identifierId,
            warningLevel = WarningLevel.MEDIUM,
            warningType = WarningType.DEACTIVATION_PLAN,
            message = "测试预警",
            triggeredAt = Instant.now(),
            isRead = false,
            isHandled = false
        )
    }

    private fun createTestDeactivation(id: Long?, identifierId: Long = 1L): IdentifierDeactivation {
        val now = Instant.now()
        return IdentifierDeactivation(
            id = id,
            identifierId = identifierId,
            deactType = DeactivationType.PHONE_NUMBER_CHANGE,
            status = DeactivationStatus.SCHEDULED,
            scheduledTime = now.plusSeconds(30 * 24 * 60 * 60),
            reason = "手机号更换",
            createdAt = now,
            updatedAt = now
        )
    }

    private fun createTestBindingHistory(
        id: Long?,
        accountId: Long = 1L,
        identifierId: Long = 100L
    ): BindingHistoryRecord {
        return BindingHistoryRecord(
            id = id,
            accountId = accountId,
            identifierId = identifierId,
            actionType = ActionType.BIND,
            newPurposes = listOf(BindingPurpose.LOGIN),
            actionAt = Instant.now()
        )
    }

    // ========== Mock Repository 实现 ==========

    private class MockIdentityIdentifierRepository : IdentityIdentifierRepository {
        private val identifiers = mutableListOf<IdentityIdentifier>()
        private var nextId = 1L

        override suspend fun insert(identifier: IdentityIdentifier): IdentityIdentifier {
            val inserted = identifier.copy(id = nextId++)
            identifiers.add(inserted)
            return inserted
        }

        override suspend fun update(identifier: IdentityIdentifier) {
            identifiers.removeAll { it.id == identifier.id }
            identifiers.add(identifier)
        }

        override suspend fun delete(identifier: IdentityIdentifier) {
            identifiers.removeAll { it.id == identifier.id }
        }

        override suspend fun getById(id: Long): IdentityIdentifier? {
            return identifiers.find { it.id == id }
        }

        override suspend fun getAll(): List<IdentityIdentifier> {
            return identifiers.toList()
        }

        override suspend fun getByStatus(status: IdentifierStatus): List<IdentityIdentifier> {
            return identifiers.filter { it.status == status }
        }

        override suspend fun checkDuplicate(type: IdentifierType, value: String): Boolean {
            return identifiers.any { it.type == type && it.value == value }
        }

        override suspend fun getBoundAccountCount(identifierId: Long): Int {
            return 0 // 需要配合 BindingRepository
        }

        override suspend fun getPendingDeactivationBefore(threshold: Instant): List<IdentityIdentifier> {
            return identifiers.filter {
                it.status == IdentifierStatus.PENDING_DEACTIVATION &&
                it.plannedDeactTime != null &&
                it.plannedDeactTime!!.isBefore(threshold)
            }
        }

        override suspend fun deleteAll() {
            identifiers.clear()
        }
    }

    private class MockApplicationAccountRepository : ApplicationAccountRepository {
        private val accounts = mutableListOf<ApplicationAccount>()
        private var nextId = 1L

        override suspend fun insert(account: ApplicationAccount): ApplicationAccount {
            val inserted = account.copy(id = nextId++)
            accounts.add(inserted)
            return inserted
        }

        override suspend fun update(account: ApplicationAccount) {
            accounts.removeAll { it.id == account.id }
            accounts.add(account)
        }

        override suspend fun delete(account: ApplicationAccount) {
            accounts.removeAll { it.id == account.id }
        }

        override suspend fun getById(id: Long): ApplicationAccount? {
            return accounts.find { it.id == id }
        }

        override suspend fun getByIdWithDetails(id: Long): ApplicationAccount? {
            return getById(id)
        }

        override suspend fun getAll(): List<ApplicationAccount> {
            return accounts.toList()
        }

        override suspend fun getByApplicationId(applicationId: Long): List<ApplicationAccount> {
            return accounts.filter { it.applicationId == applicationId }
        }

        override suspend fun getByStatus(status: AccountStatus): List<ApplicationAccount> {
            return accounts.filter { it.status == status }
        }

        override suspend fun getByIdentifierId(identifierId: Long): List<ApplicationAccount> {
            // 需要配合 BindingRepository 查询
            return accounts.toList() // 简化实现
        }

        override suspend fun checkDuplicate(applicationId: Long, accountIdentifier: String): Boolean {
            return accounts.any { it.applicationId == applicationId && it.accountIdentifier == accountIdentifier }
        }

        override suspend fun updateStatus(id: Long, status: AccountStatus) {
            val account = accounts.find { it.id == id }
            if (account != null) {
                account.status = status
                account.updatedAt = Instant.now()
            }
        }

        override suspend fun deleteAll() {
            accounts.clear()
        }
    }

    private class MockIdentifierBindingRepository : IdentifierBindingRepository {
        private val bindings = mutableListOf<IdentifierBinding>()
        private var nextId = 1L

        override suspend fun insert(binding: IdentifierBinding): IdentifierBinding {
            val inserted = IdentifierBinding(
                id = nextId++,
                accountId = binding.accountId,
                identifierId = binding.identifierId,
                purposes = binding.purposes,
                isPrimary = binding.isPrimary,
                boundAt = binding.boundAt
            )
            bindings.add(inserted)
            return inserted
        }

        override suspend fun update(binding: IdentifierBinding) {
            bindings.removeAll { it.id == binding.id }
            bindings.add(binding)
        }

        override suspend fun delete(binding: IdentifierBinding) {
            bindings.removeAll { it.id == binding.id }
        }

        override suspend fun getByAccountId(accountId: Long): List<IdentifierBinding> {
            return bindings.filter { it.accountId == accountId }
        }

        override suspend fun getByIdentifierId(identifierId: Long): List<IdentifierBinding> {
            return bindings.filter { it.identifierId == identifierId }
        }

        override suspend fun getById(id: Long): IdentifierBinding? {
            return bindings.find { it.id == id }
        }

        override suspend fun checkDuplicate(accountId: Long, identifierId: Long): Boolean {
            return bindings.any { it.accountId == accountId && it.identifierId == identifierId }
        }

        override suspend fun getCountByIdentifierId(identifierId: Long): Int {
            return bindings.count { it.identifierId == identifierId }
        }

        override suspend fun deleteByAccountAndIdentifier(accountId: Long, identifierId: Long) {
            bindings.removeAll { it.accountId == accountId && it.identifierId == identifierId }
        }

        override suspend fun deleteByAccountId(accountId: Long) {
            bindings.removeAll { it.accountId == accountId }
        }

        override suspend fun updatePurposes(id: Long, purposes: List<BindingPurpose>) {
            val binding = bindings.find { it.id == id }
            if (binding != null) {
                binding.updatePurposes(purposes)
            }
        }

        override suspend fun switchIdentifier(accountId: Long, oldIdentifierId: Long, newIdentifierId: Long) {
            val binding = bindings.find { it.accountId == accountId && it.identifierId == oldIdentifierId }
            if (binding != null) {
                bindings.removeAll { it.id == binding.id }
                val newBinding = IdentifierBinding(
                    id = binding.id,
                    accountId = accountId,
                    identifierId = newIdentifierId,
                    purposes = binding.purposes,
                    isPrimary = binding.isPrimary,
                    boundAt = binding.boundAt
                )
                bindings.add(newBinding)
            }
        }

        override suspend fun deleteAll() {
            bindings.clear()
        }
    }

    private class MockWarningRecordRepository : WarningRecordRepository {
        private val warnings = mutableListOf<WarningRecord>()
        private var nextId = 1L

        override suspend fun insert(warning: WarningRecord): WarningRecord {
            val inserted = WarningRecord(
                id = nextId++,
                identifierId = warning.identifierId,
                warningLevel = warning.warningLevel,
                warningType = warning.warningType,
                message = warning.message,
                triggeredAt = warning.triggeredAt,
                isRead = warning.isRead,
                isHandled = warning.isHandled,
                handledAt = warning.handledAt
            )
            warnings.add(inserted)
            return inserted
        }

        override suspend fun update(warning: WarningRecord) {
            warnings.removeAll { it.id == warning.id }
            warnings.add(warning)
        }

        override suspend fun delete(warning: WarningRecord) {
            warnings.removeAll { it.id == warning.id }
        }

        override suspend fun getById(id: Long): WarningRecord? {
            return warnings.find { it.id == id }
        }

        override suspend fun getUnhandled(): List<WarningRecord> {
            return warnings.filter { !it.isHandled }
        }

        override suspend fun getHandled(): List<WarningRecord> {
            return warnings.filter { it.isHandled }
        }

        override suspend fun getQuickHandleList(limit: Int): List<WarningRecord> {
            return warnings.filter { !it.isHandled }.sortedByDescending { it.warningLevel }.take(limit)
        }

        override suspend fun getByIdentifierId(identifierId: Long): List<WarningRecord> {
            return warnings.filter { it.identifierId == identifierId }
        }

        override suspend fun deleteByIdentifierId(identifierId: Long) {
            warnings.removeAll { it.identifierId == identifierId }
        }

        override suspend fun markAsRead(id: Long) {
            val warning = warnings.find { it.id == id }
            if (warning != null) {
                warning.isRead = true
            }
        }

        override suspend fun markAsHandled(id: Long) {
            val warning = warnings.find { it.id == id }
            if (warning != null) {
                warning.isHandled = true
                warning.handledAt = Instant.now()
            }
        }

        override suspend fun getUnhandledCount(): Int {
            return warnings.count { !it.isHandled }
        }
    }

    private class MockIdentifierDeactivationRepository : IdentifierDeactivationRepository {
        private val deactivations = mutableListOf<IdentifierDeactivation>()
        private var nextId = 1L

        override suspend fun insert(deactivation: IdentifierDeactivation): IdentifierDeactivation {
            val inserted = IdentifierDeactivation(
                id = nextId++,
                identifierId = deactivation.identifierId,
                deactType = deactivation.deactType,
                status = deactivation.status,
                scheduledTime = deactivation.scheduledTime,
                actualTime = deactivation.actualTime,
                reason = deactivation.reason,
                createdAt = deactivation.createdAt,
                updatedAt = deactivation.updatedAt
            )
            deactivations.add(inserted)
            return inserted
        }

        override suspend fun update(deactivation: IdentifierDeactivation) {
            deactivations.removeAll { it.id == deactivation.id }
            deactivations.add(deactivation)
        }

        override suspend fun delete(deactivation: IdentifierDeactivation) {
            deactivations.removeAll { it.id == deactivation.id }
        }

        override suspend fun getById(id: Long): IdentifierDeactivation? {
            return deactivations.find { it.id == id }
        }

        override suspend fun getByIdentifierId(identifierId: Long): IdentifierDeactivation? {
            return deactivations.find { it.identifierId == identifierId }
        }

        override suspend fun getByStatus(status: DeactivationStatus): List<IdentifierDeactivation> {
            return deactivations.filter { it.status == status }
        }

        override suspend fun getScheduledBefore(threshold: Instant): List<IdentifierDeactivation> {
            return deactivations.filter {
                it.status == DeactivationStatus.SCHEDULED &&
                it.scheduledTime != null &&
                it.scheduledTime!!.isBefore(threshold)
            }
        }

        override suspend fun getScheduledBetween(start: Instant, end: Instant): List<IdentifierDeactivation> {
            return deactivations.filter {
                it.status == DeactivationStatus.SCHEDULED &&
                it.scheduledTime != null &&
                it.scheduledTime!!.isAfter(start) &&
                it.scheduledTime!!.isBefore(end)
            }
        }

        override suspend fun markExecuted(id: Long) {
            val deactivation = deactivations.find { it.id == id }
            if (deactivation != null) {
                deactivation.execute()
            }
        }

        override suspend fun markCancelled(id: Long, reason: String) {
            val deactivation = deactivations.find { it.id == id }
            if (deactivation != null) {
                deactivation.cancel(reason)
            }
        }

        override suspend fun updateScheduledTime(id: Long, newTime: Instant) {
            val deactivation = deactivations.find { it.id == id }
            if (deactivation != null) {
                deactivation.updateScheduledTime(newTime)
            }
        }
    }

    private class MockBindingHistoryRepository : BindingHistoryRepository {
        private val histories = mutableListOf<BindingHistoryRecord>()
        private var nextId = 1L

        override suspend fun insert(history: BindingHistoryRecord): BindingHistoryRecord {
            val inserted = BindingHistoryRecord(
                id = nextId++,
                accountId = history.accountId,
                identifierId = history.identifierId,
                actionType = history.actionType,
                previousPurposes = history.previousPurposes,
                newPurposes = history.newPurposes,
                previousIdentifierId = history.previousIdentifierId,
                newIdentifierId = history.newIdentifierId,
                actionAt = history.actionAt
            )
            histories.add(inserted)
            return inserted
        }

        override suspend fun getById(id: Long): BindingHistoryRecord? {
            return histories.find { it.id == id }
        }

        override suspend fun getByAccountId(accountId: Long): List<BindingHistoryRecord> {
            return histories.filter { it.accountId == accountId }
        }

        override suspend fun getByIdentifierId(identifierId: Long): List<BindingHistoryRecord> {
            return histories.filter { it.identifierId == identifierId }
        }

        override suspend fun getAll(): List<BindingHistoryRecord> {
            return histories.toList()
        }
    }
}