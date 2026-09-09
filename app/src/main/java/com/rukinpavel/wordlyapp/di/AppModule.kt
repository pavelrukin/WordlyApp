package com.rukinpavel.wordlyapp.di

import com.rukinpavel.wordlyapp.ads.AdManagerImpl
import com.rukinpavel.wordlyapp.domain.repository.AdManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAdManager(adManagerImpl: AdManagerImpl): AdManager
}
