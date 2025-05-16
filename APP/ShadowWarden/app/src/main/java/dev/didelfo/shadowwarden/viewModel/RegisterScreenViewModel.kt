package dev.didelfo.shadowwarden.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.user.Tokeen
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.connection.MC.MinecraftUUIDResponse
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import java.util.Base64
import java.util.Date
import java.util.UUID

class RegisterScreenViewModel: ViewModel() {

    // Estado para el texto del TextField
    var txtNick by mutableStateOf("")

    // Lanzar los dialogs De informacion al usuario
    var showDialogInfo by mutableStateOf(false)
    var showDialogPremium by mutableStateOf(false)
    var showDialogVerificado by mutableStateOf(false)



    fun generarToken(response: MinecraftUUIDResponse, context: Context){

        // Generamos un token unico usando la uuid del jugador la fecha y un uu
        val time = Date().time

        val valueRandom = UUID.randomUUID().toString()

        val uuid = response.uuid

        val token = "$uuid:$valueRandom:$time"

        // Encriptamos el token en base64
        val token64 = Base64.getEncoder().encodeToString(token.toByteArray())

        val user = User(response.name, response.uuid, "https://crafatar.com/renders/head/${uuid}?overlay")

        // Creamos el JSON con el usuario
//                        val jsonUser = Gson().toJson(user)

        JSONCreator().saveObject(context, user, "user.json")


        val to = Tokeen(token64)
        val jsonToken = Gson().toJson(to)


        // Procecemos a encriptar el JSON y guardarlo
        val encript = JsonEncripter(context, GetAliasKey().getKey(KeyAlias.KeyToken))

        val jsonEncriptado = encript.encryptJson(jsonToken)
        encript.saveEncryptedFile("token.dat", jsonEncriptado)

    }





}