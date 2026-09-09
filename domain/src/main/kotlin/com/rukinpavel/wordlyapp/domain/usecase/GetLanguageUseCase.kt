package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetLanguageUseCase
@Inject
constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Language?> = repository.language
}
