package dev.didelfo.shadowwarden.ui.navigation

import java.net.URLEncoder

sealed class AppScreens(val route: String) {

// ----------------------------------
//                 Home
// ----------------------------------

    object SplashScreen : AppScreens("splash_screen")
    object RegisterScreen : AppScreens("register_screen")
    object HomeScreen : AppScreens("home_screen")

// ----------------------------------
//           Add Server
// ----------------------------------

    object AddServerScreen : AppScreens("add_server_screen")

// ----------------------------------
//           Server
// ----------------------------------

    //    object ServerHomeScreen: AppScreens("server_home_screen")
    object ServerHomeScreen : AppScreens("server_home_screen/{permissions}") {
        fun createRoute(permissions: List<String>): String {
            val encoded = URLEncoder.encode(permissions.joinToString(","), "utf-8")
            return "server_home_screen/$encoded"
        }
    }

    object ChatScreen : AppScreens("chat_screen")

}