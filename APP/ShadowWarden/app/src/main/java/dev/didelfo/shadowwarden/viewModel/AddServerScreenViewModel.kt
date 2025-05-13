package dev.didelfo.shadowwarden.viewModel

import dev.didelfo.shadowwarden.R
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import android.Manifest
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.config.servers.QR
import dev.didelfo.shadowwarden.config.servers.Server
import dev.didelfo.shadowwarden.config.servers.Servers
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.screens.utils.createDialog
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.security.firstconection.FBManager


class AddServerScreenViewModel(context: Context, nave: NavHostController): ViewModel() {


//===========================================
//         Variables Fundamentales
//===========================================

    val cont = context
    val nav = nave

    // Varriables de Icono Head
    var statusHeadIconSecure:Boolean = false
    var textType:String = "Clave"

    // Variables iconos
    var icon1:Boolean = false
    var icon2:Boolean = false
    var icon3:Boolean = false

    // Varialbes TextField
    var showTextFiel:Boolean = false
    var nameServer by mutableStateOf("")

    // Variables Button
    var textButton:String = "Generar"




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
    }


    fun generarClave(){
        if (FBManager().generarLlave(cont)){
            icon1 = true
        }
    }


//===========================================
//         Funciones Composable
//===========================================





}