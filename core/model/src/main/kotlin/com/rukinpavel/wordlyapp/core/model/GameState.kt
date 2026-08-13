package com.rukinpavel.wordlyapp.core.model

data class GameState(
    val guesses: List<String> = emptyList(),
    val results: List<GuessResult> = emptyList(),
    val targetWord: String = "",
    val currentAttempt: Int = 0,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false
)
