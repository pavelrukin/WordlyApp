package com.rukinpavel.wordlyapp.domain

import com.rukinpavel.wordlyapp.core.model.Language
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: Language) {
        repository.updateLanguage(language)
    }
}
