package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateHintCountUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(count: Int) {
        repository.updateHintCount(count)
    }
}
