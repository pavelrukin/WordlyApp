package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class IsTutorialCompletedUseCase @Inject constructor(private val repository: AppPreferencesRepository) {
    operator fun invoke(): Flow<Boolean> = repository.tutorialCompleted
}
