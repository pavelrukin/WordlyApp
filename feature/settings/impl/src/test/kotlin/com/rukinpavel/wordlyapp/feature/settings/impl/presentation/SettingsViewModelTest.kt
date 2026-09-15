package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import app.cash.turbine.test
import com.rukinpavel.wordlyapp.core.domain.usecase.GetLanguageUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.GetVibrationEnabledUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.IsPremiumUseCase
import com.rukinpavel.wordlyapp.core.domain.usecase.UpdateTutorialStatusUseCase
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.testing.MainDispatcherRule
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.repository.BillingRepository
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdateLanguageUseCase
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdatePremiumStatusUseCase
import com.rukinpavel.wordlyapp.feature.settings.impl.domain.usecase.UpdateVibrationEnabledUseCase
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

    private val getLanguageUseCase: GetLanguageUseCase = mockk()
    private val updateLanguageUseCase: UpdateLanguageUseCase = mockk()
    private val getVibrationEnabledUseCase: GetVibrationEnabledUseCase = mockk()
    private val updateVibrationEnabledUseCase: UpdateVibrationEnabledUseCase = mockk()
    private val updateTutorialStatusUseCase: UpdateTutorialStatusUseCase = mockk()
    private val isPremiumUseCase: IsPremiumUseCase = mockk()
    private val updatePremiumStatusUseCase: UpdatePremiumStatusUseCase = mockk()
    private val billingRepository: BillingRepository = mockk {
        every { subscriptionOptions } returns flowOf(emptyList())
    }

    private fun createViewModel(): SettingsViewModel {
        every { getLanguageUseCase() } returns flowOf(Language.EN)
        every { getVibrationEnabledUseCase() } returns flowOf(true)
        every { isPremiumUseCase() } returns flowOf(false)

        return SettingsViewModel(
            getLanguageUseCase,
            updateLanguageUseCase,
            getVibrationEnabledUseCase,
            updateVibrationEnabledUseCase,
            updateTutorialStatusUseCase,
            isPremiumUseCase,
            updatePremiumStatusUseCase,
            billingRepository
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
        coEvery { updateTutorialStatusUseCase(false) } returns Unit

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.OnRepeatTutorialClick)
            assertEquals(SettingsEffect.NavigateToOnboarding, awaitItem())
        }

        coVerify { updateTutorialStatusUseCase(false) }
    }
}
