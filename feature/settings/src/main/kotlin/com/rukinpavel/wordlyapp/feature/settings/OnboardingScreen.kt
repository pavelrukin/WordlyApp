package com.rukinpavel.wordlyapp.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rukinpavel.wordlyapp.core.ui.localizedString
import com.rukinpavel.wordlyapp.feature.settings.components.TutorialBoard
import com.rukinpavel.wordlyapp.feature.settings.components.TutorialExplanation
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tutorialWord = localizedString(CoreUiR.string.tutorial_demo_word)

    LaunchedEffect(tutorialWord) {
        viewModel.onEvent(OnboardingEvent.UpdateTutorialWord(tutorialWord))
    }

    OnboardingContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onComplete = onComplete
    )
}

@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = localizedString(CoreUiR.string.tutorial_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Animated Board Section
            TutorialBoard(
                word = uiState.tutorialWord,
                states = uiState.tutorialStates,
                visibleLettersCount = uiState.visibleLetters,
                revealedTilesCount = uiState.revealedTiles,
                modifier = Modifier.height(80.dp)
            )

            // Animated Explanation Section
            TutorialExplanation(
                step = uiState.currentStep,
                word = uiState.tutorialWord,
                modifier = Modifier.weight(1f)
            )

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isAnimationFinished) {
                    Button(
                        onClick = { onEvent(OnboardingEvent.PlayAgain) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(localizedString(CoreUiR.string.play_again), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onEvent(OnboardingEvent.CompleteOnboarding)
                            onComplete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(localizedString(CoreUiR.string.got_it), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onEvent(OnboardingEvent.NextStep) },
                        enabled = uiState.canNavigateNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        val buttonText = when (uiState.currentStep) {
                            TutorialStep.Introduction -> localizedString(CoreUiR.string.start_tutorial)
                            TutorialStep.Typing -> localizedString(CoreUiR.string.check_word)
                            else -> localizedString(CoreUiR.string.next)
                        }
                        Text(buttonText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
