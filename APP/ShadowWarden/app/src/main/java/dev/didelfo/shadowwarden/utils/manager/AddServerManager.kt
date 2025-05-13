package dev.didelfo.shadowwarden.utils.manager


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import dev.didelfo.shadowwarden.R
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.Cian
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeEsmeralda
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta
import dev.didelfo.shadowwarden.utils.security.certificate.KeyCertificateTemp
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias

object AddServerManager {

//===========================================
//         Variables Fundamentales
//===========================================

    // Nav controler

    private lateinit var navController: NavHostController

    // Varriables de Icono Head
    private var statusHeadIconSecure:Boolean = false
    private var textType:String = "Palabras"

    // Variables iconos
    private var icon1:Boolean = false
    private var icon2:Boolean = false
    private var icon3:Boolean = false

    // Varialbes TextField
    private var showTextFiel:Boolean = false
    private var nameServer by mutableStateOf(" fgfg")

    // Variables Button
    private var textButton:String = "Generar"

    // Varialbe Mostrar vista palabras
    private var showWords by mutableStateOf(false)
    private var palabras: List<String> = listOf()


    // Permisos camara
    private var permisoCamara by mutableStateOf(false)
    private var verPermiso by mutableStateOf(false)



//===========================================
//         Funciones Logicas
//===========================================

    fun reiniciarVars(){
        statusHeadIconSecure = false
        textType = "Palabras"
        icon1 = false
        icon2 = false
        icon3 = false
        showTextFiel = false
        nameServer = ""
        textButton = "Generar"
        showWords = false
        palabras = listOf()
        KeyCertificateTemp(GetAliasKey().getKey(KeyAlias.KeyEncripQR)).deleteTemporaryKey()
        permisoCamara = false
        verPermiso = false
    }

    private fun generarPalabrasSeguridad(){
        palabras = KeyCertificateTemp(GetAliasKey().getKey(KeyAlias.KeyEncripQR)).generateTemporaryKeyAndMnemonic()
    }

    fun inicializarNavControler(nav:NavHostController){
        navController = nav
    }





//===========================================
//         Funciones Composable
//===========================================

    // ------------ Mostrar Palabras ---------


    @Composable
    fun viewWords() {
        if (showWords) {
            generarPalabrasSeguridad()
            // Lista de palabras de ejemplo (deberías obtenerlas del ViewModel)
//            val palabras = listOf(
//                "manzana", "banana", "naranja", "pera", "uva", "sandía",
//                "limón", "mango", "kiwi", "fresa", "melón", "piña"
//            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título opcional
                Text(
                    text = "Palabras de Seguridad",
                    color = Cian,
                    fontFamily = OpenSanBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Grid de 2 columnas con 6 filas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    items(palabras.size) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .background(
                                    color = VerdeMenta,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "${index + 1}. ${palabras[index]}",
                                color = AzulOscuroProfundo,
                            )
                        }
                    }
                }

                // Botón en la parte inferior
                Button(
                    onClick = {
                        statusHeadIconSecure = true
                        icon1 = true
                        textType = "QR"
                        textButton = "Escanear"
                        showWords = false
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                        .background(VerdeEsmeralda, RoundedCornerShape(20.dp)),
//                        .border(2.dp, color = Cian, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeEsmeralda,
                        contentColor = VerdeEsmeralda
                    )
                ) {
                    Text(
                        text = "Siguiente",
                        fontSize = 16.sp,
                        fontFamily = OpenSanBold,
                        color = AzulOscuroProfundo
                    )
                }
            }
        }
    }



    // ------------ Add Server ---------

    @Composable
    fun addView(){
        // Supongo que estas variables están en tu ViewModel
        if (!showWords) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icono centrado arriba
                Spacer(modifier = Modifier.height(5.dp))
                getIconHead()

                // Texto descriptivo
                getTextDescripcion()
                Spacer(modifier = Modifier.height(10.dp))

                // Tres iconos en horizontal
                getIconsStatus()
                Spacer(modifier = Modifier.height(5.dp))

                // Cuadro de texto (oculto según variable)
                getTextField()

                // Botón final
                Spacer(modifier = Modifier.height(10.dp))
                getButton()
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }

    @Composable
    private fun getIconHead() {
        Icon(
            painter =  if (statusHeadIconSecure) {
                painterResource(R.drawable.lock_close)
            } else {
                painterResource(R.drawable.lock_open)
            },
            contentDescription = "Back",
            tint = if (statusHeadIconSecure) {
                VerdeEsmeralda
            } else {
                RojoCoral
            },
            modifier = Modifier.size(35.dp)
        )
    }

    @Composable
    private fun getTextDescripcion(){
        Text(
            text =
                when (textType){
                    "Palabras" -> "Genera las palabras de seguridad."
                    "QR" -> "Escanea el QR."
                    "Nombre" -> "Introduce el nombre del servidor."
                    else -> ""
            },
            color = VerdeMenta,
            fontSize = 16.sp,
            fontFamily = OpenSanNormal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)

        )
    }

    @Composable
    private fun getIconsStatus(){
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon1) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.generate_words),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(15.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon2) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.qr),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(15.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (icon3) {
                            VerdeEsmeralda
                        } else {
                            VerdeMenta
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.rename),
                    contentDescription = "Icono",
                    tint = AzulVerdosoOscuro,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    @Composable
    private fun getTextField(){
        if (showTextFiel){
            TextField(
                value = nameServer,
                onValueChange = { nameServer = it },
                modifier = Modifier
                    .width(280.dp)
                    .height(48.dp)
                    .background(AzulVerdosoOscuro, RoundedCornerShape(16.dp))
                    .border(2.dp, Cian, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(17.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AzulVerdosoOscuro,
                    unfocusedContainerColor = AzulVerdosoOscuro,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = OpenSanNormal,
                    color = VerdeMenta,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }

    @Composable
    private fun getButton(){
        Button(
            onClick = {
                when (textButton){
                    "Generar" -> {
                        showWords = true
                    }
                    "Escanear" -> {
                        if (permisoCamara){
                            navController.navigate(AppScreens.ScannerScreen.route)
                        } else {
                            verPermiso = true
                        }

                    }
                    "Nombrar" -> {

                    }
                    "Finalizar" -> {

                    }
                    else -> {}
                }
            },
            modifier = Modifier
                .width(150.dp)
                .height(40.dp)
                .background(AzulVerdosoOscuro, RoundedCornerShape(20.dp))
                .border(2.dp, color = Cian, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulVerdosoOscuro,
                contentColor = VerdeMenta
            )
        ) {
            Text(
                text = textButton,
                fontSize = 16.sp,
                fontFamily = OpenSanBold
            )
        }
    }

//    ------------ Permiso Camara ------------

    @Composable
    fun pedirPermisoCamara(
        context: Context
    ) {
        var lanzarPermiso by remember { mutableStateOf(false) }

        // Launcher que se memoriza una sola vez
        val pedirPermisoLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { concedido ->
            permisoCamara = concedido
        }

        // Dispara la solicitud solo cuando verPermiso cambia a true
        LaunchedEffect(verPermiso) {
            if (verPermiso) {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (tienePermiso) {
                    permisoCamara = true
                } else {
                    lanzarPermiso = true
                }
            }

            if (lanzarPermiso) {
                pedirPermisoLauncher.launch(Manifest.permission.CAMERA)
                lanzarPermiso = false
            }
        }
    }
}