package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPremiumUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isPremium
}
