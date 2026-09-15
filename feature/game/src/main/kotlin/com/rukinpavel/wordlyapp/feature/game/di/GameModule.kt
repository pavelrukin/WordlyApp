package com.rukinpavel.wordlyapp.feature.game.di

import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.feature.game.GameNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface GameModule {
    @Binds
    @IntoSet
    fun bindGameNavigation(impl: GameNavigation): FeatureNavigation
}
