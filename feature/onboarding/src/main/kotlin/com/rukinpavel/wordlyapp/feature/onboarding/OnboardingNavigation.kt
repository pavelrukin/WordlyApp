package com.rukinpavel.wordlyapp.feature.onboarding

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.core.navigation.OnboardingRoute
import javax.inject.Inject

class OnboardingNavigation @Inject constructor() : FeatureNavigation {
    override fun supports(key: Any): Boolean = key is OnboardingRoute

    override fun createEntry(
        key: Any,
        onNavigate: (Any) -> Unit,
        onBack: () -> Unit,
    ): NavEntry<NavKey> = NavEntry<NavKey>(key as NavKey) {
        OnboardingScreen(
            onComplete = onBack,
        )
    }
}
