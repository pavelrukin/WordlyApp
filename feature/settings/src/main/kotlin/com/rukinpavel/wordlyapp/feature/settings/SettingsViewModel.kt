package com.rukinpavel.wordlyapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.domain.GetLanguageUseCase
import com.rukinpavel.wordlyapp.domain.GetVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.domain.UpdateLanguageUseCase
import com.rukinpavel.wordlyapp.domain.UpdateTutorialStatusUseCase
import com.rukinpavel.wordlyapp.domain.UpdateVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.core.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val getVibrationEnabledUseCase: GetVibrationEnabledUseCase,
    private val updateVibrationEnabledUseCase: UpdateVibrationEnabledUseCase,
    private val updateTutorialStatusUseCase: UpdateTutorialStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SettingsSideEffect>()
    val sideEffect: SharedFlow<SettingsSideEffect> = _sideEffect.asSharedFlow()

    init {
        getLanguageUseCase().onEach { language ->
            val resolvedLanguage = language ?: Language.getSystemLanguage()
            _uiState.update { it.copy(language = resolvedLanguage) }
        }.launchIn(viewModelScope)

        getVibrationEnabledUseCase().onEach { enabled ->
            _uiState.update { it.copy(vibrationEnabled = enabled) }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.OnLanguageChange -> {
                viewModelScope.launch {
                    updateLanguageUseCase(event.language)
                }
            }
            is SettingsUiEvent.OnVibrationChange -> {
                viewModelScope.launch {
                    updateVibrationEnabledUseCase(event.enabled)
                }
            }
            SettingsUiEvent.OnRepeatTutorialClick -> {
                viewModelScope.launch {
                    updateTutorialStatusUseCase(false)
                    _sideEffect.emit(SettingsSideEffect.NavigateToOnboarding)
                }
            }
        }
    }
}

sealed interface SettingsSideEffect {
    object NavigateToOnboarding : SettingsSideEffect
}
