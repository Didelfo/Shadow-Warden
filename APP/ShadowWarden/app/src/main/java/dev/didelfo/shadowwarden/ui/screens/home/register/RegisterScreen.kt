package dev.didelfo.shadowwarden.ui.screens.home.register


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import dev.didelfo.shadowwarden.R
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.ui.screens.components.createDialogInfo
import dev.didelfo.shadowwarden.ui.screens.components.loadingView

@Composable
fun RegisterScreen(navController: NavHostController) {

    val context: Context = LocalContext.current
    val viewModel = remember { RegisterScreenViewModel(context)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuroProfundo),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        TituloSuperior()

        Logo()

        TextoInferiorLogo()

        textFieldNick(viewModel)

        boton(viewModel)

        mostrarDialogs(viewModel, navController)

    }

    loadingView(viewModel.showLoading)

}

@Composable
fun TituloSuperior(){
    //        Ponemos el mensaje
    Text(
        "Inicio de Sesión",
        fontSize = 36.sp,
        color = Cian,
        fontFamily = OpenSanBold
    )

    //        Ponemos un espacio
    Spacer(
        modifier = Modifier
            .height(30.dp)
    )
}

@Composable
fun Logo(){
    //        Ponemos la imagen
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo"
    )

//        Ponemos un espacio
    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
}

@Composable
fun TextoInferiorLogo(){
    //        Ponemos el texto
    Text(
        "Introduce tu nick de Minecraft:",
        color = VerdeMenta,
        fontSize = 14.sp,
        textAlign = TextAlign.Left,
        fontFamily = OpenSanNormal
    )
    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
}

@Composable
fun textFieldNick(viewModel:RegisterScreenViewModel){
    TextField(
        value = viewModel.txtNick,
        onValueChange = { viewModel.txtNick = it },
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

//        Ponemos un espacio
    Spacer(
        modifier = Modifier
            .height(60.dp)
    )
}

@Composable
fun boton(
    viewModel: RegisterScreenViewModel
){
    Button(
        onClick = {

            if ((viewModel.txtNick.isEmpty()) || (viewModel.txtNick.toString().length > 32)) {
                viewModel.showDialogInfo = true
            } else {
                viewModel.registrar()
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
            text = "Continuar",
            fontSize = 16.sp,
            fontFamily = OpenSanBold
        )
    }
}


@Composable
fun mostrarDialogs(
    viewModel: RegisterScreenViewModel,
    navController:NavHostController

){
    // Mostramos el dialogo de si esta vacio o la longitud no es la correcta

    if (viewModel.showDialogInfo) {

        createDialogInfo(
            painterResource(R.drawable.info),
            Cian,
            "Información",
            Cian,
            "No ha introducido nada o ha superado los 32 caracteres. Asegúrate de escribir un Nick de Minecraft valido.",
            "Aceptar",
            {
                viewModel.showDialogInfo = false
                viewModel.txtNick = ""
            })
    }

    // Mostramos el dialogo de si la cuenta no es premium

    if (viewModel.showDialogPremium) {

        createDialogInfo(
            painterResource(R.drawable.close),
            RojoCoral,
            "Error",
            RojoCoral,
            "¿Tu cuenta es Premium? Esta aplicación únicamente funciona con cuentas de Minecraft Premium.",
            "Aceptar",
            {
                viewModel.showDialogPremium = false
                viewModel.txtNick = ""
            })

    }

    // Mostramos el dialogo de si la cuenta no es premium

    if (viewModel.showDialogVerificado) {

        createDialogInfo(
            painterResource(R.drawable.check),
            VerdeEsmeralda,
            "Verificado",
            VerdeEsmeralda,
            "Tu cuenta ha sido verificada correctamente.",
            "Continuar",
            {
                navController.popBackStack()
                navController.navigate(AppScreens.HomeScreen.route)
                viewModel.showDialogPremium = false
            })
    }
}