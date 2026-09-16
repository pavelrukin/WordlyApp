package com.rukinpavel.wordlyapp.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.core.model.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    UserPreferencesRepository,
    AppPreferencesRepository {

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val HINT_COUNT = intPreferencesKey("hint_count")
    }

    override val language: Flow<Language?> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE]?.let { Language.fromCode(it) }
    }

    override val vibrationEnabled: Flow<Boolean> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
    }

    override val tutorialCompleted: Flow<Boolean> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.TUTORIAL_COMPLETED] ?: false
    }

    override val isPremium: Flow<Boolean> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_PREMIUM] ?: false
    }

    override val hintCount: Flow<Int> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HINT_COUNT] ?: 5
    }

    override suspend fun updateLanguage(language: Language) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.code
        }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    override suspend fun updateTutorialCompleted(completed: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] = completed
        }
    }

    override suspend fun updateHintCount(count: Int) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.HINT_COUNT] = count
        }
    }

    override suspend fun updatePremiumStatus(isPremium: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }
}
