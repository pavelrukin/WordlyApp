package com.rukinpavel.wordlyapp.feature.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rukinpavel.wordlyapp.core.model.LetterState

@Composable
fun TutorialBoard(
    word: String,
    states: List<LetterState>,
    visibleLettersCount: Int,
    revealedTilesCount: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 5) {
                val letter = if (i < visibleLettersCount) word[i] else null
                val state = states[i]
                val isRevealed = i < revealedTilesCount

                TutorialTile(
                    letter = letter,
                    state = state,
                    isRevealed = isRevealed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
