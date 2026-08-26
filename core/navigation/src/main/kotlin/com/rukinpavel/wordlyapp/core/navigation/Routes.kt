package com.rukinpavel.wordlyapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object GameRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data object OnboardingRoute : NavKey
