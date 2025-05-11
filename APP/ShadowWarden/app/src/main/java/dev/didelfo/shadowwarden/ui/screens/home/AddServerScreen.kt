package dev.didelfo.shadowwarden.ui.screens.home


import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.viewModel.AddServerScreenViewModel

@Composable
fun AddServerScreen(navController: NavHostController, qr: String?) {
    val context = LocalContext.current

    // ViewModel
    val viewModel = AddServerScreenViewModel(context)

    viewModel.nav = navController

    // Si tenemos un qr, tenemos que procesarlo y verificar que es el correcto

    if ((!qr.isNullOrEmpty()) && (!qr.equals("{qr}"))) {
        viewModel.escaneado = qr
        viewModel.textoBoton = "Añadir"
    }



    viewModel.tienePermisos(context)

    viewModel.mostrarError()

    // Permisos camara
    val pedirPermisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        viewModel.cameraPermission = concedido
    }

    viewModel.escanearQR()

    Scaffold(
        containerColor = AzulOscuroProfundo,
        topBar = {
            viewToolBarAddServer(
                title = "Añadir",
                onBackClick = { navController.navigate(AppScreens.HomeScreen.route) }
            )
        },
        content = { paddingValues ->
            viewCentralAddServer(
                pedirPermisoLauncher,
                modifier = Modifier.padding(paddingValues),
                viewModel
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun viewToolBarAddServer(
    title: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = VerdeMenta,
                fontFamily = OpenSanBold,
                fontSize = 26.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AzulVerdosoOscuro
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}

@Composable
private fun viewCentralAddServer(
    pedirPermisoLauncher: ManagedActivityResultLauncher<String, Boolean>,
    modifier: Modifier = Modifier,
    viewModel: AddServerScreenViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Recuadro en el centro
        Surface(
            modifier = Modifier
                .width(350.dp)
                .height(200.dp),
            shape = RoundedCornerShape(10.dp),
//            border = BorderStroke(2.dp, Cian),
            color = AzulGrisElegante
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                contenido(pedirPermisoLauncher,viewModel)
            }
        }
    }
}

@Composable
private fun genPalabras(){

}

@Composable
private fun contenido(
    pedirPermisoLauncher: ManagedActivityResultLauncher<String, Boolean>,
    viewModel: AddServerScreenViewModel
) {
    Text(
        "Introduce el nombre del servidor:",
        color = VerdeMenta,
        fontSize = 16.sp,
        textAlign = TextAlign.Left,
        fontFamily = OpenSanNormal
    )
    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        value = viewModel.nombreServidor,
        onValueChange = { viewModel.nombreServidor = it },
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
    Spacer(modifier = Modifier.height(25.dp))

    Button(
        onClick = {
            // Escanear
            if (viewModel.textoBoton.equals("Escanear")) {
                if (viewModel.cameraPermission) {

                    viewModel.escanear = true

                } else {
                    pedirPermisoLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            if (viewModel.textoBoton.equals("Añadir")) {
                // Lógica para añadir
                viewModel.aniadir()
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
            text = viewModel.textoBoton,
            fontSize = 16.sp,
            fontFamily = OpenSanBold
        )
    }
}
