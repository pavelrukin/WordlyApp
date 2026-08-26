package com.rukinpavel.wordlyapp.feature.settings

import com.rukinpavel.wordlyapp.core.model.LetterState

enum class TutorialStep {
    Introduction,
    Typing,
    Checking,
    ExplainingCorrect,
    ExplainingWrongPosition,
    ExplainingNotInWord,
    ExplainingHint,
    Completed
}

data class OnboardingUiState(
    val currentStep: TutorialStep = TutorialStep.Introduction,
    val visibleLetters: Int = 0,
    val revealedTiles: Int = 0,
    val isAnimationRunning: Boolean = false,
    val isAnimationFinished: Boolean = false,
    val canNavigateNext: Boolean = true,
    val tutorialWord: String = "WORDS",
    val tutorialStates: List<LetterState> = listOf(
        LetterState.CORRECT,
        LetterState.CORRECT,
        LetterState.WRONG_POSITION,
        LetterState.NOT_IN_WORD,
        LetterState.NOT_IN_WORD
    )
)

sealed interface OnboardingEvent {
    data class UpdateTutorialWord(val word: String) : OnboardingEvent
    object NextStep : OnboardingEvent
    object PlayAgain : OnboardingEvent
    object CompleteOnboarding : OnboardingEvent
}
