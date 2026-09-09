package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class IsPremiumUseCase
@Inject
constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isPremium
}
