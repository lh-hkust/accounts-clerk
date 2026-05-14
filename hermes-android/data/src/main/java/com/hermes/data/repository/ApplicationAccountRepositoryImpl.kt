package com.hermes.data.repository
import javax.inject.Inject

import com.hermes.data.dao.ApplicationAccountDao
import com.hermes.data.dao.IdentifierBindingDao
import com.hermes.data.dao.AccountExtensionDao
import com.hermes.data.entity.ApplicationAccountEntity
import com.hermes.data.entity.IdentifierBindingEntity
import com.hermes.data.entity.AccountExtensionEntity
import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.model.AccountExtension
import com.hermes.domain.repository.ApplicationAccountRepository
import com.hermes.domain.valueobject.AccountStatus

class ApplicationAccountRepositoryImpl @Inject constructor(
    private val accountDao: ApplicationAccountDao,
    private val bindingDao: IdentifierBindingDao,
    private val extensionDao: AccountExtensionDao
) : ApplicationAccountRepository {

    override suspend fun insert(account: ApplicationAccount): ApplicationAccount {
        val entity = ApplicationAccountEntity.fromDomainModel(account)
        val id = accountDao.insert(entity)
        return ApplicationAccount(
            id = id,
            applicationId = account.applicationId,
            accountName = account.accountName,
            accountIdentifier = account.accountIdentifier,
            nickname = account.nickname,
            status = account.status,
            keepAliveEnabled = account.keepAliveEnabled,
            lastLoginDate = account.lastLoginDate,
            notes = account.notes,
            tags = account.tags,
            createdAt = account.createdAt,
            updatedAt = account.updatedAt
        )
    }

    override suspend fun update(account: ApplicationAccount) {
        val entity = ApplicationAccountEntity.fromDomainModel(account)
        accountDao.update(entity)
    }

    override suspend fun delete(account: ApplicationAccount) {
        val entity = ApplicationAccountEntity.fromDomainModel(account)
        accountDao.delete(entity)
    }

    override suspend fun getById(id: Long): ApplicationAccount? {
        return accountDao.getById(id)?.toDomainModel()
    }

    override suspend fun getByIdWithDetails(id: Long): ApplicationAccount? {
        val accountEntity = accountDao.getById(id) ?: return null
        val bindings = bindingDao.getByAccountId(id).map { it.toDomainModel() }
        val extensions = extensionDao.getByAccountId(id).map { it.toDomainModel() }

        return accountEntity.toDomainModel().apply {
            bindings.forEach { bindIdentifier(it.identifierId, it.purposes, it.isPrimary) }
            extensions.forEach { addExtension(it.key, it.value, it.label, it.fieldType) }
        }
    }

    override suspend fun getAll(): List<ApplicationAccount> {
        return accountDao.getAll().map { it.toDomainModel() }
    }

    override suspend fun getByApplicationId(applicationId: Long): List<ApplicationAccount> {
        return accountDao.getByApplicationId(applicationId).map { it.toDomainModel() }
    }

    override suspend fun getByStatus(status: AccountStatus): List<ApplicationAccount> {
        return accountDao.getByStatus(status.name).map { it.toDomainModel() }
    }

    override suspend fun getByIdentifierId(identifierId: Long): List<ApplicationAccount> {
        return accountDao.getByIdentifierId(identifierId).map { it.toDomainModel() }
    }

    override suspend fun checkDuplicate(applicationId: Long, accountIdentifier: String): Boolean {
        return accountDao.existsByApplicationAndIdentifier(applicationId, accountIdentifier)
    }

    override suspend fun updateStatus(id: Long, status: AccountStatus) {
        accountDao.updateStatus(id, status.name, java.time.Instant.now().toEpochMilli())
    }

    override suspend fun deleteAll() {
        // Delete bindings first, then accounts
        val accounts = accountDao.getAll()
        for (account in accounts) {
            bindingDao.deleteByAccountId(account.id)
        }
        accountDao.deleteAll()
    }
}