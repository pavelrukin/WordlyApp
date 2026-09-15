package com.rukinpavel.wordlyapp.core.database

import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
    @Binds
    @Singleton
    fun bindAppPreferencesRepository(impl: DataStoreAppPreferencesRepository): AppPreferencesRepository

    @Binds
    @Singleton
    fun bindUserPreferencesRepository(impl: DataStoreAppPreferencesRepository): UserPreferencesRepository
}
