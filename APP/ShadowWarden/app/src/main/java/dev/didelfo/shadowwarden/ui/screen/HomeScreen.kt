package dev.didelfo.shadowwarden.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.config.user.UserSkin
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.Cian
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta
import dev.didelfo.shadowwarden.utils.json.JSONCreator

@Composable
fun HomeScreen(navController: NavHostController) {

    // Obetenemos el context
    val context = LocalContext.current
    val user = JSONCreator().loadObject(context, "user.json", User::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuroProfundo),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        titulo()

        imagenJugador(user)

        nombreJugador(user)

        botonServidores(navController)

        botonConfiguracion()

    }

}

@Composable
fun titulo(){

    // Mensaje de inicio
    Text(
        "Inicio",
        fontSize = 36.sp,
        color = Cian,
        fontFamily = OpenSanBold
    )

    // Ponemos un espacio
    Spacer(
        modifier = Modifier
            .height(60.dp)
    )

}

@Composable
fun imagenJugador(user:User?){

    // Ponemos la imagen
    if (user != null) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(user.url)
                    .build()
            ),
            contentDescription = "Skin del jugador",
            modifier = Modifier
                .size(300.dp)
        )

    }

    // Ponemos un espacio
    Spacer(
        modifier = Modifier
            .height(25.dp)
    )

}

@Composable
fun nombreJugador(user: User?){


    // Ponemos el texto
    if (user != null) {
        Text(
            user.nick,
            color = VerdeMenta,
            fontSize = 30.sp,
            textAlign = TextAlign.Left,
            fontFamily = OpenSanNormal
        )
    }

    // Separador
    Spacer(
        modifier = Modifier
            .height(60.dp)
    )

}

@Composable
fun botonServidores(navController:NavHostController){
    // Boton Servidores
    Button(
        onClick = {
            navController.navigate(AppScreens.ServerListScreen.route)
        },
        modifier = Modifier
            .width(230.dp)
            .height(40.dp)
            .background(AzulVerdosoOscuro, RoundedCornerShape(20.dp))
            .border(2.dp, color = Cian, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulVerdosoOscuro,
            contentColor = VerdeMenta
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Icono buscar servidor",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Servidores",
                fontSize = 16.sp,
                fontFamily = OpenSanBold
            )
        }
    }

    // Separador
    Spacer(
        modifier = Modifier
            .height(40.dp)
    )
}

@Composable
fun botonConfiguracion(){

    // Boton Ajustes
    Button(
        onClick = {},
        modifier = Modifier
            .width(230.dp)
            .height(40.dp)
            .background(AzulVerdosoOscuro, RoundedCornerShape(20.dp))
            .border(2.dp, color = Cian, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulVerdosoOscuro,
            contentColor = VerdeMenta
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Icono configuración",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Configuración",
                fontSize = 16.sp,
                fontFamily = OpenSanBold
            )
        }
    }

}