package com.rukinpavel.wordlyapp.domain

import javax.inject.Inject

class UpdateVibrationEnabledUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.updateVibrationEnabled(enabled)
    }
}
