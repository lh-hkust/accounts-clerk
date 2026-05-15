package com.hermes.presentation.usecase

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.model.WarningRecord
import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.valueobject.IdentifierType
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.AccountStatus
import com.hermes.domain.valueobject.BindingPurpose
import com.hermes.domain.valueobject.WarningLevel
import com.hermes.domain.valueobject.WarningType
import com.hermes.domain.valueobject.DeactivationType
import com.hermes.domain.valueobject.DeactivationStatus
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.repository.IdentifierBindingRepository
import com.hermes.domain.repository.WarningRecordRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.BindingHistoryRepository
import com.hermes.presentation.usecase.identifier.AddIdentifierUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierListUseCase
import com.hermes.presentation.usecase.identifier.GetIdentifierDetailUseCase
import com.hermes.presentation.usecase.identifier.DeleteIdentifierUseCase
import com.hermes.presentation.usecase.identifier.CheckDuplicateIdentifierUseCase
import com.hermes.presentation.usecase.account.AddAccountUseCase
import com.hermes.presentation.usecase.account.GetAccountListUseCase
import com.hermes.presentation.usecase.account.GetAccountDetailUseCase
import com.hermes.presentation.usecase.binding.BindIdentifierUseCase
import com.hermes.presentation.usecase.binding.UnbindIdentifierUseCase
import com.hermes.presentation.usecase.binding.SwitchBindingIdentifierUseCase
import com.hermes.presentation.usecase.deactivation.ScheduleDeactivationUseCase
import com.hermes.presentation.usecase.deactivation.CancelDeactivationUseCase
import com.hermes.presentation.usecase.warning.TriggerWarningUseCase
import com.hermes.presentation.usecase.warning.HandleWarningUseCase
import com.hermes.presentation.usecase.impact.AnalyzeImpactUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * UseCase 集成测试
 *
 * 测试 UseCase 与 Repository 的交互
 * 使用 Mock Repository 进行单元测试验证业务逻辑
 *
 * 注意：完整集成测试需要在 Android 环境下运行
 */
class UseCaseIntegrationTest {

    // Mock Repositories
    private lateinit var identifierRepository: MockIdentityIdentifierRepository
    private lateinit var accountRepository: MockApplicationAccountRepository
    private lateinit var bindingRepository: MockIdentifierBindingRepository
    private lateinit var warningRepository: MockWarningRecordRepository
    private lateinit var deactivationRepository: MockIdentifierDeactivationRepository
    private lateinit var bindingHistoryRepository: MockBindingHistoryRepository

    // UseCases
    private lateinit var addIdentifierUseCase: AddIdentifierUseCase
    private lateinit var getIdentifierListUseCase: GetIdentifierListUseCase
    private lateinit var getIdentifierDetailUseCase: GetIdentifierDetailUseCase
    private lateinit var deleteIdentifierUseCase: DeleteIdentifierUseCase
    private lateinit var checkDuplicateUseCase: CheckDuplicateIdentifierUseCase
    private lateinit var addAccountUseCase: AddAccountUseCase
    private lateinit var getAccountListUseCase: GetAccountListUseCase
    private lateinit var getAccountDetailUseCase: GetAccountDetailUseCase
    private lateinit var bindIdentifierUseCase: BindIdentifierUseCase
    private lateinit var unbindIdentifierUseCase: UnbindIdentifierUseCase
    private lateinit var switchBindingUseCase: SwitchBindingIdentifierUseCase
    private lateinit var scheduleDeactivationUseCase: ScheduleDeactivationUseCase
    private lateinit var cancelDeactivationUseCase: CancelDeactivationUseCase
    private lateinit var triggerWarningUseCase: TriggerWarningUseCase
    private lateinit var handleWarningUseCase: HandleWarningUseCase
    private lateinit var analyzeImpactUseCase: AnalyzeImpactUseCase

    @Before
    fun setup() {
        // 初始化 Mock Repositories
        identifierRepository = MockIdentityIdentifierRepository()
        accountRepository = MockApplicationAccountRepository()
        bindingRepository = MockIdentifierBindingRepository()
        warningRepository = MockWarningRecordRepository()
        deactivationRepository = MockIdentifierDeactivationRepository()
        bindingHistoryRepository = MockBindingHistoryRepository()

        // 初始化 UseCases
        addIdentifierUseCase = AddIdentifierUseCase(identifierRepository)
        getIdentifierListUseCase = GetIdentifierListUseCase(identifierRepository, bindingRepository)
        getIdentifierDetailUseCase = GetIdentifierDetailUseCase(identifierRepository, bindingRepository, accountRepository)
        deleteIdentifierUseCase = DeleteIdentifierUseCase(identifierRepository, bindingRepository)
        checkDuplicateUseCase = CheckDuplicateIdentifierUseCase(identifierRepository)
        addAccountUseCase = AddAccountUseCase(accountRepository)
        getAccountListUseCase = GetAccountListUseCase(accountRepository, bindingRepository)
        getAccountDetailUseCase = GetAccountDetailUseCase(accountRepository, bindingRepository)
        bindIdentifierUseCase = BindIdentifierUseCase(bindingRepository, bindingHistoryRepository)
        unbindIdentifierUseCase = UnbindIdentifierUseCase(bindingRepository, bindingHistoryRepository)
        switchBindingUseCase = SwitchBindingIdentifierUseCase(bindingRepository, bindingHistoryRepository)
        scheduleDeactivationUseCase = ScheduleDeactivationUseCase(identifierRepository, deactivationRepository, warningRepository)
        cancelDeactivationUseCase = CancelDeactivationUseCase(identifierRepository, deactivationRepository, warningRepository)
        triggerWarningUseCase = TriggerWarningUseCase(warningRepository)
        handleWarningUseCase = HandleWarningUseCase(warningRepository)
        analyzeImpactUseCase = AnalyzeImpactUseCase(bindingRepository, accountRepository)
    }

    // ========== AddIdentifierUseCase 测试 ==========

    @Test
    fun `添加标识应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        assertNotNull(identifier.id)
        assertEquals(IdentifierType.PHONE, identifier.type)
        assertEquals("13800138000", identifier.value)
        assertEquals(IdentifierStatus.ACTIVE, identifier.status)
    }

    @Test
    fun `添加邮箱标识应自动小写处理`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.EMAIL, "TEST@EXAMPLE.COM")

        assertEquals("test@example.com", identifier.value)
    }

    @Test
    fun `添加空标识值应抛出异常`() = runBlocking {
        try {
            addIdentifierUseCase(IdentifierType.PHONE, "")
            fail("Should throw IllegalArgumentException for empty value")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("empty"))
        }
    }

    @Test
    fun `添加重复标识应抛出异常`() = runBlocking {
        addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        try {
            addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
            fail("Should throw IllegalArgumentException for duplicate")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("already exists"))
        }
    }

    // ========== CheckDuplicateIdentifierUseCase 测试 ==========

    @Test
    fun `检查重复标识应返回true`() = runBlocking {
        addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        val isDuplicate = checkDuplicateUseCase(IdentifierType.PHONE, "13800138000")

        assertTrue(isDuplicate)
    }

    @Test
    fun `检查非重复标识应返回false`() = runBlocking {
        addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        val isDuplicate = checkDuplicateUseCase(IdentifierType.PHONE, "13800138001")

        assertFalse(isDuplicate)
    }

    // ========== GetIdentifierListUseCase 测试 ==========

    @Test
    fun `获取标识列表应返回正确数据`() = runBlocking {
        addIdentifierUseCase(IdentifierType.PHONE, "13800138001")
        addIdentifierUseCase(IdentifierType.EMAIL, "test@example.com")

        val list = getIdentifierListUseCase()

        assertEquals(2, list.size)
    }

    @Test
    fun `标识列表应包含绑定账户数`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")

        // 绑定
        bindIdentifierUseCase(account.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))

        val list = getIdentifierListUseCase()
        val item = list.find { it.identifier.id == identifier.id }

        assertNotNull(item)
        assertEquals(1, item!!.boundAccountCount)
    }

    @Test
    fun `标识列表应按状态排序`() = runBlocking {
        val id1 = addIdentifierUseCase(IdentifierType.PHONE, "13800138001")
        val id2 = addIdentifierUseCase(IdentifierType.PHONE, "13800138002")

        // 设置第一个为即将停用状态
        id1.status = IdentifierStatus.PENDING_DEACTIVATION
        identifierRepository.update(id1)

        val list = getIdentifierListUseCase()

        // ACTIVE 排在前面
        assertEquals(IdentifierStatus.ACTIVE, list[0].identifier.status)
        assertEquals(IdentifierStatus.PENDING_DEACTIVATION, list[1].identifier.status)
    }

    // ========== DeleteIdentifierUseCase 测试 ==========

    @Test
    fun `删除无绑定标识应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        deleteIdentifierUseCase(identifier.id!!)

        val retrieved = identifierRepository.getById(identifier.id!!)
        assertNull(retrieved)
    }

    @Test
    fun `删除有绑定标识应抛出异常`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")
        bindIdentifierUseCase(account.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))

        try {
            deleteIdentifierUseCase(identifier.id!!)
            fail("Should throw IllegalArgumentException for bound identifier")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("binding"))
        }
    }

    // ========== AddAccountUseCase 测试 ==========

    @Test
    fun `添加账户应成功`() = runBlocking {
        val account = addAccountUseCase(1L, "测试账号", "account001")

        assertNotNull(account.id)
        assertEquals(1L, account.applicationId)
        assertEquals("测试账号", account.accountName)
        assertEquals("account001", account.accountIdentifier)
        assertEquals(AccountStatus.ACTIVE, account.status)
    }

    // ========== BindIdentifierUseCase 测试 ==========

    @Test
    fun `绑定标识到账户应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")

        val binding = bindIdentifierUseCase(
            accountId = account.id!!,
            identifierId = identifier.id!!,
            purposes = listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION)
        )

        assertNotNull(binding.id)
        assertEquals(account.id!!, binding.accountId)
        assertEquals(identifier.id!!, binding.identifierId)
    }

    @Test
    fun `绑定时用途为空应抛出异常`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")

        try {
            bindIdentifierUseCase(account.id!!, identifier.id!!, emptyList())
            fail("Should throw IllegalArgumentException for empty purposes")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("empty"))
        }
    }

    @Test
    fun `绑定历史记录应被创建`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")

        bindIdentifierUseCase(account.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))

        val history = bindingHistoryRepository.getByAccountId(account.id!!)
        assertEquals(1, history.size)
    }

    // ========== UnbindIdentifierUseCase 测试 ==========

    @Test
    fun `解绑标识应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")
        bindIdentifierUseCase(account.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))

        unbindIdentifierUseCase(account.id!!, identifier.id!!)

        val bindings = bindingRepository.getByAccountId(account.id!!)
        assertTrue(bindings.isEmpty())
    }

    @Test
    fun `解绑后应创建历史记录`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account = addAccountUseCase(1L, "测试账号", "account001")
        bindIdentifierUseCase(account.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))

        unbindIdentifierUseCase(account.id!!, identifier.id!!)

        val history = bindingHistoryRepository.getByAccountId(account.id!!)
        assertEquals(2, history.size) // BIND + UNBIND
    }

    // ========== SwitchBindingIdentifierUseCase 测试 ==========

    @Test
    fun `更换绑定标识应成功`() = runBlocking {
        val oldIdentifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138001")
        val newIdentifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138002")
        val account = addAccountUseCase(1L, "测试账号", "account001")
        bindIdentifierUseCase(account.id!!, oldIdentifier.id!!, listOf(BindingPurpose.LOGIN))

        switchBindingUseCase(account.id!!, oldIdentifier.id!!, newIdentifier.id!!)

        val bindings = bindingRepository.getByAccountId(account.id!!)
        assertEquals(1, bindings.size)
        assertEquals(newIdentifier.id!!, bindings[0].identifierId)
    }

    @Test
    fun `更换绑定标识应保留用途`() = runBlocking {
        val oldIdentifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138001")
        val newIdentifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138002")
        val account = addAccountUseCase(1L, "测试账号", "account001")
        bindIdentifierUseCase(account.id!!, oldIdentifier.id!!, listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION))

        switchBindingUseCase(account.id!!, oldIdentifier.id!!, newIdentifier.id!!)

        val bindings = bindingRepository.getByAccountId(account.id!!)
        assertEquals(listOf(BindingPurpose.LOGIN, BindingPurpose.VERIFICATION), bindings[0].purposes)
    }

    // ========== ScheduleDeactivationUseCase 测试 ==========

    @Test
    fun `设置停用计划应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        scheduleDeactivationUseCase(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        val updated = identifierRepository.getById(identifier.id!!)
        assertEquals(IdentifierStatus.PENDING_DEACTIVATION, updated!!.status)
    }

    @Test
    fun `设置停用计划应创建停用记录`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        scheduleDeactivationUseCase(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        val deactivation = deactivationRepository.getByIdentifierId(identifier.id!!)
        assertNotNull(deactivation)
        assertEquals(DeactivationStatus.SCHEDULED, deactivation!!.status)
    }

    // ========== CancelDeactivationUseCase 测试 ==========

    @Test
    fun `取消停用计划应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        scheduleDeactivationUseCase(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        cancelDeactivationUseCase(identifier.id!!, "不再更换")

        val updated = identifierRepository.getById(identifier.id!!)
        assertEquals(IdentifierStatus.ACTIVE, updated!!.status)
    }

    @Test
    fun `取消停用计划应删除预警`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val scheduledTime = Instant.now().plusSeconds(30 * 24 * 60 * 60)

        scheduleDeactivationUseCase(
            identifierId = identifier.id!!,
            scheduledTime = scheduledTime,
            reason = "手机号更换",
            type = DeactivationType.PHONE_NUMBER_CHANGE
        )

        // 模拟创建预警
        triggerWarningUseCase(
            identifierId = identifier.id!!,
            warningType = WarningType.DEACTIVATION_PLAN,
            warningLevel = WarningLevel.MEDIUM,
            message = "测试预警"
        )

        cancelDeactivationUseCase(identifier.id!!, "不再更换")

        val warnings = warningRepository.getByIdentifierId(identifier.id!!)
        assertTrue(warnings.isEmpty())
    }

    // ========== TriggerWarningUseCase 测试 ==========

    @Test
    fun `触发预警应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")

        val warning = triggerWarningUseCase(
            identifierId = identifier.id!!,
            warningType = WarningType.DEACTIVATION_PLAN,
            warningLevel = WarningLevel.HIGH,
            message = "紧急处理"
        )

        assertNotNull(warning.id)
        assertEquals(identifier.id!!, warning.identifierId)
        assertEquals(WarningLevel.HIGH, warning.warningLevel)
    }

    // ========== HandleWarningUseCase 测试 ==========

    @Test
    fun `处理预警应成功`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val warning = triggerWarningUseCase(
            identifierId = identifier.id!!,
            warningType = WarningType.DEACTIVATION_PLAN,
            warningLevel = WarningLevel.MEDIUM,
            message = "测试预警"
        )

        handleWarningUseCase(warning.id!!)

        val updated = warningRepository.getById(warning.id!!)
        assertTrue(updated!!.isHandled)
    }

    // ========== AnalyzeImpactUseCase 测试 ==========

    @Test
    fun `影响分析应返回绑定账户列表`() = runBlocking {
        val identifier = addIdentifierUseCase(IdentifierType.PHONE, "13800138000")
        val account1 = addAccountUseCase(1L, "账号1", "account001")
        val account2 = addAccountUseCase(2L, "账号2", "account002")

        bindIdentifierUseCase(account1.id!!, identifier.id!!, listOf(BindingPurpose.LOGIN))
        bindIdentifierUseCase(account2.id!!, identifier.id!!, listOf(BindingPurpose.VERIFICATION))

        val impact = analyzeImpactUseCase(identifier.id!!)

        assertEquals(2, impact.affectedAccounts.size)
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
            return 0
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
            return accounts.toList()
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

        override suspend fun getById(id: Long): IdentifierBinding? {
            return bindings.find { it.id == id }
        }

        override suspend fun getByAccountId(accountId: Long): List<IdentifierBinding> {
            return bindings.filter { it.accountId == accountId }
        }

        override suspend fun getByIdentifierId(identifierId: Long): List<IdentifierBinding> {
            return bindings.filter { it.identifierId == identifierId }
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

        override suspend fun insert(record: WarningRecord): WarningRecord {
            val inserted = WarningRecord(
                id = nextId++,
                identifierId = record.identifierId,
                warningLevel = record.warningLevel,
                warningType = record.warningType,
                message = record.message,
                triggeredAt = record.triggeredAt,
                isRead = record.isRead,
                isHandled = record.isHandled,
                handledAt = record.handledAt
            )
            warnings.add(inserted)
            return inserted
        }

        override suspend fun update(record: WarningRecord) {
            warnings.removeAll { it.id == record.id }
            warnings.add(record)
        }

        override suspend fun delete(record: WarningRecord) {
            warnings.removeAll { it.id == record.id }
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

        override suspend fun insert(record: BindingHistoryRecord): BindingHistoryRecord {
            val inserted = BindingHistoryRecord(
                id = nextId++,
                accountId = record.accountId,
                identifierId = record.identifierId,
                actionType = record.actionType,
                previousPurposes = record.previousPurposes,
                newPurposes = record.newPurposes,
                previousIdentifierId = record.previousIdentifierId,
                newIdentifierId = record.newIdentifierId,
                actionAt = record.actionAt
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