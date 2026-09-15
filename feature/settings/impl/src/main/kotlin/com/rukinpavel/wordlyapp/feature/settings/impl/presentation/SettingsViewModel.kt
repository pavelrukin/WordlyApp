package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.domain.usecase.GetLanguageUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.GetVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.IsPremiumUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdateTutorialStatusUseCase
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdateLanguageUseCase
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdatePremiumStatusUseCase
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdateVibrationEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val getVibrationEnabledUseCase: GetVibrationEnabledUseCase,
    private val updateVibrationEnabledUseCase: UpdateVibrationEnabledUseCase,
    private val updateTutorialStatusUseCase: UpdateTutorialStatusUseCase,
    private val isPremiumUseCase: IsPremiumUseCase,
    private val updatePremiumStatusUseCase: UpdatePremiumStatusUseCase,
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _effect = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        getLanguageUseCase()
            .onEach { language ->
                val resolvedLanguage = language ?: Language.getSystemLanguage()
                _state.update { it.copy(language = resolvedLanguage) }
            }.launchIn(viewModelScope)

        getVibrationEnabledUseCase()
            .onEach { enabled ->
                _state.update { it.copy(vibrationEnabled = enabled) }
            }.launchIn(viewModelScope)

        isPremiumUseCase()
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
                    updateLanguageUseCase(intent.language)
                }
            }
            is SettingsIntent.OnVibrationChange -> {
                viewModelScope.launch {
                    updateVibrationEnabledUseCase(intent.enabled)
                }
            }
            SettingsIntent.OnRepeatTutorialClick -> {
                viewModelScope.launch {
                    updateTutorialStatusUseCase(false)
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
