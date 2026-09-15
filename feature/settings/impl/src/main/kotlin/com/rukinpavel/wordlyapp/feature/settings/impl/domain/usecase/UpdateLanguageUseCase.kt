package com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.SettingsPreferencesRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val repository: SettingsPreferencesRepository
) {
    suspend operator fun invoke(language: Language) {
        repository.updateLanguage(language)
    }
}
