package com.rukinpavel.wordlyapp.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsTutorialCompletedUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.tutorialCompleted
}
