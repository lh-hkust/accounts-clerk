package com.hermes.data.di

import android.content.Context
import androidx.room.Room
import com.hermes.data.db.DatabaseSeeder
import com.hermes.data.db.HermesDatabase
import com.hermes.data.dao.*
import com.hermes.data.security.KeyManagementServiceImpl
import com.hermes.domain.service.KeyManagementService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKeyManagementService(
        @ApplicationContext context: Context,
        impl: KeyManagementServiceImpl
    ): KeyManagementService {
        return impl
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyManagementService: KeyManagementService
    ): HermesDatabase {
        // 加载SQLCipher库
        System.loadLibrary("sqlcipher")

        // 获取数据库密钥（无密码模式首次启动）
        val dbKey = if (keyManagementService.isKeyInitialized()) {
            keyManagementService.getDatabaseKey(null)
        } else {
            keyManagementService.initializeKey(null)
        }

        // 将密钥转换为SQLCipher所需的格式
        val passphrase = dbKey

        return Room.databaseBuilder(
            context,
            HermesDatabase::class.java,
            "hermes_db"
        )
            .openHelperFactory(SupportOpenHelperFactory(passphrase))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideIdentityIdentifierDao(db: HermesDatabase): IdentityIdentifierDao = db.identityIdentifierDao()

    @Provides
    fun provideApplicationDao(db: HermesDatabase): ApplicationDao = db.applicationDao()

    @Provides
    fun provideApplicationAccountDao(db: HermesDatabase): ApplicationAccountDao = db.applicationAccountDao()

    @Provides
    fun provideIdentifierBindingDao(db: HermesDatabase): IdentifierBindingDao = db.identifierBindingDao()

    @Provides
    fun provideAccountExtensionDao(db: HermesDatabase): AccountExtensionDao = db.accountExtensionDao()

    @Provides
    fun provideIdentifierDeactivationDao(db: HermesDatabase): IdentifierDeactivationDao = db.identifierDeactivationDao()

    @Provides
    fun provideWarningRecordDao(db: HermesDatabase): WarningRecordDao = db.warningRecordDao()

    @Provides
    fun provideBindingHistoryRecordDao(db: HermesDatabase): BindingHistoryRecordDao = db.bindingHistoryRecordDao()

    @Provides
    @Singleton
    fun provideDatabaseSeeder(applicationDao: ApplicationDao): DatabaseSeeder {
        return DatabaseSeeder(applicationDao)
    }
}