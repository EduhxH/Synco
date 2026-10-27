package com.kult.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.kult.app.ui.SyncoApp
import com.kult.app.ui.theme.SyncoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SyncoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = viewModel.darkTheme
            LaunchedEffect(darkTheme) {
                // System bar icons follow the app's theme, not the system one.
                val style = if (darkTheme) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
            SyncoTheme(darkTheme = darkTheme) {
                SyncoApp(viewModel)
            }
        }
    }
}
