package com.rukinpavel.wordlyapp.core.domain.usecase

import com.rukinpavel.wordlyapp.core.domain.repository.UserPreferencesRepository
import com.rukinpavel.wordlyapp.core.model.Language
import javax.inject.Inject

class UpdateLanguageUseCase
@Inject
constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(language: Language) {
        repository.updateLanguage(language)
    }
}
