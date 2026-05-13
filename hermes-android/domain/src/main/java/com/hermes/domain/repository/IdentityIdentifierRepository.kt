package com.hermes.domain.repository

import com.hermes.domain.model.IdentityIdentifier
import com.hermes.domain.valueobject.IdentifierStatus
import com.hermes.domain.valueobject.IdentifierType
import java.time.Instant

interface IdentityIdentifierRepository {
    suspend fun insert(identifier: IdentityIdentifier): IdentityIdentifier
    suspend fun update(identifier: IdentityIdentifier)
    suspend fun delete(identifier: IdentityIdentifier)
    suspend fun getById(id: Long): IdentityIdentifier?
    suspend fun getAll(): List<IdentityIdentifier>
    suspend fun getByStatus(status: IdentifierStatus): List<IdentityIdentifier>
    suspend fun checkDuplicate(type: IdentifierType, value: String): Boolean
    suspend fun getBoundAccountCount(identifierId: Long): Int
    suspend fun getPendingDeactivationBefore(threshold: Instant): List<IdentityIdentifier>
}