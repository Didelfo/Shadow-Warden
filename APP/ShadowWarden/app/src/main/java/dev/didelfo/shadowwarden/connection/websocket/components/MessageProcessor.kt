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


    fun classifyCategory(m: StructureMessage) {

        when (m.category) {
            "auth" -> classifyAuth(m)
            "chat" -> classifyChat(m)
            "moderation" -> classifyModeration(m)
            "config" -> classifyConfig(m)
            else -> {}
        }
    }

    private fun classifyAuth(m: StructureMessage) {
        when (m.action) {
            "GetCurrentUserPermissions" -> {
                try {
                    // Savemos que es una lista de string
                    val permissions: List<String> = m.data.get("permissions") as List<String>
                    WSController.cliente.permission = permissions
                    nave.navigate(AppScreens.ServerHomeScreen.route)
                } catch (e: Exception) {
                    WSController.closeConnection()
                    nave.navigate(AppScreens.HomeScreen.route)
                }
            }

            else -> {}
        }
    }

    private fun classifyChat(m: StructureMessage) {
        when (m.action) {
            "SubscribeChat" -> {
                try {
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

                } catch (e: Exception) {
                    WSController.closeConnection()
                    nave.navigate(AppScreens.HomeScreen.route)
                }

            }

            "MessageSend" -> {
                try {
                    val gson = Gson()
                    val msgData = m.data["mensaje"]
                    val mensaje = gson.fromJson(gson.toJson(msgData), ChatMessage::class.java)
                    WSController.cliente.addMessage(mensaje)
                } catch (e: Exception) {
                    WSController.closeConnection()
                    nave.navigate(AppScreens.HomeScreen.route)
                }
            }

            else -> {}
        }
    }

    private fun classifyModeration(m: StructureMessage) {

        when (m.action) {

            "ChatSanction" -> {
                try {
                    val gson = Gson()
                    val msgData = m.data["mensaje"]
                    val mensaje = gson.fromJson(gson.toJson(msgData), ChatMessage::class.java)
                    WSController.cliente.addMessage(mensaje)
                } catch (e: Exception) {
                    WSController.closeConnection()
                    nave.navigate(AppScreens.HomeScreen.route)
                }
            }

            else -> {}
        }

    }

    private fun classifyConfig(m: StructureMessage) {
        when (m.action) {
            "GetConfigSpamFilter" -> {
                try {
                    WSController.cliente.enableSpam = m.data["enable"].toString().toBoolean()
                    WSController.cliente.time= m.data["time"].toString().toInt()

                    nave.navigate(AppScreens.SpamFilterScreen.route)

                } catch (e: Exception) {
                    WSController.closeConnection()
                    nave.navigate(AppScreens.HomeScreen.route)
                }
            }

            else -> {}
        }
    }


}