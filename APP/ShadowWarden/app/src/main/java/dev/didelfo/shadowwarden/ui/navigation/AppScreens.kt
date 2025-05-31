package dev.didelfo.shadowwarden.ui.navigation

import com.google.gson.Gson
import java.net.URLEncoder
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage

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

    object ServerHomeScreen : AppScreens("server_home_screen")

    object ChatScreen : AppScreens("chat_screen")

}