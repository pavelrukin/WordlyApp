package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.core.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository,
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _effect = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        appPreferencesRepository.language
            .onEach { language ->
                val resolvedLanguage = language ?: Language.getSystemLanguage()
                _state.update { it.copy(language = resolvedLanguage) }
            }.launchIn(viewModelScope)

        appPreferencesRepository.vibrationEnabled
            .onEach { enabled ->
                _state.update { it.copy(vibrationEnabled = enabled) }
            }.launchIn(viewModelScope)

        appPreferencesRepository.isPremium
            .onEach { isPremium ->
                _state.update { it.copy(isPremium = isPremium) }
            }.launchIn(viewModelScope)

        billingRepository.subscriptionOptions
            .onEach { options ->
                _state.update { it.copy(subscriptionOptions = options) }
            }.launchIn(viewModelScope)
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OnLanguageChange -> {
                viewModelScope.launch {
                    appPreferencesRepository.updateLanguage(intent.language)
                }
            }
            is SettingsIntent.OnVibrationChange -> {
                viewModelScope.launch {
                    appPreferencesRepository.updateVibrationEnabled(intent.enabled)
                }
            }
            SettingsIntent.OnRepeatTutorialClick -> {
                viewModelScope.launch {
                    appPreferencesRepository.updateTutorialCompleted(false)
                    _effect.send(SettingsEffect.NavigateToOnboarding)
                }
            }
            is SettingsIntent.OnPurchasePremiumClick -> {
                viewModelScope.launch {
                    billingRepository.purchaseSubscription(intent.option)
                }
            }
        }
    }
}
