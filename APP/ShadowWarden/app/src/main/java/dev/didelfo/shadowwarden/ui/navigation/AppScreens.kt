package dev.didelfo.shadowwarden.ui.navigation

import dev.didelfo.shadowwarden.config.servers.QR

sealed class AppScreens(val route:String) {

    object SplashScreen: AppScreens("splash_screen")
    object RegisterScreen: AppScreens("register_screen")
    object HomeScreen: AppScreens("home_screen")
    object ServerListScreen: AppScreens("server_list_screen")

    object AddServerScreen: AppScreens("add_server_screen/{qr}") {
        fun createRoute(qr: String?) = "add_server_screen/${qr}"
    }

    object ScannerScreen: AppScreens("qr_scanner_screen")

}