package com.rukinpavel.wordlyapp.feature.game

import app.cash.turbine.test
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.repository.WordRepository
import com.rukinpavel.wordlyapp.domain.usecase.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val checkGuessUseCase: CheckGuessUseCase = mockk()
    private val validateWordUseCase: ValidateWordUseCase = mockk()
    private val wordRepository: WordRepository = mockk()
    private val getLanguageUseCase: GetLanguageUseCase = mockk()
    private val getVibrationEnabledUseCase: GetVibrationEnabledUseCase = mockk()
    private val getHintCountUseCase: GetHintCountUseCase = mockk()
    private val updateHintCountUseCase: UpdateHintCountUseCase = mockk()
    private val isPremiumUseCase: IsPremiumUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { getLanguageUseCase() } returns flowOf(Language.EN)
        every { getVibrationEnabledUseCase() } returns flowOf(true)
        every { getHintCountUseCase() } returns flowOf(5)
        every { isPremiumUseCase() } returns flowOf(false)
        coEvery { wordRepository.getRandomWord(any()) } returns "APPLE"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct defaults and loads word`() = runTest {
        val viewModel = GameViewModel(
            checkGuessUseCase,
            validateWordUseCase,
            wordRepository,
            getLanguageUseCase,
            getVibrationEnabledUseCase,
            getHintCountUseCase,
            updateHintCountUseCase,
            isPremiumUseCase
        )

        viewModel.uiState.test {
            // The first emission might be the initial state before init blocks finish some flows
            // but since flows are flowOf, they emit immediately.
            
            // Advance until idle to let all init logic and loadNewWord complete
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertEquals(Language.EN, state.language)
            assertEquals(5, state.hintCount)
            assertEquals("APPLE", state.targetWord)
            assertEquals(false, state.isLoading)
        }
    }
}
