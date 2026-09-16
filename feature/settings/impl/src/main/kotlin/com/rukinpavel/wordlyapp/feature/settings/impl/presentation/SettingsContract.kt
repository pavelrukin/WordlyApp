package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.SubscriptionOption

data class SettingsState(
    val language: Language? = null,
    val vibrationEnabled: Boolean = true,
    val isPremium: Boolean = false,
    val subscriptionOptions: List<SubscriptionOption> = emptyList(),
)

sealed interface SettingsIntent {
    data class OnLanguageChange(val language: Language) : SettingsIntent
    data class OnVibrationChange(val enabled: Boolean) : SettingsIntent
    data object OnRepeatTutorialClick : SettingsIntent
    data class OnPurchasePremiumClick(val option: SubscriptionOption) : SettingsIntent
}

sealed interface SettingsEffect {
    data object NavigateToOnboarding : SettingsEffect
}
