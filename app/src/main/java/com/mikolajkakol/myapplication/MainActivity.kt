@file:OptIn(ExperimentalMaterial3Api::class)

package com.mikolajkakol.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikolajkakol.fontui.*

typealias Items = List<String>

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "list") {
                    composable("list") { MainMenu(navController) }
                    composable("RegularMenu") { RegularMenu(navController) }
                    composable("VariableMenu") { VariableMenu(navController) }
                    composable("ShaderMenu") { ShaderMenu(navController) }
                    composable("ShaderPerformanceMenu") { ShaderPerformanceMenu(navController) }
                    composable("DownloadableFontMenu") { DownloadableFontMenu(navController) }

                    composable("AllFonts") { AllFonts() }
                    composable("FontFamily") { FontFamily() }
                    composable("NotEverythingItalicIsItalic") { NotEverythingItalicIsItalic() }
                    composable("NotEverythingBoldIsBold") { NotEverythingBoldIsBold() }
                    composable("VariableSlabQuickNEasy") { VariableSlabQuickNEasy() }
                    composable("VariableSlab") { VariableSlab() }
                    composable("VariableFlex") { VariableFlex() }
                    composable("Swash") { Swash() }
                    composable("FontFeature") { FontFeature() }
                    composable("GradientFont") { GradientFont() }
                    composable("GradientFontTileMode") { GradientFontTileMode() }
                    composable("BitmapFont") { BitmapFont() }
                    composable("ShadersComposition") { ShadersComposition() }
                    composable("ShaderFont") { ShaderFont() }
                    composable("ShaderFontAnimated") { ShaderFontAnimated() }
                    composable("ShaderPerformance1") { ShaderPerformance1() }
                    composable("ShaderPerformance2") { ShaderPerformance2() }
                    composable("ShaderPerformance3") { ShaderPerformance3() }
                    composable("DownloadableFont") { DownloadableFont() }
                    composable("CustomDownloadableFont") { CustomDownloadableFont() }
                }
            }
        }
    }

    @Composable
    private fun MainMenu(navController: NavHostController) {
        navController.ListNav(
            "Fun with Fonts",
            listOf(
                "RegularMenu",
                "VariableMenu",
                "ShaderMenu",
                "ShaderPerformanceMenu",
                "DownloadableFontMenu",
            ),
            navBack = false,
        )
    }

    @Composable
    private fun RegularMenu(navController: NavHostController) {
        navController.ListNav(
            "Regular Fonts",
            listOf(
                "AllFonts",
                "FontFamily",
                "NotEverythingItalicIsItalic",
                "NotEverythingBoldIsBold",
            )
        )
    }

    @Composable
    private fun VariableMenu(navController: NavHostController) {
        navController.ListNav(
            "Variable Fonts",
            listOf(
                "VariableSlabQuickNEasy",
                "VariableSlab",
                "VariableFlex",
                "Swash",
                "FontFeature",
            )
        )
    }

    @Composable
    private fun ShaderMenu(navController: NavHostController) {
        navController.ListNav(
            "Shader Fonts",
            listOf(
                "GradientFont",
                "GradientFontTileMode",
                "BitmapFont",
                "ShadersComposition",
                "ShaderFont",
                "ShaderFontAnimated",
            )
        )
    }

    @Composable
    private fun ShaderPerformanceMenu(navController: NavHostController) {
        navController.ListNav(
            "Shader performance test",
            listOf(
                "ShaderPerformance1",
                "ShaderPerformance2",
                "ShaderPerformance3",
            )
        )
    }

    @Composable
    private fun DownloadableFontMenu(navController: NavHostController) {
        navController.ListNav(
            "Downloadable Fonts",
            listOf(
                "DownloadableFont",
                "CustomDownloadableFont",
            )
        )
    }
}

@Composable
private fun NavHostController.ListNav(
    title: String,
    items: Items,
    navBack: Boolean = true,
) = Scaffold(
    topBar = {
        if (navBack) {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )
        } else {
            CenterAlignedTopAppBar(title = { Text(title) })
        }
    }
) {
    Column(Modifier.padding(it)) {
        items.forEach { destination ->
            ItemNav(destination)
            Divider()
        }
    }
}

@Composable
private fun NavHostController.ItemNav(destination: String) {
    val text = destination
        .replace("([A-Z0-9]+)".toRegex()) { " " + it.value[0].lowercase() }
        .trim()
        .replace("^[a-z]".toRegex()) { it.value[0].uppercase() }

    ListItem(
        modifier = Modifier
            .testTag(destination)
            .clickable {
                navigate(destination)
            },
        headlineText = { Text(text = text) },
        trailingContent = {
            Icon(
                Icons.Sharp.ArrowForward,
                contentDescription = null,
            )
        }
    )
}
