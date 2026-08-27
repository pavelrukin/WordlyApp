package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Language?> = repository.language
}
