package com.rukinpavel.wordlyapp.feature.game

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
    val targetWord: String = ""
)

sealed interface GameUiEvent {
    data class OnKeyClick(val char: Char) : GameUiEvent
    object OnDeleteClick : GameUiEvent
    object OnEnterClick : GameUiEvent
    object OnPlayAgainClick : GameUiEvent
}

sealed interface GameSideEffect {
    data class ShowError(val message: String) : GameSideEffect
    object GameFinished : GameSideEffect
}
