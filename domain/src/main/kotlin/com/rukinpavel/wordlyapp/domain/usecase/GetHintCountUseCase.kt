package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHintCountUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Int> = repository.hintCount
}
