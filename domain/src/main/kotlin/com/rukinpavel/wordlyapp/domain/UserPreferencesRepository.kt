package com.rukinpavel.wordlyapp.domain

import com.rukinpavel.wordlyapp.core.model.Language
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val language: Flow<Language?>
    val vibrationEnabled: Flow<Boolean>
    val tutorialCompleted: Flow<Boolean>
    val hintCount: Flow<Int>
    suspend fun updateLanguage(language: Language)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateTutorialCompleted(completed: Boolean)
    suspend fun updateHintCount(count: Int)
}
