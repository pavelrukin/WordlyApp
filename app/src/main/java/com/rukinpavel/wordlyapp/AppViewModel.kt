package com.rukinpavel.wordlyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.domain.IsTutorialCompletedUseCase
import com.rukinpavel.wordlyapp.domain.UpdateTutorialStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.GetLanguageUseCase

@HiltViewModel
class AppViewModel @Inject constructor(
    private val isTutorialCompletedUseCase: IsTutorialCompletedUseCase,
    private val updateTutorialStatusUseCase: UpdateTutorialStatusUseCase,
    private val getLanguageUseCase: GetLanguageUseCase
) : ViewModel() {

    val isTutorialCompleted: StateFlow<Boolean> = isTutorialCompletedUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language: StateFlow<Language?> = getLanguageUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun completeTutorial() {
        viewModelScope.launch {
            updateTutorialStatusUseCase(true)
        }
    }
}
