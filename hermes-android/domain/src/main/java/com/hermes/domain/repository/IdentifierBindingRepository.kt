package com.hermes.domain.repository

import com.hermes.domain.model.IdentifierBinding
import com.hermes.domain.valueobject.BindingPurpose

interface IdentifierBindingRepository {
    suspend fun insert(binding: IdentifierBinding): IdentifierBinding
    suspend fun update(binding: IdentifierBinding)
    suspend fun delete(binding: IdentifierBinding)
    suspend fun getById(id: Long): IdentifierBinding?
    suspend fun getByAccountId(accountId: Long): List<IdentifierBinding>
    suspend fun getByIdentifierId(identifierId: Long): List<IdentifierBinding>
    suspend fun checkDuplicate(accountId: Long, identifierId: Long): Boolean
    suspend fun getCountByIdentifierId(identifierId: Long): Int
    suspend fun deleteByAccountAndIdentifier(accountId: Long, identifierId: Long)
    suspend fun updatePurposes(id: Long, purposes: List<BindingPurpose>)
    suspend fun switchIdentifier(accountId: Long, oldIdentifierId: Long, newIdentifierId: Long)
    suspend fun deleteAll()
}