package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(language: Language) {
        repository.updateLanguage(language)
    }
}
