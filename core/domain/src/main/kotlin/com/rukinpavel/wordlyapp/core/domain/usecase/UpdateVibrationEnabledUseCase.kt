package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateVibrationEnabledUseCase
@Inject
constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.updateVibrationEnabled(enabled)
    }
}
