package com.hermes.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.hermes.data.db.DatabaseSeeder
import com.hermes.presentation.workmanager.DeactivationCheckWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HermesApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()

        // 首次启动时种子化预置数据（异步执行，不阻塞启动）
        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
            databaseSeeder.seedAllIfNeeded()
        }

        // 启动停用计划定时检查
        DeactivationCheckWorker.schedule(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}