package com.rukinpavel.wordlyapp.feature.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.WordleDarkGray
import com.rukinpavel.wordlyapp.core.ui.WordleGray
import com.rukinpavel.wordlyapp.core.ui.WordleGreen
import com.rukinpavel.wordlyapp.core.ui.WordleYellow

@Composable
fun TutorialTile(
    letter: Char?,
    state: LetterState,
    isRevealed: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "TileFlipAnimation"
    )

    val targetBackgroundColor = if (rotation > 90f) {
        when (state) {
            LetterState.INITIAL -> Color.Transparent
            LetterState.CORRECT -> WordleGreen
            LetterState.WRONG_POSITION -> WordleYellow
            LetterState.NOT_IN_WORD -> WordleDarkGray
        }
    } else {
        Color.Transparent
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        label = "TileColorAnimation"
    )

    val borderColor = if (rotation > 90f) {
        backgroundColor
    } else {
        if (letter != null && letter != ' ') WordleGray else WordleDarkGray
    }

    val textColor = if (rotation > 90f) {
        if (state == LetterState.INITIAL) MaterialTheme.colorScheme.onBackground else Color.White
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val shape = RoundedCornerShape(8.dp)

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .graphicsLayer {
                rotationX = rotation
                // Fix mirroring effect when rotated 180 deg
                cameraDistance = 12f * density
            }
            .border(2.dp, borderColor, shape),
        shape = shape,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    // Reverse the content rotation if the tile is flipped
                    rotationX = if (rotation > 90f) 180f else 0f
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter?.toString()?.uppercase() ?: "",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
