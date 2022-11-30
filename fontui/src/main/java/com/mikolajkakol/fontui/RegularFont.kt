package com.mikolajkakol.fontui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

const val demoText = "Speednet. We build software."

private val fonts = listOf(
    Font(R.font.roboto_thin, weight = FontWeight.Thin),
    Font(R.font.roboto_thin_italic, weight = FontWeight.Thin, style = FontStyle.Italic),
    Font(R.font.roboto_light, weight = FontWeight.Light),
    Font(R.font.roboto_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.roboto_regular),
    Font(R.font.roboto_italic, style = FontStyle.Italic),
    Font(R.font.roboto_medium, weight = FontWeight.Medium),
    Font(R.font.roboto_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.roboto_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.roboto_black, weight = FontWeight.Black),
    Font(R.font.roboto_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
)
val robotoFontFamily = FontFamily(fonts)

@Composable
fun AllFonts() {
    Column {
        fonts.forEach { font ->
            Text(
                text = demoText,
                fontFamily = FontFamily(font),
            )
        }
    }
}

@Composable
fun FontFamily() = Column {
    val weights = listOf(
        FontWeight.Thin,
        FontWeight.Light,
        FontWeight.Normal,
        FontWeight.Medium,
        FontWeight.Bold,
        FontWeight.Black,
    )
    weights.forEach { weight ->
        Text(
            text = demoText,
            fontFamily = robotoFontFamily,
            fontWeight = weight,
        )
        Text(
            text = demoText,
            fontFamily = robotoFontFamily,
            fontWeight = weight,
            fontStyle = FontStyle.Italic,
        )
    }
}

@Composable
fun NotEverythingItalicIsItalic() = Column {
    val fontsNormal = fonts.filter { it.style == FontStyle.Normal }
    val fontsItalic = fonts.filter { it.style == FontStyle.Italic }
    fontsNormal.forEach { font ->
        Text(
            text = demoText,
            fontFamily = FontFamily(font),
            fontStyle = FontStyle.Italic,
        )
    }
    fontsItalic.forEach { font ->
        Text(
            text = demoText,
            fontFamily = FontFamily(font),
        )
    }
}

@Composable
fun NotEverythingBoldIsBold() = Column {
    val regularFont = fonts.first { it.weight == FontWeight.Normal }
    val boldFont = fonts.first { it.weight == FontWeight.Bold }
    Text(
        text = "$demoText regular",
        fontFamily = FontFamily(regularFont),
    )
    Text(
        text = "$demoText regular bolded",
        fontFamily = FontFamily(regularFont),
        fontWeight = FontWeight.Bold,
    )
    Text(
        text = "$demoText bold",
        fontFamily = FontFamily(boldFont),
    )
}
