package com.rukinpavel.wordlyapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreUserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        val HINT_COUNT = intPreferencesKey("hint_count")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
    }

    override val language: Flow<Language?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LANGUAGE]?.let { Language.fromCode(it) }
        }


    override val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
        }

    override val tutorialCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] ?: false
        }

    override val hintCount: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HINT_COUNT] ?: 5
        }

    override val isPremium: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] ?: false
        }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.code
        }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    override suspend fun updateTutorialCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] = completed
        }
    }

    override suspend fun updateHintCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HINT_COUNT] = count
        }
    }

    override suspend fun updatePremiumStatus(isPremium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }
}
