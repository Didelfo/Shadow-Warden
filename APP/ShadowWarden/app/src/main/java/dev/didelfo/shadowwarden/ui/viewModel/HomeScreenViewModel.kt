package dev.didelfo.shadowwarden.ui.viewModel

import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.servers.Server
import dev.didelfo.shadowwarden.config.servers.Servers
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import java.io.File

class HomeScreenViewModel(contex:Context): ViewModel() {

//===========================================
//         Variables Fundamentales
//===========================================
    val cont = contex

    // Varialbles usables
    var servers: ArrayList<Server> by mutableStateOf(ArrayList<Server>())
    val user:User = JSONCreator().loadObject(cont, "user.json", User::class.java)

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

            servers = Gson().fromJson(jsonString, Servers::class.java).listaServidores
        }
    }

    fun deleteServer(server: Server) {
        // traemos los servidores desde los archivos
        val key: JsonEncripter = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))
        val newSever = JSONCreator().stringObjet(key.decryptJson(key.readEncryptedFile("servers.dat")), Servers::class.java)

        // Eliminamos el archivo
        newSever.listaServidores.remove(server)

        // Lo mostramos
        servers = newSever.listaServidores
        key.saveEncryptedFile("servers.dat", key.encryptJson(Gson().toJson(newSever)))
    }

    fun conectar(server:Server){
        // Intentamos conectar
        WSController.connect(server)
        WSController.sendMessage("Movil conectado con exito")
    }



}