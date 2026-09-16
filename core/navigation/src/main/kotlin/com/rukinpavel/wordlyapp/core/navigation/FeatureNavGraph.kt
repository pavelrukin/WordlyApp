package com.rukinpavel.wordlyapp.core.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject

class FeatureNavGraph @Inject constructor(private val features: Set<@JvmSuppressWildcards FeatureNavigation>) {
    fun getEntry(
        key: Any,
        onNavigate: (Any) -> Unit,
        onBack: () -> Unit,
    ): NavEntry<NavKey> = features.find { it.supports(key) }?.createEntry(key, onNavigate, onBack)
        ?: throw IllegalArgumentException("No feature found for key $key")
}
