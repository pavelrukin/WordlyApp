package com.rukinpavel.wordlyapp.core.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val FloralShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = LilacSoft,
    secondary = PetalPink,
    tertiary = LeafGreen,
    background = Color.Transparent,
    surface = Color(0xFF2D2D2D)
)

private val LightColorScheme = lightColorScheme(
    primary = BlossomPurple,
    secondary = PetalPinkDeep,
    tertiary = LeafGreenDeep,
    background = Color.Transparent,
    surface = CreamPetal
)

@Composable
fun WordlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamic color is disabled as per instructions, but we keep the logic structure if needed.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // We use a fallback color for status bar since background is transparent
            window.statusBarColor = if (darkTheme) BgGradientStartDark.toArgb() else BgGradientStart.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = FloralShapes,
    ) {
        FloralBackground(darkTheme = darkTheme, content = content)
    }
}
