package dev.didelfo.shadowwarden.ui.navigation

import dev.didelfo.shadowwarden.config.servers.QR

sealed class AppScreens(val route:String) {

// ----------------------------------
//                 Home
// ----------------------------------

    object SplashScreen: AppScreens("splash_screen")
    object RegisterScreen: AppScreens("register_screen")
    object HomeScreen: AppScreens("home_screen")

// ----------------------------------
//           Add Server
// ----------------------------------

    object AddServerScreen: AppScreens("add_server_screen/{qr}") {
        fun createRoute(qr: String?) = "add_server_screen/${qr}"
    }
    object ScannerScreen: AppScreens("qr_scanner_screen")

// ----------------------------------
//           Server
// ----------------------------------

    object ServerHomeScreen: AppScreens("server_home_screen")

}