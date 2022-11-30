package com.mikolajkakol.fontui

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineExceptionHandler
import androidx.compose.ui.text.googlefonts.Font as GFont

val handler = CoroutineExceptionHandler { _, throwable ->
    // process the Throwable
    Log.e("ASDASF", "There has been an issue: ", throwable)
}

val fontSettings: (FontWeight, FontStyle) -> FontVariation.Settings =
    { weight: FontWeight, style: FontStyle ->
        FontVariation.Settings(
//            weight,
//            style,
            FontVariation.slant(-10f),
//            FontVariation.width(50f),
//            FontVariation.italic(0.4f)
        )
    }

val settings = fontSettings(FontWeight.Normal, FontStyle.Normal)

val font = FontFamily(
    listOf(
        Font(R.font.proxima_nova_soft_regular, variationSettings = settings),
//        Font(R.font.proxima_nova_soft_bold, FontWeight.Bold, variationSettings = settings),
    )
)
val fontVariable = FontFamily(listOf(Font(R.font.opensans_variable, variationSettings = settings)))
val fontColor = FontFamily(listOf(Font(R.font.bungee_spice_color, variationSettings = settings)))
val fontNotoVariable = FontFamily(listOf(Font(R.font.noto_variable, variationSettings = settings)))

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontDownloaded = FontFamily(
    GFont(googleFont = GoogleFont("IBM Plex Serif", false), fontProvider = provider)
)


val fontR1 = FontFamily(listOf(Font(R.font.roboto_flex)))
val fontR2 = FontFamily(listOf(Font(R.font.roboto_flex, variationSettings = settings)))

private val fonts = listOf(
//    font, fontVariable, fontNotoVariable, fontDownloaded,
    fontColor,
    fontVariable,
    fontR1, fontR2,
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun FontFun() = CompositionLocalProvider(
    LocalFontFamilyResolver provides createFontFamilyResolver(LocalContext.current, handler)
) {
    val weights = 100..900 step 200
    Column(Modifier.verticalScroll(rememberScrollState())) {
        fonts.forEach { font ->
            Text(font.toString())
            weights.forEach {
                Text(
                    modifier = Modifier.border(1.dp, Color.Red),
                    text = "Sample text\n$it",
                    fontFamily = font,
                    fontWeight = FontWeight(it)
                )
                FontVariation
            }
        }
    }
}