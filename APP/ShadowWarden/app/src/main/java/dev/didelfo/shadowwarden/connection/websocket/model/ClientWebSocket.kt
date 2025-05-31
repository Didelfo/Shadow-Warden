package dev.didelfo.shadowwarden.connection.websocket.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage

class ClientWebSocket {
    // Variables
    var publicKeyServer: String = ""
    var publicKeyMovil: String = ""
    var permission: List<String> = listOf()
    var chat by mutableStateOf(listOf<ChatMessage>())

    fun reset(){
        publicKeyMovil = ""
        publicKeyServer = ""
        permission = listOf()
    }

    fun addMessage(msg: ChatMessage){
        chat = chat.toMutableList().apply {
            if (isNotEmpty()) {
                removeAt(0)
            }
            add(msg)
        }
    }




}