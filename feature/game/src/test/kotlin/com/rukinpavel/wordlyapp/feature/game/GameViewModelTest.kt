package com.rukinpavel.wordlyapp.feature.game

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.rukinpavel.wordlyapp.core.domain.game.GameEngine
import com.rukinpavel.wordlyapp.core.domain.repository.AdManager
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.WordRepository
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.feature.game.domain.usecase.ValidateWordUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val gameEngine: GameEngine = mockk()
    private val validateWordUseCase: ValidateWordUseCase = mockk()
    private val wordRepository: WordRepository = mockk()
    private val appPreferencesRepository: AppPreferencesRepository = mockk()
    private val adManager: AdManager = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { appPreferencesRepository.language } returns flowOf(Language.EN)
        every { appPreferencesRepository.vibrationEnabled } returns flowOf(true)
        every { appPreferencesRepository.hintCount } returns flowOf(5)
        every { appPreferencesRepository.isPremium } returns flowOf(false)
        coEvery { wordRepository.getRandomWord(any()) } returns "APPLE"
        coEvery { wordRepository.getHint(any(), any()) } returns "A sweet red fruit"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct defaults and loads word`() =
        runTest {
            val viewModel =
                GameViewModel(
                    savedStateHandle,
                    gameEngine,
                    validateWordUseCase,
                    wordRepository,
                    appPreferencesRepository,
                    adManager,
                )

            viewModel.uiState.test {
                testDispatcher.scheduler.advanceUntilIdle()

                val state = expectMostRecentItem()
                assertEquals(Language.EN, state.language)
                assertEquals(5, state.hintCount)
                assertEquals("APPLE", state.targetWord)
                assertEquals("A sweet red fruit", state.wordHint)
                assertEquals(false, state.isLoading)
            }
        }

    @Test
    fun `restores state from SavedStateHandle after process death`() =
        runTest {
            val restoredHandle = SavedStateHandle(
                mapOf(
                    "target_word" to "CRANE",
                    "language" to "EN",
                    "current_row" to 2,
                    "current_guess" to "AB",
                    "game_status" to "PLAYING",
                ),
            )

            val viewModel = GameViewModel(
                restoredHandle,
                gameEngine,
                validateWordUseCase,
                wordRepository,
                appPreferencesRepository,
                adManager,
            )

            viewModel.uiState.test {
                testDispatcher.scheduler.advanceUntilIdle()

                val state = expectMostRecentItem()
                assertEquals("CRANE", state.targetWord)
                assertEquals(Language.EN, state.language)
                assertEquals(2, state.currentRow)
                assertEquals("AB", state.currentGuess)
                assertEquals(GameStatus.PLAYING, state.gameStatus)
            }
        }

    @Test
    fun `resets game and loads new word when language changes`() =
        runTest {
            val languageFlow = MutableStateFlow<Language?>(Language.EN)
            every { appPreferencesRepository.language } returns languageFlow
            coEvery { wordRepository.getRandomWord(Language.EN) } returns "APPLE"
            coEvery { wordRepository.getRandomWord(Language.RU) } returns "КНИГА"

            val viewModel = GameViewModel(
                savedStateHandle,
                gameEngine,
                validateWordUseCase,
                wordRepository,
                appPreferencesRepository,
                adManager,
            )

            viewModel.uiState.test {
                testDispatcher.scheduler.advanceUntilIdle()
                val initialState = expectMostRecentItem()
                assertEquals(Language.EN, initialState.language)
                assertEquals("APPLE", initialState.targetWord)

                // Change language to RU
                languageFlow.value = Language.RU
                testDispatcher.scheduler.advanceUntilIdle()

                val updatedState = expectMostRecentItem()
                assertEquals(Language.RU, updatedState.language)
                assertEquals("КНИГА", updatedState.targetWord)
            }
        }
}
