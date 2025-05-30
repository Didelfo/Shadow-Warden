package dev.didelfo.shadowwarden.connection.websocket.components

import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.model.StructureMessage
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage

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
                WSController.cliente.permission = permissions
                nave.navigate(AppScreens.ServerHomeScreen.route)
            }
            else -> {}
        }
    }

    private fun classifyChat(m: StructureMessage){
        when(m.action){
            "SubscribeChat" -> {
                val mensajes: List<ChatMessage> = m.data.get("mensajesChat") as List<ChatMessage>
                nave.navigate(AppScreens.ChatScreen.createRoute(mensajes))
            }
            "MessageSend" -> {}
            else -> {}
        }
    }


}