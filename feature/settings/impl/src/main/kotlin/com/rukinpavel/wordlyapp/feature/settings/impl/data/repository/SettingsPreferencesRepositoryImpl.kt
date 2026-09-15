package com.rukinpavel.wordlyapp.feature.settings.impl.data.repository

import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.SettingsPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : SettingsPreferencesRepository {

    override suspend fun updateLanguage(language: Language) {
        userPreferencesRepository.updateLanguage(language)
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        userPreferencesRepository.updateVibrationEnabled(enabled)
    }

    override suspend fun updatePremiumStatus(isPremium: Boolean) {
        userPreferencesRepository.updatePremiumStatus(isPremium)
    }
}
