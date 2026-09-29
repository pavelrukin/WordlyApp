package com.rukinpavel.wordlyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel
@Inject
constructor(private val appPreferencesRepository: AppPreferencesRepository) : ViewModel() {
    val isTutorialCompleted: StateFlow<Boolean?> =
        appPreferencesRepository.tutorialCompleted
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val language: StateFlow<Language?> =
        appPreferencesRepository.language
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun completeTutorial() {
        viewModelScope.launch {
            appPreferencesRepository.updateTutorialCompleted(true)
        }
    }
}
