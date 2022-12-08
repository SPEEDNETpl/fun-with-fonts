package com.mikolajkakol.fontui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_6
import app.cash.paparazzi.Paparazzi
import com.android.resources.Density
import org.junit.Rule
import org.junit.Test

class CreatePics {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_6.copy(
            screenWidth = 1000,
            screenHeight = 1280,
            density = Density.XXXHIGH,
            softButtons = false,
        ),
        theme = "android:Theme.Material.Light.NoActionBar.Fullscreen"
    )

    private fun pic(name: String? = null, content: @Composable () -> Unit) {
        paparazzi.snapshot(name) {
            Box(modifier = Modifier.background(Color.White)) {
                content()
            }
        }
    }

    @Test
    fun regularFont() {
        pic("AllFonts") { AllFonts() }
        pic("FontFamily") { FontFamily() }
        pic("NotEverythingItalicIsItalic") { NotEverythingItalicIsItalic() }
        pic("NotEverythingBoldIsBold") { NotEverythingBoldIsBold() }
    }

    @Test
    fun variableFont() {
        pic("VariableSlabQuickNEasy") { VariableSlabQuickNEasy() }
        pic("VariableSlab") { VariableSlab() }
        pic("VariableFlex") { VariableFlex() }
        pic("Swash") { Swash() }
        pic("FontFeature") { FontFeature() }
    }

    @Test
    fun shaderFont() {
        pic("GradientFont") { GradientFont() }
        pic("GradientFontTileMode") { GradientFontTileMode() }
        pic("BitmapFont") { BitmapFont() }
        pic("ShadersComposition") { ShadersComposition() }
    }
}