package com.hermes.data.di

import android.content.Context
import androidx.room.Room
import com.hermes.data.db.HermesDatabase
import com.hermes.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HermesDatabase {
        return Room.databaseBuilder(
            context,
            HermesDatabase::class.java,
            "hermes_db"
        ).build()
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
}