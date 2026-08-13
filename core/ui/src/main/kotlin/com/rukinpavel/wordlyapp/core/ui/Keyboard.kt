package com.rukinpavel.wordlyapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rukinpavel.wordlyapp.core.model.LetterState

@Composable
fun Keyboard(
    letterStates: Map<Char, LetterState>,
    onKeyClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        "QWERTYUIOP".toList(),
        "ASDFGHJKL".toList(),
        "ZXCVBNM".toList()
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                if (rowIndex == 2) {
                    KeyItem(
                        text = "ENTER",
                        onClick = onEnterClick,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                row.forEach { char ->
                    val state = letterStates[char] ?: LetterState.INITIAL
                    KeyItem(
                        text = char.toString(),
                        onClick = { onKeyClick(char) },
                        state = state,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowIndex == 2) {
                    KeyItem(
                        icon = { Icon(Icons.Default.Backspace, contentDescription = "Delete") },
                        onClick = onDeleteClick,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun KeyItem(
    text: String? = null,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    state: LetterState = LetterState.INITIAL,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        LetterState.INITIAL -> WordleLightGray
        LetterState.CORRECT -> WordleGreen
        LetterState.WRONG_POSITION -> WordleYellow
        LetterState.NOT_IN_WORD -> WordleDarkGray
    }

    val textColor = if (state == LetterState.INITIAL) Color.Black else Color.White

    Box(
        modifier = modifier
            .height(56.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = textColor,
                fontSize = if (text.length > 1) 12.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (icon != null) {
            icon()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardPreview() {
    WordlyTheme {
        Keyboard(
            letterStates = mapOf(
                'Q' to LetterState.CORRECT,
                'W' to LetterState.WRONG_POSITION,
                'E' to LetterState.NOT_IN_WORD
            ),
            onKeyClick = {},
            onDeleteClick = {},
            onEnterClick = {}
        )
    }
}
