package com.rukinpavel.wordlyapp.domain

import javax.inject.Inject

class UpdatePremiumStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(isPremium: Boolean) {
        repository.updatePremiumStatus(isPremium)
    }
}
