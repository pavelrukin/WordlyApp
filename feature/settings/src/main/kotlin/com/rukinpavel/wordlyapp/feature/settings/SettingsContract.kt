package com.rukinpavel.wordlyapp.feature.settings

import com.rukinpavel.wordlyapp.core.model.Language

data class SettingsUiState(
    val language: Language? = null,
    val vibrationEnabled: Boolean = true
)

sealed interface SettingsUiEvent {
    data class OnLanguageChange(val language: Language) : SettingsUiEvent
    data class OnVibrationChange(val enabled: Boolean) : SettingsUiEvent
    object OnRepeatTutorialClick : SettingsUiEvent
}
