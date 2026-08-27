package com.rukinpavel.wordlyapp.feature.settings.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rukinpavel.wordlyapp.core.ui.localizedString
import com.rukinpavel.wordlyapp.feature.settings.TutorialStep
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR

@Composable
fun TutorialExplanation(
    step: TutorialStep,
    word: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (fadeIn() + slideInVertically { it }).togetherWith(fadeOut() + slideOutVertically { -it })
            },
            label = "ExplanationAnimation"
        ) { targetStep ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val titleRes = when (targetStep) {
                    TutorialStep.Introduction -> CoreUiR.string.tutorial_intro_title
                    TutorialStep.Typing -> CoreUiR.string.tutorial_typing_title
                    TutorialStep.Checking -> CoreUiR.string.tutorial_checking_title
                    TutorialStep.ExplainingCorrect -> CoreUiR.string.tutorial_correct_title
                    TutorialStep.ExplainingWrongPosition -> CoreUiR.string.tutorial_wrong_pos_title
                    TutorialStep.ExplainingNotInWord -> CoreUiR.string.tutorial_not_in_word_title
                    TutorialStep.ExplainingHint -> CoreUiR.string.tutorial_hint_title
                    TutorialStep.Completed -> CoreUiR.string.tutorial_completed_title
                }

                val description = when (targetStep) {
                    TutorialStep.Introduction -> localizedString(CoreUiR.string.tutorial_intro_desc)
                    TutorialStep.Typing -> localizedString(CoreUiR.string.tutorial_typing_desc)
                    TutorialStep.Checking -> localizedString(CoreUiR.string.tutorial_checking_desc)
                    TutorialStep.ExplainingCorrect -> localizedString(CoreUiR.string.tutorial_correct_desc, word[0])
                    TutorialStep.ExplainingWrongPosition -> localizedString(CoreUiR.string.tutorial_wrong_pos_desc, word[2])
                    TutorialStep.ExplainingNotInWord -> localizedString(CoreUiR.string.tutorial_not_in_word_desc, word[3])
                    TutorialStep.ExplainingHint -> localizedString(CoreUiR.string.tutorial_hint_desc)
                    TutorialStep.Completed -> localizedString(CoreUiR.string.tutorial_completed_desc)
                }

                Text(
                    text = localizedString(titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}
