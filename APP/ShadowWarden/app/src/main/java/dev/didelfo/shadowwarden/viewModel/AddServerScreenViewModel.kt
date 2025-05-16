package dev.didelfo.shadowwarden.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.navigation.NavHostController
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.servers.Server
import dev.didelfo.shadowwarden.config.servers.ServerEncrip
import dev.didelfo.shadowwarden.utils.security.firstconection.FBManager
import dev.didelfo.shadowwarden.utils.security.firstconection.KeyManager
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec


class AddServerScreenViewModel(context: Context, nave: NavHostController) : ViewModel() {


//===========================================
//         Variables Fundamentales
//===========================================

    val cont = context
    val nav = nave

    // Varriables de Icono Head
    var statusHeadIconSecure by mutableStateOf(false)
    var textType by mutableStateOf("Clave")

    // Variables iconos
    var icon1 by mutableStateOf(false)
    var icon2 by mutableStateOf(false)
    var icon3 by mutableStateOf(false)

    // Varialbes TextField
    var showTextFiel by mutableStateOf(false)
    var nameServer by mutableStateOf("")

    // Variables Button
    var textButton by mutableStateOf("Generar")

    // Variable activar loading
    var loadingViewStatus by mutableStateOf(false)

    // Variables Alerts
    var AlertGeneracionClaveCorrecta by mutableStateOf(false)
    var AlertGeneracionClaveExiste by mutableStateOf(false)
    var AlertGeneracionClaveError by mutableStateOf(false)

    var AlertBorrarVerificacionCorrecta by mutableStateOf(false)
    var AlertBorrarVerificacionNoEncontrado by mutableStateOf(false)
    var AlertBorrarVerificacionError by mutableStateOf(false)

    var AlertExisteDatosExito by mutableStateOf(false)
    var AlertExisteDatosNoEncontrado by mutableStateOf(false)
    var AlertExisteDatosError by mutableStateOf(false)

    // Variable servidor desencriptado
    lateinit var serverEn: ServerEncrip


//===========================================
//         Funciones Logicas
//===========================================

    fun reiniciarVars() {
        statusHeadIconSecure = false
        textType = "Clave"
        icon1 = false
        icon2 = false
        icon3 = false
        showTextFiel = false
        nameServer = ""
        textButton = "Generar"

        // Borramos la llave ya creado para que cambie en la siguiente creacion
        KeyManager(cont, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado)).deleteKey()
    }

    // ----------------- Generamos la llave ------------------------------

    fun generarClave() {
        loadingViewStatus = true
        FBManager().generarLlave(cont) { success, alreadyExists, error ->
            when {
                success -> {
                    // Caso 1: Todo bien - pasar al siguiente paso
                    AlertGeneracionClaveCorrecta = true
                    changeStatusExist()

                }

                alreadyExists -> {
                    // Caso 2: Mostrar diálogo preguntando si desea cancelar
                    AlertGeneracionClaveExiste = true
                }

                error != null -> {
                    // Caso 3: Mostrar error
                    AlertGeneracionClaveError = true

                }
            }
            loadingViewStatus = false
        }
    }

    // --------------------------- Borramos para cancelar el proceso ----------------------------------

    fun borrarClave() {
        loadingViewStatus = true
        FBManager().borrarRegistro(cont) { success, notFound, error ->
            when {
                success -> {
                    // Registro borrado exitosamente
                    AlertBorrarVerificacionCorrecta = true
                    reiniciarVars()
                }

                notFound -> {
                    // El registro no existía
                    AlertBorrarVerificacionNoEncontrado = true
                }

                error != null -> {
                    // Ocurrió un error
                    AlertBorrarVerificacionError = true
                }
            }

            loadingViewStatus = false
        }
    }

    // ------------------------ Obtenemos los daots del servidor -----------------------------

    fun obtenerDatosDesencriptar() {
        loadingViewStatus = true
        FBManager().obtenerDatosEncriptados(cont) { success, archivo, keys, error ->
            when {
                success -> {
                    // Datos obtenidos correctamente (archivo y keys no son null aquí)
                    procesarDatosEncriptados(keys.toString(), archivo.toString())

                }

                error != null -> {
                    // Manejar diferentes tipos de errores
                    when {
                        error.message?.contains("están vacíos") == true -> {
                            AlertExisteDatosNoEncontrado = true
                        }

                        error.message?.contains("No se encontró el registro") == true -> {
                            AlertExisteDatosNoEncontrado = true
                        }

                        else -> {
                            AlertExisteDatosError = true
                        }
                    }
                }
            }
            loadingViewStatus = false
        }
    }

    // Aqui procesamos los datos

    private fun procesarDatosEncriptados(keys: String, archivo: String) {

        // Le quitamos el base64
        val bytes = Base64.decode(keys, Base64.NO_WRAP)

        // 2. Crear especificación de clave X509
        val keySpec = X509EncodedKeySpec(bytes)

        // 3. Obtener KeyFactory para el algoritmo y le ponemos el tipo qu ele pusismos en java
        val keyFactory = KeyFactory.getInstance("EC")

        val keyPublicaServidor = keyFactory.generatePublic(keySpec)

        // Ahora que tenemos la llave publica calculamos la compartida
        val keymanager = KeyManager(cont, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))
        val llaveCompartida = keymanager.generateSharedSecret(keyPublicaServidor)

        // Ahora desciframos el string del archivo que contiene la informacion encriptada

        val archivoDesencriptado = keymanager.decryptString(llaveCompartida, archivo)
        serverEn = Gson().fromJson(archivoDesencriptado, ServerEncrip::class.java)

        AlertExisteDatosExito = true
        changeStatusVerific()
    }


//===========================================
//         Funciones Composable
//===========================================

    fun changeStatusExist() {
        icon1 = true
        statusHeadIconSecure = true
        textType = "Comando"
        textButton = "Verificar"

    }

    private fun changeStatusVerific(){
        icon2 = true
        showTextFiel = true
        textType = "Nombre"
        textButton = "Finalizar"
    }


}