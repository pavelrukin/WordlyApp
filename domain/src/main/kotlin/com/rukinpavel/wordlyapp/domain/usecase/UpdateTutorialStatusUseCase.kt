package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateTutorialStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        repository.updateTutorialCompleted(completed)
    }
}
