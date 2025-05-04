package dev.didelfo.shadowwarden.ui.screens.home

import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.servers.QR
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.screens.utils.createDialog
import dev.didelfo.shadowwarden.utils.camera.QRScannerView

@Composable
fun ScannerScreen(navController: NavHostController) {
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var motrarError by remember { mutableStateOf<Boolean>(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        QRScannerView(
            onCodeScanned = {
                scannedCode = it


                    try {
                        val gson = Gson()
                        val qr:QR = gson.fromJson(scannedCode, QR::class.java)

                        motrarError = false
                        navController.navigate(AppScreens.AddServerScreen.createRoute(scannedCode))

                    } catch (e: Exception) {

                        motrarError = true

                    }
            }
        )

        if (motrarError){

            createDialog(
                painterResource(R.drawable.close),
                RojoCoral,
                "Error",
                RojoCoral,
                "Este codigo QR no pertenece a un servidor de Minecraft.",
                "Volver",
                {
                    motrarError = false
                }
            )

        }



    }
}
