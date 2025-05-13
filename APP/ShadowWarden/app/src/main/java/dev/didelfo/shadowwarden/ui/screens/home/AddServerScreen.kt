package dev.didelfo.shadowwarden.ui.screens.home


import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import dev.didelfo.shadowwarden.utils.security.firstconection.FBManager
import dev.didelfo.shadowwarden.viewModel.AddServerScreenViewModel

@Composable
fun AddServerScreen(navController: NavHostController, qr: String?) {

    val context = LocalContext.current
    val viewModel: AddServerScreenViewModel = AddServerScreenViewModel(context, navController)

//--------------------- Top and central View --------------------

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
                modifier = Modifier.padding(paddingValues),
                viewModel
            )
        }
    )
}


// ----------- Tol bar ------------------

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


// ---------- Central View -----------

@Composable
private fun viewCentralAddServer(
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
                .width(350.dp),
            shape = RoundedCornerShape(10.dp),
//            border = BorderStroke(2.dp, Cian),
            color = AzulGrisElegante
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                centralView(viewModel)
            }
        }
    }
}



// ------------ Add Server ---------

@Composable
private fun centralView(
    viewModel: AddServerScreenViewModel
){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Icono centrado arriba
        Spacer(modifier = Modifier.height(5.dp))
        getIconHead(viewModel)

        // Texto descriptivo
        getTextDescripcion(viewModel)
        Spacer(modifier = Modifier.height(10.dp))

        // Tres iconos en horizontal
        getIconsStatus(viewModel)
        Spacer(modifier = Modifier.height(5.dp))

        // Cuadro de texto (oculto según variable)
        getTextField(viewModel)

        // Botón final
        Spacer(modifier = Modifier.height(10.dp))
        getButton(viewModel)
        Spacer(modifier = Modifier.height(5.dp))
    }

}

@Composable
private fun getIconHead(viewModel: AddServerScreenViewModel) {
    Icon(
        painter =  if (viewModel.statusHeadIconSecure) {
            painterResource(R.drawable.lock_close)
        } else {
            painterResource(R.drawable.lock_open)
        },
        contentDescription = "Back",
        tint = if (viewModel.statusHeadIconSecure) {
            VerdeEsmeralda
        } else {
            RojoCoral
        },
        modifier = Modifier.size(35.dp)
    )
}

@Composable
private fun getTextDescripcion(viewModel: AddServerScreenViewModel){
    Text(
        text =
            when (viewModel.textType){
                "Clave" -> "Genera la clave de seguridad."
                "Comando" -> "Usa el comando /link en Minecraft"
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
private fun getIconsStatus(viewModel: AddServerScreenViewModel){
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (viewModel.icon1) {
                        VerdeEsmeralda
                    } else {
                        VerdeMenta
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.key_icon),
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
                    color = if (viewModel.icon2) {
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
                    color = if (viewModel.icon3) {
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
private fun getTextField(viewModel: AddServerScreenViewModel){
    if (viewModel.showTextFiel){
        TextField(
            value = viewModel.nameServer,
            onValueChange = { viewModel.nameServer = it },
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
private fun getButton(viewModel: AddServerScreenViewModel){
    Button(
        onClick = {
            when (viewModel.textButton){
                "Generar" -> {

                    viewModel.generarClave()

                }
                "Escanear" -> {

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
            text = viewModel.textButton,
            fontSize = 16.sp,
            fontFamily = OpenSanBold
        )
    }
}

