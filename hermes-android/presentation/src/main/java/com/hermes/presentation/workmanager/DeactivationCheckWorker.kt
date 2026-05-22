package com.hermes.presentation.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hermes.domain.repository.IdentityIdentifierRepository
import com.hermes.domain.repository.IdentifierDeactivationRepository
import com.hermes.domain.repository.WarningRecordRepository
import com.hermes.domain.valueobject.WarningType
import com.hermes.presentation.usecase.warning.TriggerWarningUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * 停用计划定时检查 Worker
 * 每小时检查一次即将到期的停用计划
 */
@HiltWorker
class DeactivationCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val identifierRepository: IdentityIdentifierRepository,
    private val deactivationRepository: IdentifierDeactivationRepository,
    private val warningRepository: WarningRecordRepository,
    private val triggerWarningUseCase: TriggerWarningUseCase
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "deactivation_check_work"
        const val CHECK_INTERVAL_HOURS = 1L

        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DeactivationCheckWorker>(
                CHECK_INTERVAL_HOURS,
                TimeUnit.HOURS
            )
                .setInitialDelay(0, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        try {
            val now = Instant.now()
            val threshold = now.plusSeconds(24 * 60 * 60) // 24小时后

            // 获取即将到期的停用计划
            val pendingIdentifiers = identifierRepository.getPendingDeactivationBefore(threshold)

            for (identifier in pendingIdentifiers) {
                val identifierId = identifier.id ?: continue

                // 检查是否已经到达停用时间
                val plannedTime = identifier.plannedDeactTime
                if (plannedTime != null && plannedTime.isBefore(now)) {
                    // 执行停用
                    identifier.deactivate()
                    identifierRepository.update(identifier)

                    // 更新停用计划状态
                    val deactivation = deactivationRepository.getByIdentifierId(identifierId)
                    if (deactivation != null) {
                        deactivationRepository.markExecuted(deactivation.id!!)
                    }
                } else {
                    // 检查是否已有预警
                    val existingWarnings = warningRepository.getByIdentifierId(identifierId)
                    if (existingWarnings.isEmpty() && plannedTime != null) {
                        // 触发预警
                        triggerWarningUseCase(identifierId, WarningType.DEADLINE_APPROACHING)
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}