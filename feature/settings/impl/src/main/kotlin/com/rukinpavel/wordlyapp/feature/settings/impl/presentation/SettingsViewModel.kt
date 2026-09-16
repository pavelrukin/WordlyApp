package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.core.domain.usecase.GetLanguageUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.GetVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.IsPremiumUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdateLanguageUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdatePremiumStatusUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdateTutorialStatusUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdateVibrationEnabledUseCase
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
