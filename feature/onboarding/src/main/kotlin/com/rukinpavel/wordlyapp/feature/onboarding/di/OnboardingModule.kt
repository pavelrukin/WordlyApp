package com.rukinpavel.wordlyapp.feature.onboarding.di

import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.feature.onboarding.OnboardingNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface OnboardingModule {
    @Binds
    @IntoSet
    fun bindOnboardingNavigation(impl: OnboardingNavigation): FeatureNavigation
}
