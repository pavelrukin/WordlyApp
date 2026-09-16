package com.rukinpavel.wordlyapp.core.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey

interface FeatureNavigation {
    fun supports(key: Any): Boolean
    fun createEntry(key: Any, onNavigate: (Any) -> Unit, onBack: () -> Unit): NavEntry<NavKey>
}
