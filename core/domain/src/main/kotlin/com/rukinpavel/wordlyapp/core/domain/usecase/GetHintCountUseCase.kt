package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetHintCountUseCase
@Inject
constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Int> = repository.hintCount
}
