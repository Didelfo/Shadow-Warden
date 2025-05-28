package dev.didelfo.shadowwarden.connection.websocket.components

import android.util.Log
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens

class MessageProcessor(navController: NavHostController) {

    private val nave = navController


    fun classifyCategory(m: StructureMessage){

        when(m.category){
            "auth" -> {
                classifyAuth(m)
            }
            "chat" -> {
                classifyChat(m)
            }
            else -> {}
        }
    }

    private fun classifyAuth(m: StructureMessage){
        when(m.action){
            "GetCurrentUserPermissions" -> {
                // Savemos que es una lista de string
                val permissions: List<String> = m.data.get("permissions") as List<String>
                nave.navigate(AppScreens.ServerHomeScreen.createRoute(permissions))
            }
            else -> {}
        }
    }

    private fun classifyChat(m: StructureMessage){
        when(m.action){
            "JoinChat" -> {}
            "MessageSend" -> {}
            else -> {}
        }
    }


}