package dev.didelfo.shadowwarden.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.utils.security.firstconection.FBManager
import dev.didelfo.shadowwarden.utils.security.firstconection.KeyManager
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias


class AddServerScreenViewModel(context: Context, nave: NavHostController): ViewModel() {


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



//===========================================
//         Funciones Logicas
//===========================================

    fun reiniciarVars(){
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

    fun borrarClave(){
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


    fun obtenerDatosEncriptados() {
        loadingViewStatus = true
        FBManager().obtenerDatosEncriptados(cont) { success, archivo, keys, error ->
            when {
                success -> {
                    // Datos obtenidos correctamente (archivo y keys no son null aquí)
//                    AlertDatosObtenidosCorrectamente = true
//                    procesarDatosEncriptados(archivo!!, keys!!)
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

//===========================================
//         Funciones Composable
//===========================================

    fun changeStatusExist(){
        icon1 = true
        statusHeadIconSecure = true
        textType = "Comando"
        textButton = "Verificar"

    }





}