package com.rukinpavel.wordlyapp.data

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.domain.repository.WordRepository
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
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): WordRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        dataStoreUserPreferencesRepository: DataStoreUserPreferencesRepository
    ): UserPreferencesRepository
}
