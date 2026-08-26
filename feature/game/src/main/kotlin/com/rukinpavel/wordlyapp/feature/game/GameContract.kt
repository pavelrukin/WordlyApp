package com.rukinpavel.wordlyapp.feature.game

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.LetterState

data class BoardLetter(
    val char: Char = ' ',
    val state: LetterState = LetterState.INITIAL
)

enum class GameStatus {
    PLAYING, WON, LOST
}

data class GameUiState(
    val board: List<List<BoardLetter>> = List(6) { List(5) { BoardLetter() } },
    val currentRow: Int = 0,
    val currentGuess: String = "",
    val gameStatus: GameStatus = GameStatus.PLAYING,
    val keyboardLetterStates: Map<Char, LetterState> = emptyMap(),
    val isLoading: Boolean = true,
    val targetWord: String = "",
    val language: Language = Language.EN,
    val vibrationEnabled: Boolean = true,
    val revealedHints: Map<Int, Char> = emptyMap(),
    val hintCount: Int = 5,
    val showAdDialog: Boolean = false,
    val isPremium: Boolean = false
)

sealed interface GameUiEvent {
    data class OnKeyClick(val char: Char) : GameUiEvent
    object OnDeleteClick : GameUiEvent
    object OnEnterClick : GameUiEvent
    object OnPlayAgainClick : GameUiEvent
    object OnHintClick : GameUiEvent
    object OnWatchAdClick : GameUiEvent
    object OnDismissAdDialog : GameUiEvent
}

sealed interface GameSideEffect {
    data class ShowError(val messageRes: Int, val args: List<Any> = emptyList()) : GameSideEffect
    object GameFinished : GameSideEffect
}
