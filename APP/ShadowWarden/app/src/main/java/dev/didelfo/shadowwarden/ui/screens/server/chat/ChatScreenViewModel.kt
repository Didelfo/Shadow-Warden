package dev.didelfo.shadowwarden.ui.screens.server.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.didelfo.shadowwarden.connection.websocket.WSController

class ChatScreenViewModel(context: Context) : ViewModel() {

    val cont = context
    var mens = WSController.cliente.chat


}