package com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase

import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.SettingsPreferencesRepository
import javax.inject.Inject

class UpdatePremiumStatusUseCase @Inject constructor(
    private val repository: SettingsPreferencesRepository
) {
    suspend operator fun invoke(isPremium: Boolean) {
        repository.updatePremiumStatus(isPremium)
    }
}
