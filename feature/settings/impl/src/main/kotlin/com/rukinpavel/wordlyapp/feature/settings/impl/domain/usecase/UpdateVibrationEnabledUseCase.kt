package com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase

import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.SettingsPreferencesRepository
import javax.inject.Inject

class UpdateVibrationEnabledUseCase @Inject constructor(
    private val repository: SettingsPreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.updateVibrationEnabled(enabled)
    }
}
