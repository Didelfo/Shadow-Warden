package dev.didelfo.shadowwarden.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import dev.didelfo.shadowwarden.ui.screens.home.*
import dev.didelfo.shadowwarden.ui.screens.home.addServer.AddServerScreen
import dev.didelfo.shadowwarden.ui.screens.home.home.HomeScreen
import dev.didelfo.shadowwarden.ui.screens.home.register.RegisterScreen
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatScreen
import dev.didelfo.shadowwarden.ui.screens.server.serverhome.ServerHomeScreen
import java.net.URLDecoder

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SplashScreen.route) {

// ----------------------------------
//                 Home
// ----------------------------------

        composable(route = AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }
        composable(route = AppScreens.HomeScreen.route){
            HomeScreen(navController)
        }


// ----------------------------------
//           Add Server
// ----------------------------------

        composable(route = AppScreens.AddServerScreen.route){
            AddServerScreen(navController)
        }


// ----------------------------------
//           Server
// ----------------------------------

        composable(route = AppScreens.ServerHomeScreen.route) {
            ServerHomeScreen(navController)
        }

//        composable(route = AppScreens.ChatScreen.route){
//            ChatScreen(navController)
//        }
        composable(
            route = AppScreens.ChatScreen.route,
            arguments = listOf(
                navArgument("messages") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val messagesJson = backStackEntry.arguments?.getString("messages") ?: ""
            val messages = try {
                val decodedJson = URLDecoder.decode(messagesJson, "UTF-8")
                Gson().fromJson(decodedJson, Array<ChatMessage>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
            ChatScreen(navController, messages)
        }


    }
}