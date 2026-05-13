package com.hermes.domain.repository

import com.hermes.domain.model.IdentifierDeactivation
import com.hermes.domain.valueobject.DeactivationStatus
import java.time.Instant

interface IdentifierDeactivationRepository {
    suspend fun insert(deactivation: IdentifierDeactivation): IdentifierDeactivation
    suspend fun update(deactivation: IdentifierDeactivation)
    suspend fun delete(deactivation: IdentifierDeactivation)
    suspend fun getById(id: Long): IdentifierDeactivation?
    suspend fun getByIdentifierId(identifierId: Long): IdentifierDeactivation?
    suspend fun getByStatus(status: DeactivationStatus): List<IdentifierDeactivation>
    suspend fun getScheduledBefore(threshold: Instant): List<IdentifierDeactivation>
    suspend fun getScheduledBetween(start: Instant, end: Instant): List<IdentifierDeactivation>
    suspend fun markExecuted(id: Long)
    suspend fun markCancelled(id: Long, reason: String)
    suspend fun updateScheduledTime(id: Long, newTime: Instant)
}