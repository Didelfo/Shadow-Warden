package dev.didelfo.shadowwarden.connection.websocket.components

import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.model.StructureMessage
import dev.didelfo.shadowwarden.ui.navigation.AppNavigator
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage

class MessageProcessor() {

    val nave = checkNotNull(AppNavigator.navController)


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
                WSController.cliente.chat = mensajes
                nave.navigate(AppScreens.ChatScreen.route)
            }
            "MessageSend" -> {
                val mensaje: ChatMessage = m.data.get("mensaje") as ChatMessage
                WSController.cliente.addMessage(mensaje)
            }
            else -> {}
        }
    }


}