package com.rukinpavel.wordlyapp.feature.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rukinpavel.wordlyapp.core.domain.game.GameEngine
import com.rukinpavel.wordlyapp.core.domain.repository.AdManager
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.WordRepository
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR
import com.rukinpavel.wordlyapp.feature.game.domain.usecase.ValidateWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameEngine: GameEngine,
    private val validateWordUseCase: ValidateWordUseCase,
    private val wordRepository: WordRepository,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val adManager: AdManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<GameSideEffect>()
    val sideEffect: SharedFlow<GameSideEffect> = _sideEffect.asSharedFlow()

    private var targetWord: String = ""

    init {
        val restored = restoreStateFromHandle()
        if (restored != null) {
            targetWord = restored.second
            _uiState.value = restored.first
        }

        appPreferencesRepository.language
            .onEach { language ->
                val resolvedLanguage = language ?: Language.getSystemLanguage()
                val languageChanged = _uiState.value.language != resolvedLanguage
                if (languageChanged || targetWord.isEmpty()) {
                    _uiState.update { it.copy(language = resolvedLanguage) }
                    resetGame()
                }
            }.launchIn(viewModelScope)

        appPreferencesRepository.vibrationEnabled
            .onEach { enabled ->
                _uiState.update { it.copy(vibrationEnabled = enabled) }
            }.launchIn(viewModelScope)

        appPreferencesRepository.hintCount
            .onEach { count ->
                _uiState.update { it.copy(hintCount = count) }
            }.launchIn(viewModelScope)

        appPreferencesRepository.isPremium
            .onEach { isPremium ->
                _uiState.update { it.copy(isPremium = isPremium) }
            }.launchIn(viewModelScope)
    }

    private fun saveStateToHandle(state: GameUiState, targetWord: String) {
        if (targetWord.isEmpty()) return
        savedStateHandle[KEY_TARGET_WORD] = targetWord
        savedStateHandle[KEY_LANGUAGE] = state.language.name
        savedStateHandle[KEY_CURRENT_ROW] = state.currentRow
        savedStateHandle[KEY_CURRENT_GUESS] = state.currentGuess
        savedStateHandle[KEY_GAME_STATUS] = state.gameStatus.name

        savedStateHandle[KEY_HINTS_KEYS] = state.revealedHints.keys.toIntArray()
        savedStateHandle[KEY_HINTS_VALS] = state.revealedHints.values.toCharArray()

        val boardChars = CharArray(30)
        val boardStates = IntArray(30)
        for (r in 0 until 6) {
            for (c in 0 until 5) {
                val letter = state.board[r][c]
                val idx = r * 5 + c
                boardChars[idx] = letter.char
                boardStates[idx] = letter.state.ordinal
            }
        }
        savedStateHandle[KEY_BOARD_CHARS] = boardChars
        savedStateHandle[KEY_BOARD_STATES] = boardStates

        savedStateHandle[KEY_KB_KEYS] = state.keyboardLetterStates.keys.toCharArray()
        savedStateHandle[KEY_KB_VALS] = state.keyboardLetterStates.values.map { it.ordinal }.toIntArray()
    }

    private fun restoreStateFromHandle(): Pair<GameUiState, String>? {
        val savedTargetWord = savedStateHandle.get<String>(KEY_TARGET_WORD) ?: return null
        if (savedTargetWord.isEmpty()) return null

        val savedLangName = savedStateHandle.get<String>(KEY_LANGUAGE)
        val savedLanguage = try {
            if (savedLangName != null) Language.valueOf(savedLangName) else Language.EN
        } catch (e: Exception) {
            Language.EN
        }

        val currentRow = savedStateHandle.get<Int>(KEY_CURRENT_ROW) ?: 0
        val currentGuess = savedStateHandle.get<String>(KEY_CURRENT_GUESS) ?: ""
        val statusName = savedStateHandle.get<String>(KEY_GAME_STATUS) ?: GameStatus.PLAYING.name
        val gameStatus = try {
            GameStatus.valueOf(statusName)
        } catch (e: Exception) {
            GameStatus.PLAYING
        }

        val hintsKeys = savedStateHandle.get<IntArray>(KEY_HINTS_KEYS)
        val hintsVals = savedStateHandle.get<CharArray>(KEY_HINTS_VALS)
        val hints = mutableMapOf<Int, Char>()
        if (hintsKeys != null && hintsVals != null && hintsKeys.size == hintsVals.size) {
            for (i in hintsKeys.indices) {
                hints[hintsKeys[i]] = hintsVals[i]
            }
        }

        val boardChars = savedStateHandle.get<CharArray>(KEY_BOARD_CHARS)
        val boardStates = savedStateHandle.get<IntArray>(KEY_BOARD_STATES)
        val board = List(6) { r ->
            List(5) { c ->
                val idx = r * 5 + c
                if (boardChars != null && boardStates != null && idx < boardChars.size && idx < boardStates.size) {
                    val char = boardChars[idx]
                    val stateOrdinal = boardStates[idx]
                    val state = LetterState.entries.getOrElse(stateOrdinal) { LetterState.INITIAL }
                    BoardLetter(char, state)
                } else {
                    BoardLetter()
                }
            }
        }

        val kbKeys = savedStateHandle.get<CharArray>(KEY_KB_KEYS)
        val kbVals = savedStateHandle.get<IntArray>(KEY_KB_VALS)
        val keyboardStates = mutableMapOf<Char, LetterState>()
        if (kbKeys != null && kbVals != null && kbKeys.size == kbVals.size) {
            for (i in kbKeys.indices) {
                val state = LetterState.entries.getOrElse(kbVals[i]) { LetterState.INITIAL }
                keyboardStates[kbKeys[i]] = state
            }
        }

        val restoredUiState = GameUiState(
            board = board,
            currentRow = currentRow,
            currentGuess = currentGuess,
            gameStatus = gameStatus,
            keyboardLetterStates = keyboardStates,
            isLoading = false,
            targetWord = savedTargetWord,
            language = savedLanguage,
            revealedHints = hints,
        )

        return Pair(restoredUiState, savedTargetWord)
    }

    private fun loadNewWord() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val language = _uiState.value.language
                targetWord = wordRepository.getRandomWord(language).uppercase()
                if (targetWord.isEmpty()) {
                    throw Exception("Empty word received")
                }
                val hint = wordRepository.getHint(targetWord, language)
                _uiState.update { it.copy(isLoading = false, targetWord = targetWord, wordHint = hint) }
                saveStateToHandle(_uiState.value, targetWord)
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
            GameUiEvent.OnDismissAdDialog -> {
                _uiState.update { it.copy(showAdDialog = false) }
                saveStateToHandle(_uiState.value, targetWord)
            }
        }
    }

    private fun handleHintClick() {
        val currentState = _uiState.value
        if (currentState.gameStatus != GameStatus.PLAYING) return

        if (!currentState.isPremium && currentState.hintCount <= 0) {
            _uiState.update { it.copy(showAdDialog = true) }
            saveStateToHandle(_uiState.value, targetWord)
            return
        }

        val hintIdx = gameEngine.pickHintIndex(5, currentState.revealedHints)
        if (hintIdx != null) {
            val hintChar = targetWord[hintIdx]
            val newHints = currentState.revealedHints.toMutableMap().also { it[hintIdx] = hintChar }
            val newKeyboard = currentState.keyboardLetterStates.toMutableMap().also {
                it[hintChar] = LetterState.CORRECT
            }
            if (!currentState.isPremium) {
                viewModelScope.launch {
                    appPreferencesRepository.updateHintCount(currentState.hintCount - 1)
                }
            }
            _uiState.update { it.copy(revealedHints = newHints, keyboardLetterStates = newKeyboard) }
            saveStateToHandle(_uiState.value, targetWord)
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
        saveStateToHandle(_uiState.value, targetWord)
        adManager.showRewardedAd(
            onRewarded = {
                viewModelScope.launch {
                    val newCount = _uiState.value.hintCount + 3
                    appPreferencesRepository.updateHintCount(newCount)
                    _uiState.update { it.copy(isLoading = false) }
                    saveStateToHandle(_uiState.value, targetWord)
                    _sideEffect.emit(GameSideEffect.ShowError(CoreUiR.string.extra_hints_awarded))
                }
            },
            onError = {
                _uiState.update { it.copy(isLoading = false) }
                saveStateToHandle(_uiState.value, targetWord)
                viewModelScope.launch {
                    val errorRes = if (adManager.isVpnActive()) {
                        CoreUiR.string.ad_failed_vpn_error
                    } else {
                        CoreUiR.string.ad_failed_to_load
                    }
                    _sideEffect.emit(GameSideEffect.ShowError(errorRes))
                }
            },
        )
    }

    private fun resetGame() {
        savedStateHandle.remove<String>(KEY_TARGET_WORD)
        savedStateHandle.remove<String>(KEY_LANGUAGE)
        targetWord = ""
        _uiState.update {
            GameUiState(
                language = it.language,
                vibrationEnabled = it.vibrationEnabled,
                hintCount = it.hintCount,
                isPremium = it.isPremium,
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

        val newBoard =
            currentState.board.mapIndexed { rowIndex, row ->
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
        saveStateToHandle(_uiState.value, targetWord)
    }

    private fun handleEnterClick() {
        val currentState = _uiState.value
        val hints = currentState.revealedHints
        val wordLength = 5

        val guess = gameEngine.buildFullGuess(currentState.currentGuess, hints, wordLength)

        if (guess.length < wordLength) {
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

        val result = gameEngine.checkGuess(guess, targetWord)

        val newBoard = currentState.board.mapIndexed { rowIndex, row ->
            if (rowIndex == currentState.currentRow) {
                List(wordLength) { colIndex -> BoardLetter(guess[colIndex], result[colIndex]) }
            } else {
                row
            }
        }

        val newKeyboardStates = currentState.keyboardLetterStates.toMutableMap()
        for (i in guess.indices) {
            if (gameEngine.shouldUpdateKeyboardState(newKeyboardStates[guess[i]], result[i])) {
                newKeyboardStates[guess[i]] = result[i]
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
            if (result[i] == LetterState.CORRECT) newHints[i] = guess[i]
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                currentRow = currentState.currentRow + 1,
                currentGuess = "",
                gameStatus = newStatus,
                keyboardLetterStates = newKeyboardStates,
                revealedHints = newHints,
            )
        }
        saveStateToHandle(_uiState.value, targetWord)

        if (newStatus != GameStatus.PLAYING) {
            viewModelScope.launch { _sideEffect.emit(GameSideEffect.GameFinished) }
        }
    }

    companion object {
        private const val KEY_TARGET_WORD = "target_word"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CURRENT_ROW = "current_row"
        private const val KEY_CURRENT_GUESS = "current_guess"
        private const val KEY_GAME_STATUS = "game_status"
        private const val KEY_HINTS_KEYS = "hints_keys"
        private const val KEY_HINTS_VALS = "hints_vals"
        private const val KEY_BOARD_CHARS = "board_chars"
        private const val KEY_BOARD_STATES = "board_states"
        private const val KEY_KB_KEYS = "kb_keys"
        private const val KEY_KB_VALS = "kb_vals"
    }
}
