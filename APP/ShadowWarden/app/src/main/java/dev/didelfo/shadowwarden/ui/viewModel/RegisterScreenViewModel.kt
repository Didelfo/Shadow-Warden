package dev.didelfo.shadowwarden.ui.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.user.Tokeen
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.connection.MC.MinecraftApi
import dev.didelfo.shadowwarden.connection.MC.UserMinecraft
import dev.didelfo.shadowwarden.utils.tools.FilesSistem
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import kotlinx.coroutines.launch
import java.util.Base64
import java.util.Date
import java.util.UUID

class RegisterScreenViewModel(context: Context,) : ViewModel() {

    var cont: Context = context

    // Estado para el texto del TextField
    var txtNick by mutableStateOf("")


    // Lanzar los dialogs De informacion al usuario
    var showDialogInfo by mutableStateOf(false)
    var showDialogPremium by mutableStateOf(false)
    var showDialogVerificado by mutableStateOf(false)

    var showLoading by mutableStateOf(false)



    private fun generarToken(u: UserMinecraft){
            // Generamos un token unico usando la uuid del jugador la fecha y un uu
            val time = Date().time

            val valueRandom = UUID.randomUUID().toString()

            val uuid = u.id

            val token = "$uuid:$valueRandom:$time"

            // Encriptamos el token en base64
            val token64 = Base64.getEncoder().encodeToString(token.toByteArray())

            val user = User(u.name, u.id, "https://crafatar.com/renders/head/${uuid}?overlay")

            // guardamos el archivo
            JSONCreator().saveObject(cont, user, "user.json")


            val to = Tokeen(token64)
            val jsonToken = Gson().toJson(to)


            // Procecemos a encriptar el JSON y guardarlo
            val encript = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyToken))

            // guardamos el token
            encript.saveEncryptedFile("token.dat", encript.encryptJson(jsonToken))
    }

    fun registrar() {
        showLoading = true
        FilesSistem(cont)
        viewModelScope.launch {

            try {
                val user = MinecraftApi().getPlayerUUID(txtNick)
                if (user != null) {
                    generarToken(user)
                    MinecraftApi().getSkin(user, cont)
                    showDialogVerificado = true
                } else {
                    showDialogPremium = true
                }
            } catch (e: Exception) {
                showDialogPremium = true
            } finally {
                showLoading = false

            }
        }
    }
}