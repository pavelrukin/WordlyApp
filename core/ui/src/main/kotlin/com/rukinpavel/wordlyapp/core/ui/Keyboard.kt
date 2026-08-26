package com.rukinpavel.wordlyapp.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR

@Composable
fun Keyboard(
    language: Language,
    letterStates: Map<Char, LetterState>,
    onKeyClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit,
    vibrationEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    val rows = when (language) {
        Language.EN -> listOf(
            "QWERTYUIOP".toList(),
            "ASDFGHJKL".toList(),
            "ZXCVBNM".toList()
        )
        Language.RU -> listOf(
            "ЙЦУКЕНГШЩЗХЪ".toList(),
            "ФЫВАПРОЛДЖЭ".toList(),
            "ЯЧСМИТЬБЮ".toList()
        )
        Language.UK -> listOf(
            "ЙЦУКЕНГШЩЗХЇ".toList(),
            "ФІВАПРОЛДЖЄҐ".toList(),
            "ЯЧСМИТЬБЮ".toList()
        )
    }

    val handleKeyClick: (Char) -> Unit = {
        if (vibrationEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onKeyClick(it)
    }
    
    val handleDeleteClick: () -> Unit = {
        if (vibrationEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onDeleteClick()
    }
    
    val handleEnterClick: () -> Unit = {
        if (vibrationEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onEnterClick()
    }

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
                if (rowIndex == rows.lastIndex) {
                    KeyItem(
                        text = stringResource(CoreUiR.string.enter),
                        onClick = handleEnterClick,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                row.forEach { char ->
                    val state = letterStates[char] ?: LetterState.INITIAL
                    KeyItem(
                        text = char.toString(),
                        onClick = { handleKeyClick(char) },
                        state = state,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowIndex == rows.lastIndex) {
                    KeyItem(
                        icon = { Icon(Icons.Default.Backspace, contentDescription = stringResource(CoreUiR.string.cd_delete)) },
                        onClick = handleDeleteClick,
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "KeyScaleAnimation"
    )

    val targetBackgroundColor = when (state) {
        LetterState.INITIAL -> WordleLightGray
        LetterState.CORRECT -> WordleGreen
        LetterState.WRONG_POSITION -> WordleYellow
        LetterState.NOT_IN_WORD -> WordleDarkGray
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        label = "KeyBackgroundAnimation"
    )

    val textColor = if (state == LetterState.INITIAL) Color.Black else Color.White

    val shape = RoundedCornerShape(10.dp)

    Surface(
        modifier = modifier
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple to emphasize custom animation
                onClick = onClick
            ),
        shape = shape,
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
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
}

@Preview(showBackground = true)
@Composable
fun KeyboardPreview() {
    WordlyTheme {
        Keyboard(
            language = Language.EN,
            letterStates = mapOf(
                'Q' to LetterState.CORRECT,
                'W' to LetterState.WRONG_POSITION,
                'E' to LetterState.NOT_IN_WORD
            ),
            onKeyClick = {},
            onDeleteClick = {},
            onEnterClick = {},
            vibrationEnabled = true
        )
    }
}
