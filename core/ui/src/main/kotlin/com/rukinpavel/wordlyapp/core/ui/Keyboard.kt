package com.rukinpavel.wordlyapp.core.ui

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    language: Language,
    letterStates: Map<Char, LetterState>,
    onKeyClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onEnterClick: () -> Unit,
    vibrationEnabled: Boolean = true,
) {
    val haptic = LocalHapticFeedback.current

    val rows =
        when (language) {
            Language.EN ->
                listOf(
                    "QWERTYUIOP".toList(),
                    "ASDFGHJKL".toList(),
                    "ZXCVBNM".toList(),
                )

            Language.RU ->
                listOf(
                    "ЙЦУКЕНГШЩЗХЪ".toList(),
                    "ФЫВАПРОЛДЖЭ".toList(),
                    "ЯЧСМИТЬБЮ".toList(),
                )

            Language.UK ->
                listOf(
                    "ЙЦУКЕНГШЩЗХЇ".toList(),
                    "ФІВАПРОЛДЖЄҐ".toList(),
                    "ЯЧСМИТЬБЮ".toList(),
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
        modifier =
        modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                if (rowIndex == rows.lastIndex) {
                    KeyItem(
                        text = localizedString(CoreUiR.string.enter),
                        onClick = handleEnterClick,
                        modifier = Modifier.weight(1.5f),
                    )
                }

                row.forEach { char ->
                    val state = letterStates[char] ?: LetterState.INITIAL
                    KeyItem(
                        text = char.toString(),
                        onClick = { handleKeyClick(char) },
                        state = state,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowIndex == rows.lastIndex) {
                    KeyItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = localizedString(CoreUiR.string.cd_delete),
                                tint = Color.Black,
                            )
                        },
                        onClick = handleDeleteClick,
                        modifier = Modifier.weight(1.5f),
                    )
                }
            }
        }
    }
}

@Composable
fun KeyItem(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    state: LetterState = LetterState.INITIAL,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "KeyScaleAnimation",
    )

    val targetBackgroundColor =
        when (state) {
            LetterState.INITIAL -> WordleLightGray
            LetterState.CORRECT -> WordleGreen
            LetterState.WRONG_POSITION -> WordleYellow
            LetterState.NOT_IN_WORD -> WordleDarkGray
        }

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        label = "KeyBackgroundAnimation",
    )

    val textColor = if (state == LetterState.INITIAL) Color.Black else Color.White

    val shape = RoundedCornerShape(10.dp)

    Surface(
        modifier =
        modifier
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable default ripple to emphasize custom animation
                onClick = onClick,
            ),
        shape = shape,
        color = backgroundColor,
        tonalElevation = 2.dp,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (text != null) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = if (text.length > 1) 12.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                )
            } else if (icon != null) {
                icon()
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Small Screen", widthDp = 320, showBackground = true)
@Composable
fun KeyboardPreview() {
    WordlyTheme {
        CompositionLocalProvider(LocalLocalizedContext provides LocalContext.current) {
            Keyboard(
                language = Language.EN,
                letterStates =
                mapOf(
                    'Q' to LetterState.CORRECT,
                    'W' to LetterState.WRONG_POSITION,
                    'E' to LetterState.NOT_IN_WORD,
                ),
                onKeyClick = {},
                onDeleteClick = {},
                onEnterClick = {},
                vibrationEnabled = true,
            )
        }
    }
}
