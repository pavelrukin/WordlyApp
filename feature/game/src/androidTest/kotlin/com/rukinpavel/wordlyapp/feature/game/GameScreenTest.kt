package com.rukinpavel.wordlyapp.feature.game

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.ui.LocalLocalizedContext
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Rule
import org.junit.Test

class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreen_displaysKeyboardKeys() {
        val uiState = GameUiState(
            isLoading = false,
            language = Language.EN,
            targetWord = "APPLE"
        )
        
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalLocalizedContext provides LocalContext.current
            ) {
                GameContent(
                    uiState = uiState,
                    sideEffect = MutableSharedFlow(),
                    onEvent = {},
                    onSettingsClick = {}
                )
            }
        }

        // Check if some QWERTY keys are displayed
        composeTestRule.onNodeWithText("Q").assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Z").assertIsDisplayed()
    }
}
