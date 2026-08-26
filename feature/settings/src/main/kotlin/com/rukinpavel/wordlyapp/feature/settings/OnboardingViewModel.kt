package com.rukinpavel.wordlyapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.domain.UpdateTutorialStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateTutorialStatusUseCase: UpdateTutorialStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var animationJob: Job? = null

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.UpdateTutorialWord -> {
                if (_uiState.value.tutorialWord != event.word) {
                    _uiState.update { it.copy(tutorialWord = event.word) }
                    resetTutorial()
                }
            }
            OnboardingEvent.NextStep -> handleNextStep()
            OnboardingEvent.PlayAgain -> resetTutorial()
            OnboardingEvent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun handleNextStep() {
        val currentState = _uiState.value
        if (!currentState.canNavigateNext) return

        when (currentState.currentStep) {
            TutorialStep.Introduction -> startTyping()
            TutorialStep.Typing -> startChecking()
            TutorialStep.Checking -> transitionTo(TutorialStep.ExplainingCorrect)
            TutorialStep.ExplainingCorrect -> transitionTo(TutorialStep.ExplainingWrongPosition)
            TutorialStep.ExplainingWrongPosition -> transitionTo(TutorialStep.ExplainingNotInWord)
            TutorialStep.ExplainingNotInWord -> transitionTo(TutorialStep.ExplainingHint)
            TutorialStep.ExplainingHint -> finishTutorial()
            TutorialStep.Completed -> {}
        }
    }

    private fun startTyping() {
        animationJob?.cancel()
        _uiState.update { 
            it.copy(
                currentStep = TutorialStep.Typing,
                canNavigateNext = false,
                isAnimationRunning = true
            ) 
        }
        
        animationJob = viewModelScope.launch {
            for (i in 1..5) {
                delay(300)
                _uiState.update { it.copy(visibleLetters = i) }
            }
            delay(200)
            _uiState.update { 
                it.copy(
                    canNavigateNext = true,
                    isAnimationRunning = false
                ) 
            }
        }
    }

    private fun startChecking() {
        animationJob?.cancel()
        _uiState.update { 
            it.copy(
                currentStep = TutorialStep.Checking,
                canNavigateNext = false,
                isAnimationRunning = true
            ) 
        }
        
        animationJob = viewModelScope.launch {
            for (i in 1..5) {
                delay(400)
                _uiState.update { it.copy(revealedTiles = i) }
            }
            delay(200)
            _uiState.update { 
                it.copy(
                    canNavigateNext = true,
                    isAnimationRunning = false
                ) 
            }
        }
    }

    private fun transitionTo(step: TutorialStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    private fun finishTutorial() {
        _uiState.update { 
            it.copy(
                currentStep = TutorialStep.Completed,
                isAnimationFinished = true,
                canNavigateNext = false
            ) 
        }
    }

    private fun resetTutorial() {
        animationJob?.cancel()
        _uiState.update { OnboardingUiState() }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            updateTutorialStatusUseCase(true)
        }
    }
}
