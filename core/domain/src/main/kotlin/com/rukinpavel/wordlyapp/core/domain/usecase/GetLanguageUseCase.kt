package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetLanguageUseCase @Inject constructor(private val repository: AppPreferencesRepository) {
    operator fun invoke(): Flow<Language?> = repository.language
}
