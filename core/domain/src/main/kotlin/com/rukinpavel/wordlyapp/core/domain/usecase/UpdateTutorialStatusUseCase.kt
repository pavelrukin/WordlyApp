package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import javax.inject.Inject

class UpdateTutorialStatusUseCase @Inject constructor(private val repository: AppPreferencesRepository) {
    suspend operator fun invoke(completed: Boolean) {
        repository.updateTutorialCompleted(completed)
    }
}
