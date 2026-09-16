package com.rukinpavel.wordlyapp.feature.settings.impl.di

import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.feature.settings.impl.SettingsNavigation
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
    @Singleton
    @IntoSet
    fun bindSettingsNavigation(impl: SettingsNavigation): FeatureNavigation
}
