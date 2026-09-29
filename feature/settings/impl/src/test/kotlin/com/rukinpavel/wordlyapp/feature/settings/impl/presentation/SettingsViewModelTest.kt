package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import app.cash.turbine.test
import com.rukinpavel.wordlyapp.core.domain.repository.AppPreferencesRepository
import com.rukinpavel.wordlyapp.core.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appPreferencesRepository: AppPreferencesRepository = mockk()
    private val billingRepository: BillingRepository = mockk {
        every { subscriptionOptions } returns flowOf(emptyList())
    }

    private fun createViewModel(): SettingsViewModel {
        every { appPreferencesRepository.language } returns flowOf(Language.EN)
        every { appPreferencesRepository.vibrationEnabled } returns flowOf(true)
        every { appPreferencesRepository.isPremium } returns flowOf(false)

        return SettingsViewModel(
            appPreferencesRepository,
            billingRepository,
        )
    }

    @Test
    fun `initial state is correctly loaded`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(Language.EN, state.language)
            assertEquals(true, state.vibrationEnabled)
            assertEquals(false, state.isPremium)
        }
    }

    @Test
    fun `on repeat tutorial click, side effect is emitted`() = runTest {
        val viewModel = createViewModel()
        coEvery { appPreferencesRepository.updateTutorialCompleted(false) } returns Unit

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.OnRepeatTutorialClick)
            assertEquals(SettingsEffect.NavigateToOnboarding, awaitItem())
        }

        coVerify { appPreferencesRepository.updateTutorialCompleted(false) }
    }
}
