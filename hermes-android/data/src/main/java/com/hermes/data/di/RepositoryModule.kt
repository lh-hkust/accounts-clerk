package com.hermes.data.di

import com.hermes.data.repository.*
import com.hermes.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIdentityIdentifierRepository(impl: IdentityIdentifierRepositoryImpl): IdentityIdentifierRepository

    @Binds
    @Singleton
    abstract fun bindApplicationRepository(impl: ApplicationRepositoryImpl): ApplicationRepository

    @Binds
    @Singleton
    abstract fun bindApplicationAccountRepository(impl: ApplicationAccountRepositoryImpl): ApplicationAccountRepository

    @Binds
    @Singleton
    abstract fun bindIdentifierBindingRepository(impl: IdentifierBindingRepositoryImpl): IdentifierBindingRepository

    @Binds
    @Singleton
    abstract fun bindIdentifierDeactivationRepository(impl: IdentifierDeactivationRepositoryImpl): IdentifierDeactivationRepository

    @Binds
    @Singleton
    abstract fun bindWarningRecordRepository(impl: WarningRecordRepositoryImpl): WarningRecordRepository

    @Binds
    @Singleton
    abstract fun bindBindingHistoryRepository(impl: BindingHistoryRepositoryImpl): BindingHistoryRepository
}