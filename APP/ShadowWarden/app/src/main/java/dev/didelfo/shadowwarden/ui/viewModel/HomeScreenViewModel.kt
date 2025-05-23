package dev.didelfo.shadowwarden.ui.viewModel

import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dev.didelfo.shadowwarden.localfiles.Server
import dev.didelfo.shadowwarden.localfiles.Servers
import dev.didelfo.shadowwarden.localfiles.User
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.components.MessageWS
import dev.didelfo.shadowwarden.connection.websocket.components.StructureMessage
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class HomeScreenViewModel(contex:Context): ViewModel() {

//===========================================
//         Variables Fundamentales
//===========================================
    val cont = contex

    // Varialbles usables
    var servers: ArrayList<Server> by mutableStateOf(ArrayList<Server>())
    val user:User = JsonManager().loadObject(cont, "user.json", User::class.java)
    val t: ToolManager = ToolManager()

    // Variables de mostrar
    var loadingScreen by mutableStateOf(false)
    var showMenuDelete by mutableStateOf(false)


    var file by mutableStateOf(File(cont.filesDir, "skin.png"))


//===========================================
//         Funciones
//===========================================

    fun getServers() {

        if (File(cont.filesDir, "servers.dat").exists()){

            val jsonEncrip = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))

            val jsonString: String = jsonEncrip.decryptJson(jsonEncrip.readEncryptedFile("servers.dat"))

            servers = JsonManager().stringToObjet(jsonString, Servers::class.java).listaServidores
        }
    }

    fun deleteServer(server: Server) {
        // traemos los servidores desde los archivos
        val key: JsonEncripter = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))
        val newSever = JsonManager().stringToObjet(key.decryptJson(key.readEncryptedFile("servers.dat")), Servers::class.java)

        // Eliminamos el archivo
        newSever.listaServidores.remove(server)

        // Lo mostramos
        servers = newSever.listaServidores

        key.saveEncryptedFile("servers.dat", key.encryptJson(JsonManager().objetToString(newSever)))
    }

    fun conectar(server:Server){

        EphemeralKeyStore.clearKeys()
        EphemeralKeyStore.generateKeyPair()

        // Ponemos pantalla de carga
        loadingScreen = true

        // Conectamos el servidor
        WSController.connect(server)

        Log.d("prueba", "Antesdel Hmac")
        // Generamos nuestro hmac
        val hmacTool = HmacHelper()
        val nonce = hmacTool.generateNonce()
        val token = t.getToken(cont).token


        // ---------------- Codigo Nuevo -------------

        viewModelScope.launch {
            var attempts = 0
            while (!WSController.claveCompartidaUsable && attempts < 100) {
                delay(100)
                attempts++
            }

            if (!WSController.claveCompartidaUsable) {
                Log.e("HomeViewModel", "Timeout: No se completó el handshake.")
                loadingScreen = false
                return@launch
            }

            // Aqui esta el problema la clave compartida es null
            val keyShare = EphemeralKeyStore.getShared()

            if (keyShare != null) {
                Log.d("prueba", "la llave no es null")
                val hmac = hmacTool.generateHmac(
                    token,
                    keyShare,
                    nonce
                )

                val msg = StructureMessage(
                    "auth",
                    "IdentifyAndCheckPermissions",
                    hmac,
                    nonce,
                    t.getUser(cont).uuid,
                    mapOf()
                )
                val msgEncryp = t.encryptObjectMessage(msg)

                val respuesta = WSController.sendAndWaitResponse(msgEncryp)


            } else {
                Log.d("prueba", "la llave es null")

            }

            loadingScreen = false

        }






        // -------------- Codigo antiguo ----------------






//        Log.d("prueba", "antes de la creacion de hmac")
//        val hmac = hmacTool.generateHmac(
//            token,
//            checkNotNull(EphemeralKeyStore.getShared()),
//            nonce
//            )
//        Log.d("prueba", "Despues del hmac")



//        Log.d("prueba", "antes de la estructura")
//        var msg: StructureMessage = StructureMessage(
//            "auth",
//            "IdentifyAndCheckPermissions",
//            hmac,
//            nonce,
//            t.getUser(cont).uuid,
//            mapOf()
//        )

//        val msgEncryp = t.encryptObjectMessage(msg)

        // StructureMessage("","","","", "",mapOf())
//        var respuesta = MessageWS("", "", "")
//        viewModelScope.launch {
//            Log.d("prueba", "En la corrutina")
//            respuesta = WSController.sendAndWaitResponse(msgEncryp)
//        }

    }



}