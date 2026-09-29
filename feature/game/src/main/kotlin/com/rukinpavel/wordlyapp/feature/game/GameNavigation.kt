package com.rukinpavel.wordlyapp.feature.game

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.rukinpavel.wordlyapp.core.navigation.FeatureNavigation
import com.rukinpavel.wordlyapp.core.navigation.GameRoute
import com.rukinpavel.wordlyapp.feature.settings.api.SettingsRoute
import javax.inject.Inject

class GameNavigation @Inject constructor() : FeatureNavigation {
    override fun supports(key: NavKey): Boolean = key is GameRoute

    override fun createEntry(
        key: NavKey,
        onNavigate: (NavKey) -> Unit,
        onBack: () -> Unit,
    ): NavEntry<NavKey> = NavEntry(key) {
        GameScreen(
            onSettingsClick = { onNavigate(SettingsRoute) },
        )
    }
}
