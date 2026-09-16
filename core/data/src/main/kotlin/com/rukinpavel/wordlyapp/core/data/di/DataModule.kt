package com.rukinpavel.wordlyapp.core.data.di

import com.rukinpavel.wordlyapp.core.data.repository.BillingRepositoryImpl
import com.rukinpavel.wordlyapp.core.data.repository.LocalWordRepository
import com.rukinpavel.wordlyapp.core.data.repository.UserPreferencesRepositoryImpl
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindBillingRepository(impl: BillingRepositoryImpl): BillingRepository

    @Binds
    @Singleton
    fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    fun bindAppPreferencesRepository(impl: UserPreferencesRepositoryImpl): AppPreferencesRepository

    @Binds
    @Singleton
    fun bindWordRepository(impl: LocalWordRepository): WordRepository
}
