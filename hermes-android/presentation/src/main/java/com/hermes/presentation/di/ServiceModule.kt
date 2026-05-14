package com.hermes.presentation.di

import com.hermes.domain.service.CryptoExportService
import com.hermes.domain.service.DataCompressionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 服务提供模块
 * 提供加密导出和数据压缩服务
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideCryptoExportService(): CryptoExportService {
        return CryptoExportService()
    }

    @Provides
    @Singleton
    fun provideDataCompressionService(): DataCompressionService {
        return DataCompressionService()
    }
}