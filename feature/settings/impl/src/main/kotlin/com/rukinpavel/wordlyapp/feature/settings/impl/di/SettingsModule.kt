package com.rukinpavel.wordlyapp.feature.settings.impl.di

import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.feature.settings.impl.SettingsNavigation
import com.rukinpavel.wordlyapp.feature.settings.impl.data.repository.BillingRepositoryImpl
import com.rukinpavel.wordlyapp.feature.settings.impl.data.repository.SettingsPreferencesRepositoryImpl
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.SettingsPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SettingsModule {
    @Binds
    @IntoSet
    fun bindSettingsNavigation(impl: SettingsNavigation): FeatureNavigation

    @Binds
    @Singleton
    fun bindSettingsPreferencesRepository(impl: SettingsPreferencesRepositoryImpl): SettingsPreferencesRepository

    @Binds
    @Singleton
    fun bindBillingRepository(impl: BillingRepositoryImpl): BillingRepository
}
