package com.mikolajkakol.fontui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp

@Composable
fun Animated() = Column {
    val infiniteTransition = rememberInfiniteTransition()
    val repeatableSpec = remember { infiniteRepeatable<Float>(tween(600), RepeatMode.Reverse) }
    val positionYTAS = infiniteTransition.animateFloat(
        initialValue = 649f,
        targetValue = 854f,
        animationSpec = repeatableSpec
    )
    val positionYTDE = infiniteTransition.animateFloat(
        initialValue = -305f,
        targetValue = -98f,
        animationSpec = repeatableSpec
    )
    val positionYTLC = infiniteTransition.animateFloat(
        initialValue = 416f,
        targetValue = 570f,
        animationSpec = repeatableSpec
    )

    val positionYTUC = infiniteTransition.animateFloat(
        initialValue =528f,
        targetValue = 760f,
        animationSpec = repeatableSpec
    )

    Text(
        text = "Loading...",
        fontSize = 100.sp,
        fontFamily = FontFamily(
            Font(
                R.font.roboto_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(400),
                    FontVariation.width(50f),
                    FontVariation.grade(78),
                    FontVariation.Setting("YTAS", positionYTAS.value),
                    FontVariation.Setting("YTDE", positionYTDE.value),
                    FontVariation.Setting("YTLC", positionYTLC.value),
                    FontVariation.Setting("YTUC", positionYTUC.value),
                )
            )
        ),
    )
}
