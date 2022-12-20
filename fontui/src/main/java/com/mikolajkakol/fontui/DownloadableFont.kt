package com.mikolajkakol.fontui

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import androidx.compose.ui.text.googlefonts.Font as GFont

private val errorHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e("downloadable", "There has been an issue: ", throwable)
}

private val googleProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val googleFont = FontFamily(
    GFont(googleFont = GoogleFont("IBM Plex Serif", false), fontProvider = googleProvider)
)

@Composable
fun DownloadableFont() = CompositionLocalProvider(
    LocalFontFamilyResolver provides createFontFamilyResolver(LocalContext.current, errorHandler)
) {
    Text(
        text = demoText,
        fontFamily = googleFont,
    )
}

@Composable
fun CustomDownloadableFont() = CompositionLocalProvider(
    LocalFontFamilyResolver provides createFontFamilyResolver(LocalContext.current, errorHandler)
) {
    Text(
        text = demoText,
        fontFamily = FontFamily(
            CustomFont(name = "sansita_swashed")
        )
    )
}

class CustomFont(val name: String) :
    AndroidFont(FontLoadingStrategy.Async, CustomFontTypefaceLoader) {
    @ExperimentalTextApi
    override val style: FontStyle
        get() = FontStyle.Normal
    override val weight: FontWeight
        get() = FontWeight.Normal
}

object CustomFontTypefaceLoader : AndroidFont.TypefaceLoader {

    private val cache = mutableMapOf<String, Typeface?>()

    override suspend fun awaitLoad(context: Context, font: AndroidFont): Typeface? {
        require(font is CustomFont)
        return cache.getOrPut(font.name) {
            delay(2000) // simulate doing IO
            val id = context.resources.getIdentifier(font.name, "font", context.packageName)
            ResourcesCompat.getFont(context, id)
        }
    }

    override fun loadBlocking(context: Context, font: AndroidFont): Typeface? {
        throw IllegalStateException("only async is supported")
    }
}
