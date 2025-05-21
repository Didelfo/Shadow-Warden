package dev.didelfo.shadowwarden.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.didelfo.shadowwarden.ui.screens.home.*
import dev.didelfo.shadowwarden.ui.screens.server.ChatScreen
import dev.didelfo.shadowwarden.ui.screens.server.ServerHomeScreen

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
        composable(route = AppScreens.ServerHomeScreen.route){
            ServerHomeScreen(navController)
        }

        composable(route = AppScreens.ChatScreen.route){
            ChatScreen(navController)
        }


    }
}