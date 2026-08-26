package com.rukinpavel.wordlyapp.core.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FloralBackground(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FloralBackgroundTransition")

    val colorStart by infiniteTransition.animateColor(
        initialValue = if (darkTheme) BgGradientStartDark else BgGradientStart,
        targetValue = if (darkTheme) BgGradientMidDark else BgGradientMid,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorStart"
    )

    val colorMid by infiniteTransition.animateColor(
        initialValue = if (darkTheme) BgGradientMidDark else BgGradientMid,
        targetValue = if (darkTheme) BgGradientEndDark else BgGradientEnd,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorMid"
    )

    val colorEnd by infiniteTransition.animateColor(
        initialValue = if (darkTheme) BgGradientEndDark else BgGradientEnd,
        targetValue = if (darkTheme) BgGradientStartDark else BgGradientStart,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorEnd"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FlowerRotation"
    )

    val sway by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FlowerSway"
    )

    val random = remember { Random(42) }
    val flowers = remember {
        List(random.nextInt(6, 11)) {
            FlowerData(
                xPercent = random.nextFloat(),
                yPercent = random.nextFloat(),
                sizePercent = random.nextFloat() * 0.15f + 0.1f,
                rotationSpeed = (random.nextFloat() - 0.5f) * 2f,
                color = when (random.nextInt(3)) {
                    0 -> PetalPink.copy(alpha = 0.3f)
                    1 -> LilacSoft.copy(alpha = 0.3f)
                    else -> SkyPeach.copy(alpha = 0.3f)
                }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(colorStart, colorMid, colorEnd),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )

            flowers.forEach { flower ->
                val x = flower.xPercent * size.width
                val y = flower.yPercent * size.height + sin(sway + flower.xPercent * 10f) * 20f
                val flowerSize = flower.sizePercent * size.width

                rotate(rotation * flower.rotationSpeed, pivot = Offset(x, y)) {
                    translate(x, y) {
                        drawFlower(flowerSize, flower.color)
                    }
                }
            }
        }
        content()
    }
}

private fun DrawScope.drawFlower(size: Float, color: Color) {
    val petalCount = 5
    val radius = size / 2f
    val path = Path()

    for (i in 0 until petalCount) {
        val angle = (i * 360f / petalCount).toDouble()
        val rad = Math.toRadians(angle)
        val petalX = (radius * cos(rad)).toFloat()
        val petalY = (radius * sin(rad)).toFloat()

        path.addOval(
            Rect(
                center = Offset(petalX / 2f, petalY / 2f),
                radius = radius / 2f
            )
        )
    }
    drawPath(path, color)
}

private data class FlowerData(
    val xPercent: Float,
    val yPercent: Float,
    val sizePercent: Float,
    val rotationSpeed: Float,
    val color: Color
)
