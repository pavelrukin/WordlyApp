package com.rukinpavel.wordlyapp.core.platform.android

import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DailyReminderWorkerEntryPoint {
    fun appPreferencesRepository(): AppPreferencesRepository
}
