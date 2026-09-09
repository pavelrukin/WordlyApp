package com.rukinpavel.wordlyapp.feature.settings

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.SubscriptionOption

data class SettingsUiState(
    val language: Language? = null,
    val vibrationEnabled: Boolean = true,
    val isPremium: Boolean = false,
    val subscriptionOptions: List<SubscriptionOption> = emptyList(),
)

sealed interface SettingsUiEvent {
    data class OnLanguageChange(val language: Language) : SettingsUiEvent

    data class OnVibrationChange(val enabled: Boolean) : SettingsUiEvent

    object OnRepeatTutorialClick : SettingsUiEvent

    data class OnPurchasePremiumClick(val option: SubscriptionOption) : SettingsUiEvent
}
