package com.rukinpavel.wordlyapp.core.domain.repository

import com.rukinpavel.wordlyapp.core.model.Language
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val language: Flow<Language?>
    val vibrationEnabled: Flow<Boolean>
    val isPremium: Flow<Boolean>
    val tutorialCompleted: Flow<Boolean>

    suspend fun updateTutorialCompleted(completed: Boolean)
}

// Temporary bridge interface to avoid breaking legacy code
interface UserPreferencesRepository {
    val language: Flow<Language?>
    val vibrationEnabled: Flow<Boolean>
    val tutorialCompleted: Flow<Boolean>
    val hintCount: Flow<Int>
    val isPremium: Flow<Boolean>

    suspend fun updateLanguage(language: Language)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateTutorialCompleted(completed: Boolean)
    suspend fun updateHintCount(count: Int)
    suspend fun updatePremiumStatus(isPremium: Boolean)
}
