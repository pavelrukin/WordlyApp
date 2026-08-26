package com.rukinpavel.wordlyapp.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rukinpavel.wordlyapp.core.model.LetterState

@Composable
fun LetterTile(
    char: Char,
    state: LetterState,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        LetterState.INITIAL -> Color.Transparent
        LetterState.CORRECT -> WordleGreen
        LetterState.WRONG_POSITION -> WordleYellow
        LetterState.NOT_IN_WORD -> WordleDarkGray
    }

    val borderColor = when (state) {
        LetterState.INITIAL -> if (char == ' ') WordleDarkGray else WordleGray
        else -> backgroundColor
    }

    val textColor = when (state) {
        LetterState.INITIAL -> MaterialTheme.colorScheme.onBackground
        LetterState.CORRECT, LetterState.WRONG_POSITION -> Color.White
        LetterState.NOT_IN_WORD -> Color.White
    }

    val scale by animateFloatAsState(
        targetValue = if (char != ' ' && state == LetterState.INITIAL) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "PopAnimation"
    )

    val shape = RoundedCornerShape(12.dp)
    val isFilled = char != ' '

    Surface(
        color = backgroundColor,
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .scale(if (char != ' ' && state == LetterState.INITIAL) scale else 1f)
            .shadow(if (isFilled) 2.dp else 0.dp, shape)
            .border(2.dp, borderColor, shape),
        shape = shape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = char.toString().uppercase(),
                color = textColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LetterTilePreview() {
    WordlyTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            LetterTile(char = 'W', state = LetterState.INITIAL)
            LetterTile(char = 'O', state = LetterState.CORRECT)
            LetterTile(char = 'R', state = LetterState.WRONG_POSITION)
            LetterTile(char = 'D', state = LetterState.NOT_IN_WORD)
            LetterTile(char = ' ', state = LetterState.INITIAL)
        }
    }
}
