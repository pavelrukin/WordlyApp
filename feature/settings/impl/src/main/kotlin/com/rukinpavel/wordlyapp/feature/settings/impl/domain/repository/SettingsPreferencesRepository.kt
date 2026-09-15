package com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository

import com.rukinpavel.wordlyapp.core.model.Language

interface SettingsPreferencesRepository {
    suspend fun updateLanguage(language: Language)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updatePremiumStatus(isPremium: Boolean)
}
