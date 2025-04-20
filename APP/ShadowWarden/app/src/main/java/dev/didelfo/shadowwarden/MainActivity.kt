package dev.didelfo.shadowwarden

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.didelfo.shadowwarden.ui.navigation.AppNavigation
import dev.didelfo.shadowwarden.ui.screen.*
import dev.didelfo.shadowwarden.ui.theme.ShadowWardenTheme
import java.io.File
import java.io.FileOutputStream

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

