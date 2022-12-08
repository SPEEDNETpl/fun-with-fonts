package com.mikolajkakol.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikolajkakol.fontui.ShaderFontAnimated

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") { Composables(navController) }
                        composable("shaderAnim1") { ShaderFontAnimated() }
                    }
                }
            }
        }
    }

    @Composable
    private fun Composables(navController: NavHostController) = Column {
        Button(onClick = { navController.navigate("shaderAnim1") }) {
            Text(text = "shaderAnim1")
        }

    }
}
