package com.rukinpavel.wordlyapp.domain

import javax.inject.Inject

class UpdateHintCountUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(count: Int) {
        repository.updateHintCount(count)
    }
}
