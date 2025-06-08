package dev.didelfo.shadowwarden.ui.screens.server.chat

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.components.MessageProcessor
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


    // Variables mostrar menu
    var MostrarMuteo by mutableStateOf(false)
    var MostrarWarn by mutableStateOf(false)
    var MostrarBaneo by mutableStateOf(false)

    var DuracionSegundos by mutableStateOf(false)
    var Duracionminutos by mutableStateOf(false)
    var DuracionHoras by mutableStateOf(false)
    var DuracionDias by mutableStateOf(false)
    var DuracionInfinito by mutableStateOf(false)


    var TextoRazon by mutableStateOf("")
    var botonValido by mutableStateOf(false)

    var showMenu by mutableStateOf(false)


    // Slide bar, - Barra horizontal
    val valoresPermitidos = listOf(1, 5, 10, 15, 20, 25, 30)
    var sliderIndex by mutableStateOf(0)
        private set
    var duracionSeleccionada by mutableStateOf(valoresPermitidos[0])
        private set

    fun updateSliderIndex(index: Int) {
        sliderIndex = index.coerceIn(valoresPermitidos.indices)
        duracionSeleccionada = valoresPermitidos[index]
    }

    fun onSancionar(m: ChatMessage) {
        val datos = HashMap<String, Any>()

        // Ponemos el jugador que recibe la sancion
        datos.put("jugador", m.name)

        // Ponemos el tipo de sancion
        if (MostrarMuteo) datos.put("type", "mute")
        if (MostrarWarn) datos.put("type", "warn")
        if (MostrarBaneo) datos.put("type", "ban")

        // duracion de la sancio
        if (DuracionInfinito) {
            datos.put("duracion", "never")
        } else {
            datos.put("duracion", duracionSeleccionada.toString())
        }

        // La unidad de tiempo
        if (DuracionSegundos) datos.put("unidad", "s")
        if (Duracionminutos) datos.put("unidad", "m")
        if (DuracionHoras) datos.put("unidad", "h")
        if (DuracionDias) datos.put("unidad", "d")

        // Ponemos la razon
        datos.put("razon", TextoRazon)

        // Y el nombre del staff
        datos.put("nameStaff", ToolManager().getUser(cont).nick)


        // Preparamos la estructura para enviar

        try {
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

                val hmacTool = HmacHelper()
                val nonce = hmacTool.generateNonce()
                val key = EphemeralKeyStore.getShared()
                if (key != null) {
                    val hmac = hmacTool.generateHmac(
                        ToolManager().getToken(cont).token,
                        key,
                        nonce
                    )

                    val msg: StructureMessage = StructureMessage(
                        "",
                        "moderation",
                        "ChatSanction",
                        hmac,
                        nonce,
                        ToolManager().getUser(cont).uuid,
                        datos
                    )

                    showMenu = false // Ocultamos el menu ya lelgara la respuesta cuando se procese
                    val respuesta = WSController.sendAndWaitResponse(msg)
                    MessageProcessor().classifyCategory(respuesta)
                }
            }


        } catch (e: Exception) {
            WSController.closeConnection()
        }
    }

    fun validarBoton() {
        if (
            (MostrarMuteo || MostrarBaneo) &&
            (DuracionSegundos || Duracionminutos || DuracionHoras || DuracionDias || DuracionInfinito) &&
            TextoRazon.isNotBlank()
        ) {
            botonValido = true
        } else {
            if (MostrarWarn && TextoRazon.isNotBlank()) {
                botonValido = true
            } else {
                botonValido = false
            }
        }
    }


    fun resetTipo() {
        MostrarMuteo = false
        MostrarWarn = false
        MostrarBaneo = false
    }

    fun resetDuracion() {
        DuracionSegundos = false
        Duracionminutos = false
        DuracionHoras = false
        DuracionDias = false
        DuracionInfinito = false
    }


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


            var map: Map<String, Any> = mapOf(
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
            WSController.cliente.addMessage(
                ChatMessage(
                    LocalTime.now().toString(),
                    "",
                    ToolManager().getUser(cont).nick,
                    msg
                )
            )
        }
    }
}