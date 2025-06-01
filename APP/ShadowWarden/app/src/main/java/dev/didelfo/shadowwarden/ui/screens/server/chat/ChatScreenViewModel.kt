package dev.didelfo.shadowwarden.ui.screens.server.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.model.MessageWS
import dev.didelfo.shadowwarden.connection.websocket.model.StructureMessage
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class ChatScreenViewModel(context: Context) : ViewModel() {

    val cont = context

    fun enviaalServidor(msg: String) {


        val hmacTool = HmacHelper()

        val nonce = hmacTool.generateNonce()
        val shareKey = EphemeralKeyStore.getShared()

        if (shareKey != null) {

            val hmac = hmacTool.generateHmac(
                ToolManager().getToken(cont).token,
                shareKey,
                nonce
            )


            var map:Map<String, Any> = mapOf(
                "mensaje" to msg,
                "usuario" to ToolManager().getUser(cont).nick
            )

            var msgEnviaar: StructureMessage = StructureMessage(
                "",
                "chat",
                "MessageSend",
                hmac,
                nonce,
                ToolManager().getUser(cont).uuid,
                map
            )

            // Encriptamos el mensaje
            val par = EphemeralKeyStore.encryptAndSign(JsonManager().objetToString(msgEnviaar))

            val msgEnviarCi: MessageWS = MessageWS(
                "Communication",
                ToolManager().byteArrayToBase64(par.first),
                ToolManager().byteArrayToBase64(par.second)
            )

            // Enviamos
            WSController.sendMessage(JsonManager().objetToString(msgEnviarCi))

            // Añadimos a nuestro chat
            WSController.cliente.addMessage(ChatMessage(
                LocalTime.now().toString(),
                "",
                ToolManager().getUser(cont).nick,
                msg
            ))
        }
    }
}