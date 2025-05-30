package dev.didelfo.shadowwarden.ui.screens.home.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.components.MessageProcessor
import dev.didelfo.shadowwarden.connection.websocket.components.StructureMessage
import dev.didelfo.shadowwarden.localfiles.Server
import dev.didelfo.shadowwarden.localfiles.Servers
import dev.didelfo.shadowwarden.localfiles.User
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class HomeScreenViewModel(contex: Context, nav: NavHostController) : ViewModel() {

    //===========================================
//         Variables Fundamentales
//===========================================
    val cont = contex
    val nave = nav

    // Varialbles usables
    var servers: ArrayList<Server> by mutableStateOf(ArrayList<Server>())
    val t: ToolManager = ToolManager()
    val user: User = t.getUser(cont)


    // Variables de mostrar
    var loadingScreen by mutableStateOf(false)
    var showMenuDelete by mutableStateOf(false)


    var file by mutableStateOf(File(cont.filesDir, "skin.png"))


//===========================================
//         Funciones
//===========================================

    fun getServers() {

        if (File(cont.filesDir, "servers.dat").exists()) {

            val jsonEncrip = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))

            val jsonString: String =
                jsonEncrip.decryptJson(jsonEncrip.readEncryptedFile("servers.dat"))

            servers = JsonManager().stringToObjet(jsonString, Servers::class.java).listaServidores
        }
    }

    fun deleteServer(server: Server) {
        // traemos los servidores desde los archivos
        val key: JsonEncripter = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))
        val newSever = JsonManager().stringToObjet(
            key.decryptJson(key.readEncryptedFile("servers.dat")),
            Servers::class.java
        )

        // Eliminamos el archivo
        newSever.listaServidores.remove(server)

        // Lo mostramos
        servers = newSever.listaServidores

        key.saveEncryptedFile("servers.dat", key.encryptJson(JsonManager().objetToString(newSever)))
    }

    fun conectar(server: Server) {

        // Ponemos pantalla de carga
        loadingScreen = true

        // Conectamos el servidor
        WSController.connect(server)

        // Generamos nuestro hmac
        val hmacTool = HmacHelper()
        val nonce = hmacTool.generateNonce()
        val token = t.getToken(cont).token


        // ---------------- Pregunta asincrona -------------

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

            // Obtenemos la llave
            val keyShare = EphemeralKeyStore.getShared()

            if (keyShare != null) {
                val hmac = hmacTool.generateHmac(
                    token,
                    keyShare,
                    nonce
                )

                // El mensaje que mandamos
                val msg = StructureMessage(
                    "",
                    "auth",
                    "IdentifyAndCheckPermissions",
                    hmac,
                    nonce,
                    t.getUser(cont).uuid,
                    mapOf()
                )

                // Esperamos la respuesta de la peticion
                val respuesta = WSController.sendAndWaitResponse(msg)
                MessageProcessor(nave).classifyCategory(respuesta)

            }
            loadingScreen = false
        }
    }
}