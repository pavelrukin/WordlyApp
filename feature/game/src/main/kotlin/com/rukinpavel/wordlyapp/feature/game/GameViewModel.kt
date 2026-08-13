package com.rukinpavel.wordlyapp.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.domain.CheckGuessUseCase
import com.rukinpavel.wordlyapp.domain.ValidateWordUseCase
import com.rukinpavel.wordlyapp.domain.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val checkGuessUseCase: CheckGuessUseCase,
    private val validateWordUseCase: ValidateWordUseCase,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<GameSideEffect>()
    val sideEffect: SharedFlow<GameSideEffect> = _sideEffect.asSharedFlow()

    private var targetWord: String = ""

    init {
        loadNewWord()
    }

    private fun loadNewWord() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                targetWord = wordRepository.getRandomWord().uppercase()
                if (targetWord.isEmpty()) {
                    throw Exception("Empty word received")
                }
                _uiState.update { it.copy(isLoading = false, targetWord = targetWord) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _sideEffect.emit(GameSideEffect.ShowError("Failed to load word. Please try again."))
            }
        }
    }

    fun onEvent(event: GameUiEvent) {
        when (event) {
            is GameUiEvent.OnKeyClick -> {
                if (_uiState.value.gameStatus == GameStatus.PLAYING) {
                    handleKeyClick(event.char)
                }
            }
            GameUiEvent.OnDeleteClick -> {
                if (_uiState.value.gameStatus == GameStatus.PLAYING) {
                    handleDeleteClick()
                }
            }
            GameUiEvent.OnEnterClick -> {
                if (_uiState.value.gameStatus == GameStatus.PLAYING) {
                    handleEnterClick()
                }
            }
            GameUiEvent.OnPlayAgainClick -> resetGame()
        }
    }

    private fun resetGame() {
        _uiState.update { GameUiState() }
        loadNewWord()
    }

    private fun handleKeyClick(char: Char) {
        val currentState = _uiState.value
        if (currentState.currentGuess.length < 5) {
            val newGuess = currentState.currentGuess + char.uppercaseChar()
            updateBoardWithCurrentGuess(newGuess)
        }
    }

    private fun handleDeleteClick() {
        val currentState = _uiState.value
        if (currentState.currentGuess.isNotEmpty()) {
            val newGuess = currentState.currentGuess.dropLast(1)
            updateBoardWithCurrentGuess(newGuess)
        }
    }

    private fun updateBoardWithCurrentGuess(newGuess: String) {
        val currentState = _uiState.value
        val newBoard = currentState.board.mapIndexed { rowIndex, row ->
            if (rowIndex == currentState.currentRow) {
                List(5) { colIndex ->
                    if (colIndex < newGuess.length) {
                        BoardLetter(newGuess[colIndex], LetterState.INITIAL)
                    } else {
                        BoardLetter()
                    }
                }
            } else {
                row
            }
        }

        _uiState.update { it.copy(board = newBoard, currentGuess = newGuess) }
    }

    private fun handleEnterClick() {
        val currentState = _uiState.value
        val guess = currentState.currentGuess

        if (guess.length < 5) {
            viewModelScope.launch {
                _sideEffect.emit(GameSideEffect.ShowError("Not enough letters"))
            }
            return
        }

        if (!validateWordUseCase(guess)) {
            viewModelScope.launch {
                _sideEffect.emit(GameSideEffect.ShowError("Not in word list"))
            }
            return
        }

        val result = checkGuessUseCase(targetWord, guess)
        val newBoard = currentState.board.mapIndexed { rowIndex, row ->
            if (rowIndex == currentState.currentRow) {
                List(5) { colIndex ->
                    BoardLetter(guess[colIndex], result[colIndex])
                }
            } else {
                row
            }
        }

        val newKeyboardStates = currentState.keyboardLetterStates.toMutableMap()
        for (i in 0 until 5) {
            val char = guess[i]
            val newState = result[i]
            val oldState = newKeyboardStates[char]
            
            if (shouldUpdateKeyboardState(oldState, newState)) {
                newKeyboardStates[char] = newState
            }
        }

        val isWin = result.all { it == LetterState.CORRECT }
        val isLastAttempt = currentState.currentRow == 5
        
        val newStatus = when {
            isWin -> GameStatus.WON
            isLastAttempt -> GameStatus.LOST
            else -> GameStatus.PLAYING
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                currentRow = currentState.currentRow + 1,
                currentGuess = "",
                gameStatus = newStatus,
                keyboardLetterStates = newKeyboardStates
            )
        }

        if (newStatus != GameStatus.PLAYING) {
            viewModelScope.launch {
                _sideEffect.emit(GameSideEffect.GameFinished)
            }
        }
    }

    private fun shouldUpdateKeyboardState(oldState: LetterState?, newState: LetterState): Boolean {
        if (oldState == null) return true
        if (oldState == LetterState.CORRECT) return false
        if (newState == LetterState.CORRECT) return true
        if (oldState == LetterState.WRONG_POSITION) return false
        if (newState == LetterState.WRONG_POSITION) return true
        return false
    }
}


