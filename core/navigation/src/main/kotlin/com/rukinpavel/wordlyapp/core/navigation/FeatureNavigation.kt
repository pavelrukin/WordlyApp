package com.rukinpavel.wordlyapp.core.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey

interface FeatureNavigation {
    fun supports(key: NavKey): Boolean
    fun createEntry(key: NavKey, onNavigate: (NavKey) -> Unit, onBack: () -> Unit): NavEntry<NavKey>
}
