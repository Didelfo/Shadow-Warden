package dev.didelfo.shadowwarden.ui.screens.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.R
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController){
    logic(navController)
    view()
}


@Composable
fun logic(nav: NavHostController){

    // Obetenemos el context
    val context = LocalContext.current

    LaunchedEffect(Unit) {

        delay(1000)
        nav.popBackStack()
        if (JSONCreator().exist(context, "token.dat")){
            nav.navigate(AppScreens.HomeScreen.route)
        } else {
            nav.navigate(AppScreens.RegisterScreen.route)
        }
    }
}


@Composable
fun view(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuroProfundo),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

//        Ponemos la imagen
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )

//        Ponemos un espacio
        Spacer(
            modifier = Modifier
                .height(80.dp)
        )

//        Ponemos la rueda de carga
        CircularProgressIndicator(
            color = Cian,

            modifier = Modifier.size(40.dp)

        )


    }
}