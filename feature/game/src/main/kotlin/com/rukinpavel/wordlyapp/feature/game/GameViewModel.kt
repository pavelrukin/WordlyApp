package com.rukinpavel.wordlyapp.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.domain.CheckGuessUseCase
import com.rukinpavel.wordlyapp.domain.GetHintCountUseCase
import com.rukinpavel.wordlyapp.domain.GetLanguageUseCase
import com.rukinpavel.wordlyapp.domain.GetVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.domain.IsPremiumUseCase
import com.rukinpavel.wordlyapp.domain.UpdateHintCountUseCase
import com.rukinpavel.wordlyapp.domain.ValidateWordUseCase
import com.rukinpavel.wordlyapp.domain.WordRepository
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val checkGuessUseCase: CheckGuessUseCase,
    private val validateWordUseCase: ValidateWordUseCase,
    private val wordRepository: WordRepository,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val getVibrationEnabledUseCase: GetVibrationEnabledUseCase,
    private val getHintCountUseCase: GetHintCountUseCase,
    private val updateHintCountUseCase: UpdateHintCountUseCase,
    private val isPremiumUseCase: IsPremiumUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<GameSideEffect>()
    val sideEffect: SharedFlow<GameSideEffect> = _sideEffect.asSharedFlow()

    private var targetWord: String = ""

    init {
        getLanguageUseCase().onEach { language ->
            val resolvedLanguage = language ?: Language.getSystemLanguage()
            if (_uiState.value.language != resolvedLanguage || targetWord.isEmpty()) {
                _uiState.update { it.copy(language = resolvedLanguage) }
                resetGame()
            }
        }.launchIn(viewModelScope)

        getVibrationEnabledUseCase().onEach { enabled ->
            _uiState.update { it.copy(vibrationEnabled = enabled) }
        }.launchIn(viewModelScope)

        getHintCountUseCase().onEach { count ->
            _uiState.update { it.copy(hintCount = count) }
        }.launchIn(viewModelScope)

        isPremiumUseCase().onEach { isPremium ->
            _uiState.update { it.copy(isPremium = isPremium) }
        }.launchIn(viewModelScope)
    }

    private fun loadNewWord() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                targetWord = wordRepository.getRandomWord(_uiState.value.language).uppercase()
                if (targetWord.isEmpty()) {
                    throw Exception("Empty word received")
                }
                _uiState.update { it.copy(isLoading = false, targetWord = targetWord) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.failed_load_word))
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
            GameUiEvent.OnHintClick -> handleHintClick()
            GameUiEvent.OnWatchAdClick -> watchAd()
            GameUiEvent.OnDismissAdDialog -> _uiState.update { it.copy(showAdDialog = false) }
        }
    }

    private fun handleHintClick() {
        val currentState = _uiState.value
        if (currentState.gameStatus != GameStatus.PLAYING) return

        if (!currentState.isPremium && currentState.hintCount <= 0) {
            _uiState.update { it.copy(showAdDialog = true) }
            return
        }

        val unknownIndices = (0 until 5).filter { i ->
            !currentState.revealedHints.containsKey(i)
        }

        if (unknownIndices.isNotEmpty()) {
            val hintIdx = unknownIndices.random()
            val hintChar = targetWord[hintIdx]
            
            val newHints = currentState.revealedHints.toMutableMap()
            newHints[hintIdx] = hintChar
            
            val newKeyboardStates = currentState.keyboardLetterStates.toMutableMap()
            newKeyboardStates[hintChar] = LetterState.CORRECT
            
            if (!currentState.isPremium) {
                viewModelScope.launch {
                    updateHintCountUseCase(currentState.hintCount - 1)
                }
            }

            _uiState.update { 
                it.copy(
                    revealedHints = newHints, 
                    keyboardLetterStates = newKeyboardStates
                ) 
            }
            
            viewModelScope.launch {
                _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.hint_message, listOf(hintChar)))
            }
            
            updateBoardWithCurrentGuess(currentState.currentGuess)
        } else {
            viewModelScope.launch {
                _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.all_hints_revealed))
            }
        }
    }

    private fun watchAd() {
        _uiState.update { it.copy(showAdDialog = false, isLoading = true) }
        viewModelScope.launch {
            // Simulate ad watching
            delay(2000)
            val newCount = _uiState.value.hintCount + 3
            updateHintCountUseCase(newCount)
            _uiState.update { it.copy(isLoading = false) }
            _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.extra_hints_awarded))
        }
    }

    private fun resetGame() {
        _uiState.update { 
            GameUiState(
                language = it.language,
                vibrationEnabled = it.vibrationEnabled,
                hintCount = it.hintCount,
                isPremium = it.isPremium
            )
        }
        loadNewWord()
    }

    private fun handleKeyClick(char: Char) {
        val currentState = _uiState.value
        val maxTypedLetters = 5 - currentState.revealedHints.size
        if (currentState.currentGuess.length < maxTypedLetters) {
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

    private fun updateBoardWithCurrentGuess(typedLetters: String) {
        val currentState = _uiState.value
        val hints = currentState.revealedHints
        
        val newBoard = currentState.board.mapIndexed { rowIndex, row ->
            if (rowIndex == currentState.currentRow) {
                var typedIdx = 0
                List(5) { colIndex ->
                    if (hints.containsKey(colIndex)) {
                        BoardLetter(hints[colIndex]!!, LetterState.CORRECT)
                    } else if (typedIdx < typedLetters.length) {
                        BoardLetter(typedLetters[typedIdx++], LetterState.INITIAL)
                    } else {
                        BoardLetter()
                    }
                }
            } else {
                row
            }
        }

        _uiState.update { it.copy(board = newBoard, currentGuess = typedLetters) }
    }

    private fun handleEnterClick() {
        val currentState = _uiState.value
        val typed = currentState.currentGuess
        val hints = currentState.revealedHints
        
        val fullGuessBuilder = StringBuilder()
        var typedIdx = 0
        for (i in 0 until 5) {
            if (hints.containsKey(i)) {
                fullGuessBuilder.append(hints[i])
            } else if (typedIdx < typed.length) {
                fullGuessBuilder.append(typed[typedIdx++])
            }
        }
        
        val guess = fullGuessBuilder.toString()

    if (guess.length < 5) {
        viewModelScope.launch {
            _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.not_enough_letters))
        }
        return
    }

    if (!validateWordUseCase(guess)) {
        viewModelScope.launch {
            _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.not_in_word_list))
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

        val newHints = currentState.revealedHints.toMutableMap()
        for (i in result.indices) {
            if (result[i] == LetterState.CORRECT) {
                newHints[i] = guess[i]
            }
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                currentRow = currentState.currentRow + 1,
                currentGuess = "",
                gameStatus = newStatus,
                keyboardLetterStates = newKeyboardStates,
                revealedHints = newHints
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
