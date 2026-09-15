package com.rukinpavel.wordlyapp.feature.settings.impl

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.core.navigation.OnboardingRoute
import com.rukinpavel.wordlyapp.feature.settings.api.SettingsRoute
import com.rukinpavel.wordlyapp.feature.settings.impl.presentation.SettingsScreen
import javax.inject.Inject

class SettingsNavigation @Inject constructor() : FeatureNavigation {
    override fun supports(key: Any): Boolean = key is SettingsRoute

    override fun createEntry(
        key: Any,
        onNavigate: (Any) -> Unit,
        onBack: () -> Unit
    ): NavEntry<NavKey> {
        return NavEntry<NavKey>(key as NavKey) {
            SettingsScreen(
                onBackClick = onBack,
                onNavigateToOnboarding = { onNavigate(OnboardingRoute) }
            )
        }
    }
}
