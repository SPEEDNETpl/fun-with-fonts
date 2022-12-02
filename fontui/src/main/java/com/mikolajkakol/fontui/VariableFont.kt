package com.mikolajkakol.fontui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

val weights = listOf(
    FontWeight.Thin,
    FontWeight.Light,
    FontWeight.Normal,
    FontWeight.Medium,
    FontWeight.Bold,
    FontWeight.Black,
)

@Composable
fun VariableSlabQuickNEasy() = Column {
    val slabFontFamily = FontFamily(Font(R.font.roboto_slab))
    weights.forEach { weight ->
        Text(
            text = demoText,
            fontFamily = slabFontFamily,
            fontWeight = weight,
        )
    }
}

private val slabFontFamily = weights.map {
    Font(
        R.font.roboto_slab,
        weight = it,
        variationSettings = FontVariation.Settings(FontVariation.weight(it.weight))
    )
}.let { FontFamily(it) }

@Composable
fun VariableSlab() = Column {
    weights.forEach { weight ->
        Text(
            text = demoText,
            fontFamily = slabFontFamily,
            fontWeight = weight,
        )
    }
}

@Composable
fun VariableFlex() = Column {
    Text(
        text = "Original",
        fontFamily = FontFamily(
            Font(
                R.font.roboto_flex,
            )
        ),
    )
    Text(
        text = "Width weight",
        fontFamily = FontFamily(
            Font(
                R.font.roboto_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(580),
                    FontVariation.width(50f),
                )
            )
        ),
    )
    Text(
        text = "Width weight + grade",
        fontFamily = FontFamily(
            Font(
                R.font.roboto_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(400),
                    FontVariation.width(50f),
                    FontVariation.grade(78),
                )
            )
        ),
    )
    Text(
        text = "Very funky font",
        fontFamily = FontFamily(
            Font(
                R.font.roboto_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(400),
                    FontVariation.width(50f),
                    FontVariation.grade(78),
                    FontVariation.Setting("YOPQ", 106f),
                    FontVariation.Setting("YTLC", 451f),
                    FontVariation.Setting("YTDE", -305f),
                )
            )
        ),
    )
}

@Composable
fun Swash() = Column {
    Text(
        text = "OpenType Variable Fonts",
        fontFamily = FontFamily(Font(R.font.sansita_swashed)),
    )
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontFeatureSettings = "swsh,salt")) {
            append("OpenType Variable Fonts")
        }
    }
    Text(
        text = annotatedString,
        fontFamily = FontFamily(Font(R.font.sansita_swashed)),
    )
}

@Composable
fun FontFeature() = Column {
    val text = buildAnnotatedString {
        append("0")
        withStyle(SpanStyle(fontFeatureSettings = "zero")) {
            append("0\n")
        }
        append("subs & dnom")
        withStyle(SpanStyle(fontFeatureSettings = "subs")) {
            append("0")
        }
        withStyle(SpanStyle(fontFeatureSettings = "dnom")) {
            append("0\n")
        }
        append("1234567890\n")
        withStyle(SpanStyle(fontFeatureSettings = "ss01")) {
            append("1234567890\n")
        }
        withStyle(SpanStyle(fontFeatureSettings = "frac,dlig")) {
            append("3/4 !?")
        }
    }
    Text(
        text = text,
        fontFamily = FontFamily(Font(R.font.inter)),
    )
    Text(
        text = text,
        fontFamily = FontFamily(
            Font(
                R.font.inter,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(800),
                    FontVariation.slant(-10f),
                )
            )
        ),
    )
}
