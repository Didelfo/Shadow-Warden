package dev.didelfo.shadowwarden.ui.screens.server.serverhome

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.R
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.components.MessageProcessor
import dev.didelfo.shadowwarden.connection.websocket.components.StructureMessage
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ServerHomeScreenViewModel(context: Context, nav: NavHostController) : ViewModel() {

    val cont = context
    val nave = nav

    var isEditing by mutableStateOf(false)
    val allItems = listOf<GridItem>(
        GridItem(R.drawable.chat, "Chat", "shadowwarden.app.ui.chat"),
    )

    fun clickItem(i: GridItem) {

        val hmactool = HmacHelper()
        val nonce = hmactool.generateNonce()
        val hmac = hmactool.generateHmac(
            ToolManager().getToken(cont).token,
            // En este punto la llave compartida es practicamente que sea null
            checkNotNull(EphemeralKeyStore.getShared()),
            nonce
        )

        var msg: StructureMessage = StructureMessage(
            "",
            "",
            "",
            hmac,
            nonce,
            ToolManager().getUser(cont).uuid,
            mapOf()
        )

        when (i.id) {
            "shadowwarden.app.ui.chat" -> {
                // Solicitamos al servidor inscribirnos en el chat
                msg.category = "register"
                msg.action = "SubscribeChat"
            }

            else -> {}
        }


        viewModelScope.launch {
            var attempts = 0
            while (!WSController.claveCompartidaUsable && attempts < 100) {
                delay(100)
                attempts++
            }

            if (!WSController.claveCompartidaUsable) {
                Log.e("HomeViewModel", "Timeout: No se completó el handshake.")
                return@launch
            }

            // Esperamos la respuesta de la peticion
            val respuesta = WSController.sendAndWaitResponse(msg)
            MessageProcessor(nave).classifyCategory(respuesta)
        }
    }
}