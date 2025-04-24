package dev.didelfo.shadowwarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.didelfo.shadowwarden.ui.navigation.AppNavigation
import dev.didelfo.shadowwarden.ui.theme.ShadowWardenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShadowWardenTheme {
                AppNavigation()
            }
        }
    }






}

