package com.rukinpavel.wordlyapp.domain

import com.rukinpavel.wordlyapp.core.model.Language
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Language?> = repository.language
}
