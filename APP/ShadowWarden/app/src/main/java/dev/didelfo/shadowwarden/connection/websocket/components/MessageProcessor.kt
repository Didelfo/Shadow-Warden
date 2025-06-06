package dev.didelfo.shadowwarden.connection.websocket.components

import android.util.Log
import androidx.navigation.NavHostController
import com.google.gson.Gson
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.model.StructureMessage
import dev.didelfo.shadowwarden.ui.navigation.AppNavigator
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage
import kotlinx.coroutines.flow.StateFlow

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
            "moderation"  -> {
                classifyModeration(m)
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

    private fun classifyChat(m: StructureMessage) {
        when (m.action) {
            "SubscribeChat" -> {
                val gson = Gson()
                val rawList = m.data["mensajesChat"] as? List<*> ?: emptyList<Any>()
                val mensajes = rawList.mapNotNull {
                    try {
                        val json = gson.toJson(it)
                        gson.fromJson(json, ChatMessage::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                nave.navigate(AppScreens.ChatScreen.route)
                WSController.cliente.setMessages(mensajes)


            }

            "MessageSend" -> {
                val gson = Gson()
                val msgData = m.data["mensaje"]
                val mensaje = gson.fromJson(gson.toJson(msgData), ChatMessage::class.java)
                WSController.cliente.addMessage(mensaje)
            }

            else -> {}
        }
    }

    private fun classifyModeration(m: StructureMessage) {

        when(m.action){
            "ChatSanction" -> {
                val gson = Gson()
                val msgData = m.data["mensaje"]
                val mensaje = gson.fromJson(gson.toJson(msgData), ChatMessage::class.java)
                WSController.cliente.addMessage(mensaje)
            }
            else ->{}
        }

    }




}