package dev.didelfo.shadowwarden.connection.websocket.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ClientWebSocket {
    // Variables
    var publicKeyServer: String = ""
    var publicKeyMovil: String = ""
    var permission: List<String> = listOf()

    var enableSpam by mutableStateOf( false)
    var time by mutableStateOf( 0)

    // Usamos MutableStateFlow para tener siempre el último valor
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    var messages: StateFlow<List<ChatMessage>> = _messages

    // Función para añadir un mensaje nuevo
    fun addMessage(message: ChatMessage) {
        val currentList = _messages.value.toMutableList()
        currentList.add(0, message) // <-- Aquí lo añadimos al inicio
        _messages.value = currentList
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        Log.d("ChatViewModel", "Recibidos ${newMessages.size} mensajes")
        _messages.value = newMessages
    }


    fun reset(){
        publicKeyMovil = ""
        publicKeyServer = ""
        permission = listOf()
    }





}