package com.prgamebooster.di

import android.content.Context
import com.prgamebooster.data.repository.DeviceMonitorRepositoryImpl
import com.prgamebooster.data.repository.GameProfileRepositoryImpl
import com.prgamebooster.data.repository.SettingsRepositoryImpl
import com.prgamebooster.domain.repository.DeviceMonitorRepository
import com.prgamebooster.domain.repository.GameProfileRepository
import com.prgamebooster.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameProfileRepository(impl: GameProfileRepositoryImpl): GameProfileRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindDeviceMonitorRepository(impl: DeviceMonitorRepositoryImpl): DeviceMonitorRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}
