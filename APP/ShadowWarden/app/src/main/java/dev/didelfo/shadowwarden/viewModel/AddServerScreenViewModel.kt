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


class AddServerScreenViewModel(context: Context): ViewModel() {

    // Interfaz grafica
    var nombreServidor by mutableStateOf("")
    var textoBoton by mutableStateOf("Escanear")

    var context = context

    // QR
    var escanear by mutableStateOf(false)
    var errorNombre by mutableStateOf(false)
    var escaneado by mutableStateOf("")


    lateinit var nav:NavHostController






    @Composable
    fun escanearQR(){
        if (escanear) nav.navigate(AppScreens.ScannerScreen.route)
    }


    fun aniadir(){

        if(nombreServidor.length > 32 || nombreServidor.isEmpty() || nombreServidor.equals("") || nombreServidor.equals(" ")){
            errorNombre = true
        } else {

            val mgJSON = JSONCreator()
            var servers: Servers = Servers(arrayListOf())

            var qr = mgJSON.stringObjet(escaneado, QR::class.java)

            var server:Server = Server(nombreServidor, qr.ip, qr.port)

            if (mgJSON.exist(context, "servers.json")) {
                // Traemos el json si existe, sino se creara
                servers = mgJSON.loadObject(context, "servers.json", Servers::class.java)

            }

            // Añadimos el servidor
            servers.listaServidores.add(server)

            // Guardamos de nuevo el json
            mgJSON.saveObject(context, servers,"servers.json" )

            // Navegamos a la ventana
            nav.navigate(AppScreens.HomeScreen.route)

        }
    }


    @Composable
    fun mostrarError(){

        if (errorNombre){
            createDialog(
                painterResource(R.drawable.close),
                RojoCoral,
                "Error",
                RojoCoral,
                "El nombre supera los caracteres permitidos o no esta relleno.",
                "Aceptar",
                {
                    errorNombre = false
                }
            )
        }

    }



}