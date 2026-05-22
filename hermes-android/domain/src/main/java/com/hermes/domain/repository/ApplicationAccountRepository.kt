package com.hermes.domain.repository

import com.hermes.domain.model.ApplicationAccount
import com.hermes.domain.valueobject.AccountStatus

interface ApplicationAccountRepository {
    suspend fun insert(account: ApplicationAccount): ApplicationAccount
    suspend fun update(account: ApplicationAccount)
    suspend fun delete(account: ApplicationAccount)
    suspend fun getById(id: Long): ApplicationAccount?
    suspend fun getByIdWithDetails(id: Long): ApplicationAccount?
    suspend fun getAll(): List<ApplicationAccount>
    suspend fun getByApplicationId(applicationId: Long): List<ApplicationAccount>
    suspend fun getByStatus(status: AccountStatus): List<ApplicationAccount>
    suspend fun getByIdentifierId(identifierId: Long): List<ApplicationAccount>
    suspend fun checkDuplicate(applicationId: Long, accountIdentifier: String): Boolean
    suspend fun updateStatus(id: Long, status: AccountStatus)
    suspend fun deleteAll()
}