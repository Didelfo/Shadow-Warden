package dev.didelfo.shadowwarden.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.compose.runtime.key
import androidx.navigation.NavHostController
import com.google.gson.Gson
import dev.didelfo.shadowwarden.localfiles.Server
import dev.didelfo.shadowwarden.localfiles.ServerTemporal
import dev.didelfo.shadowwarden.localfiles.Servers
import dev.didelfo.shadowwarden.localfiles.Tokeen
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import dev.didelfo.shadowwarden.connection.FireBase.FBManager
import dev.didelfo.shadowwarden.security.keys.KeyManagerKeyStore
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.security.keys.ArchivoHMAC
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.security.keys.KeyProcessor
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import java.io.File


class AddServerScreenViewModel(context: Context, nave: NavHostController) : ViewModel() {

//===========================================
//         Variables Fundamentales Logicas
//===========================================
    // Context
    val cont = context

    // Nav
    val nav = nave

    // El estado
    var statusProccess = AddServerStatus.Incial

    //


//===========================================
//         Variables Composables
//===========================================

    // Varriables de Icono Inicial de la cabeza
    var statusHeadIconSecure by mutableStateOf(false)

    // El texto que se muestra
    var textGuia by mutableStateOf("Clave")

    // Variables iconos
    var icon1 by mutableStateOf(false)
    var icon2 by mutableStateOf(false)
    var icon3 by mutableStateOf(false)
    var icon4 by mutableStateOf(false)

    // Varialbes TextField
    var showTextFiel by mutableStateOf(false)
    var nameServer by mutableStateOf("")

    // Variables Button
    var textButton by mutableStateOf("Generar")

    // Variable activar loading
    var loadingViewStatus by mutableStateOf(false)

    // Variables Alerts Paso 1
    var AlertGeneracionClaveCorrecta by mutableStateOf(false)
    var AlertGeneracionClaveExiste by mutableStateOf(false)
    var AlertGeneracionClaveError by mutableStateOf(false)

    // Variables Alerts Paso 1 Borrar
    var AlertBorrarVerificacionCorrecta by mutableStateOf(false)
    var AlertBorrarVerificacionError by mutableStateOf(false)

    // Variales Alerts Paso 2 /link
    var AlertExisteDatosExito by mutableStateOf(false)
    var AlertExisteDatosNoEncontrado by mutableStateOf(false)
    var AlertExisteDatosError by mutableStateOf(false)

    // Variables Alerts Paso 3 /verificar
    var AlertaCuentaVerificada by mutableStateOf(false)
    var AlertaCuentaError by mutableStateOf(false)


    // Variables Alerts Paso 4
    var AlertaGuardadoExitoso by mutableStateOf(false)
    var AlertaGuardadoError by mutableStateOf(false)


    // Variable servidor desencriptado
    lateinit var serverEn: ServerTemporal

    //===========================================
//         Estados
//===========================================
    fun cambiarEstado(status: AddServerStatus) {
        when (status) {
            AddServerStatus.Incial -> {
                // Icono cabeza
                statusHeadIconSecure = false
                textGuia = "Genera la clave de seguridad para tener una comunicación segura."
                showTextFiel = false
                nameServer = ""
                textButton = "Generar"
                statusProccess = AddServerStatus.Incial
                KeyManagerKeyStore(
                    cont,
                    GetAliasKey().getKey(KeyAlias.KeyEncripCertificado)
                ).deleteKey() // Borramos la clave
            }

            AddServerStatus.ComandoLink -> {
                statusHeadIconSecure = true
                icon1 = true
                textGuia =
                    "Usa el comando \\\"/link\\\" en Minecraft. Cuando obtengas la verificación pulse \\\"Verificar\\\"."
                showTextFiel = false
                nameServer = ""
                textButton = "Verificar"
                statusProccess = AddServerStatus.ComandoLink
            }

            AddServerStatus.ComandoVerificar -> {
                statusHeadIconSecure = true
                icon1 = true
                icon2 = true
                textGuia =
                    "Usa el comando \\\"/verificar\\\" en Minecraft para confirmar la cuenta."
                showTextFiel = false
                nameServer = ""
                textButton = "Vincular"
                statusProccess = AddServerStatus.ComandoVerificar
            }
            AddServerStatus.PonerNombre -> {
                statusHeadIconSecure = true
                icon1 = true
                icon2 = true
                icon3 = true
                textGuia =
                    "UIntroduce el nombre con el que deseas guardar el servidor."
                showTextFiel = true
                nameServer = ""
                textButton = "Finalizar"
                statusProccess = AddServerStatus.PonerNombre
            }

            else -> {}
        }
    }


    fun pulsarBoton() {
        when (statusProccess) {
            AddServerStatus.Incial -> {
                generarClave()
            }

            AddServerStatus.ComandoLink -> {
                obtenerDatosDesencriptar()
            }

            AddServerStatus.ComandoVerificar -> {
                comprobarVerificacion()
            }

            AddServerStatus.PonerNombre -> {
                guardarServidor()
            }

            else -> {}
        }
    }


//===========================================
//         Funciones Logicas
//===========================================

    // ----------------- Generamos la llave ------------------------------

    // Estemetodo general el par de claves.
    // Alertas controladas:
    // - AlertGeneracionClaveCorrecta
    // - AlertGeneracionClaveExiste
    // - AlertGeneracionClaveError
    private fun generarClave() {
        loadingViewStatus = true
        FBManager(cont).generarArchivoConKeyMovil { created, alreadyExists, error ->
            when {
                created -> {
                    // Caso 1: Todo bien - pasar al siguiente paso
                    AlertGeneracionClaveCorrecta = true
                    cambiarEstado(AddServerStatus.ComandoLink)

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

    // Borra nuestro registro de la base de datos controla las alertas:
    // - AlertBorrarVerificacionCorrecta
    // - AlertBorrarVerificacionError
    fun borrarClave() {
        loadingViewStatus = true
        FBManager(cont).borrarRegistro { success, error ->
            when {
                success -> {
                    // Registro borrado exitosamente
                    cambiarEstado(AddServerStatus.Incial)
                    AlertBorrarVerificacionCorrecta = true
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

        var keys = ""
        var archivo = ""

        FBManager(cont).obtenerCampo("keys",String::class.java) { valor, error ->
            if (error == null && !valor.isNullOrEmpty()) {
                keys = valor
            } else {
                AlertBorrarVerificacionError = true
            }

            FBManager(cont).obtenerCampo("archivo",String::class.java) { valor, error ->
                if (error == null && !valor.isNullOrEmpty()) {
                    archivo = valor
                } else {
                    AlertBorrarVerificacionError = true
                }

                if (!keys.isEmpty() && !archivo.isEmpty()){
                    procesarDatosEncriptados(keys, archivo)
                }

                loadingViewStatus = false
            }
        }
    }

    // Aqui procesamos los datos

    private fun procesarDatosEncriptados(key: String, archivo: String) {

        // Traemos la clave publica
        val keyPublicaServidor = ToolManager().publicKeyBase64ToPublicKey(key)

        // Ahora que tenemos la llave publica calculamos la compartida
        val keymanager =
            KeyManagerKeyStore(cont, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))
        val llaveCompartida = keymanager.generateSharedSecret(keyPublicaServidor)

        // Ahora desciframos el string del archivo que contiene la informacion encriptada
        val archivoDesencriptado = keymanager.decryptString(llaveCompartida, archivo)
        serverEn = JsonManager().stringToObjet(archivoDesencriptado, ServerTemporal::class.java)

        // Procedemos a usar la misma clave para la informacion del "archivo" y pasar nuestro token
        // cifrado para guardarlo de manera segura.
        val encripToken = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyToken))
        val token: Tokeen = JsonManager().stringToObjet(
            encripToken.decryptJson(encripToken.readEncryptedFile("token.dat")),
            Tokeen::class.java
        )
//        val tokenString = ToolManager().base64ToString(token.token)

        val tokenEncrip = keymanager.encryptString(llaveCompartida, token.token)

        FBManager(cont).actualizarCampo("token", tokenEncrip) { success, error ->
            if (success){
                AlertExisteDatosExito = true
                cambiarEstado(AddServerStatus.ComandoVerificar)
            } else {
                AlertExisteDatosError = true
                borrarClave()
                nav.popBackStack()
                nav.navigate(AppScreens.HomeScreen.route)
            }
        }
    }


    // -------------------- Comprobamos si el servidor verifico el token --------

    fun comprobarVerificacion() {
        loadingViewStatus = true

        var keys = ""
        var hmac = ""
        var nonce = ""

        FBManager(cont).obtenerCampo("keys", String::class.java){valor, error ->
            if (error == null && !valor.isNullOrEmpty()) {
                keys = valor
            } else {
                Log.d("prueba", "obtener keys")
                AlertaCuentaError = true
            }

            FBManager(cont).obtenerCampo("hmac", String::class.java){valor, error ->
                if (error == null && !valor.isNullOrEmpty()) {
                    hmac = valor
                } else {
                    Log.d("prueba", "obtener hmac")
                    AlertaCuentaError = true
                }

                FBManager(cont).obtenerCampo("nonce", String::class.java){valor, error ->
                    if (error == null && !valor.isNullOrEmpty()) {
                        nonce = valor
                    } else {
                        Log.d("prueba", "obtener nonce")
                        AlertaCuentaError = true
                    }

                    if (!keys.isEmpty() && !hmac.isEmpty() && !nonce.isEmpty()) {
                        Log.d("prueba", "antes procesar nonce")
                        procesarHMAC(keys, hmac, nonce)
                    } else {
                        Log.d("prueba", "campos vacios")
                        AlertaCuentaError = true
                    }

                }
            }
        }
        loadingViewStatus = false
    }


    private fun procesarHMAC(keys: String, hmac: String, nonce: String) {

        val key = KeyManagerKeyStore(cont, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))

        // Clave compartida
        val keyShare = key.generateSharedSecret(ToolManager().publicKeyBase64ToPublicKey(keys))

        // Archivo del servidor desencriptado con el HMAC
        val hmacServidor = key.decryptString(keyShare, hmac)

        // Obtenemos el token
        val token = ToolManager().getToken(cont).token

        // Creamos el HMAC del servidor
        val hmacMovil = HmacHelper().generateHmac(token, keyShare, nonce)

        if (HmacHelper().verifyHmac(hmacMovil, hmacServidor)) {
            Log.d("prueba", "Hmac verificado")
            AlertaCuentaVerificada = true
        } else {
            Log.d("prueba", "Hmac No verificado")
            AlertaCuentaError = false
        }
    }


    // ------------------------ Guardamos los datos del servidor encriptados -----------------------------


    fun guardarServidor() {
        loadingViewStatus = true
        if (!nameServer.isEmpty()) {

            // Creamos el servidor con el nombre
            val server: Server =
                Server(nameServer, serverEn.ip, serverEn.port, serverEn.certificado)

            val json = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyServerEncrip))

            var servers: Servers = Servers(arrayListOf())

            // Leemos el archivo ya existente, sino existe se crea
            if (File(cont.filesDir, "servers.dat").exists()) {
                val s = json.readEncryptedFile("servers.dat")
                servers = JsonManager().stringToObjet(json.decryptJson(s), Servers::class.java)
            }

            // Lo añadimos a la lista de servidores
            servers.listaServidores.add(server)

            // Guardamos el archivo con el servidor añadido
            json.saveEncryptedFile("servers.dat", json.encryptJson(JsonManager().objetToString(servers)))

            loadingViewStatus = false
            AlertaGuardadoExitoso = true
        } else {
            loadingViewStatus = false
            AlertaGuardadoError = true
        }
    }

}
// ------------------- Estados ---------------------

enum class AddServerStatus {
    Incial,
    ComandoLink,
    ComandoVerificar,
    PonerNombre
}
