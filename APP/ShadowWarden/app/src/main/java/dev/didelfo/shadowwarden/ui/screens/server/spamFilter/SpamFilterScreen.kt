package dev.didelfo.shadowwarden.ui.screens.server.spamFilter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.R
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatMessage
import dev.didelfo.shadowwarden.ui.screens.server.chat.ChatScreenViewModel
import dev.didelfo.shadowwarden.ui.screens.server.serverhome.ServerHomeScreenViewModel
import dev.didelfo.shadowwarden.ui.theme.AzulGrisElegante
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.MoradoLila
import dev.didelfo.shadowwarden.ui.theme.MoradoPastel
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta

@Composable
fun SpamFilterScreen(navController: NavHostController) {


    Scaffold(
        topBar = {
            ToolBar(
                title = "Opciones",
                {
                    navController.navigate(AppScreens.HomeScreen.route)
                    WSController.closeConnection()
                }
            )
        }
    ) { innerPadding ->
        main(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private fun guardarConfig(){}


@Composable
private fun main(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AzulOscuroProfundo),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AzulVerdosoOscuro,
            tonalElevation = 10.dp,
            modifier = modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = modifier
                    .padding(24.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.spam),
                        contentDescription = "icono spam",
                        tint = MoradoPastel,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier.width(5.dp))
                    Text(
                        text = "Configuracion Filtro Spam",
                        color = MoradoPastel,
                        fontFamily = OpenSanBold,
                        fontSize = 26.sp
                    )
                    Spacer(modifier.width(5.dp))
                    Icon(
                        painter = painterResource(R.drawable.spam),
                        contentDescription = "icono spam",
                        tint = MoradoPastel,
                        modifier = modifier.size(28.dp)
                    )
                }
                Spacer(modifier.height(5.dp))
                Text(
                    text = "Modifica la configuración del filtro de spam.",
                    color = VerdeMenta,
                    fontFamily = OpenSanNormal,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier.height(10.dp))

                // Botones tipo sancion
                Row {
                    Button(
                        onClick = {
                            WSController.cliente.enableSpam = true
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (WSController.cliente.enableSpam) MoradoPastel else AzulGrisElegante
                        )
                    ) {
                        Text(
                            "Activo",
                            color = if (WSController.cliente.enableSpam) AzulOscuroProfundo else VerdeMenta,
                            fontFamily = OpenSanBold
                        )
                    }
                    Button(
                        onClick = {
                            WSController.cliente.enableSpam = false
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!WSController.cliente.enableSpam) MoradoPastel else AzulGrisElegante
                        )
                    ) {
                        Text(
                            "Desactivado",
                            color = if (!WSController.cliente.enableSpam) AzulOscuroProfundo else VerdeMenta,
                            fontFamily = OpenSanBold
                        )
                    }
                }

                Spacer(modifier.height(15.dp))

                TextField(
                    value = WSController.cliente.time.toString(),
                    onValueChange = {

                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = MoradoPastel,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = MoradoPastel,
                        fontSize = 18.sp,
                        fontFamily = OpenSanBold
                    ),
                    modifier = modifier
                        .width(70.dp)
                        .height(60.dp),
                    singleLine = true
                )



                Button(
                    onClick = {
                        guardarConfig()
                    },
                    modifier = modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoPastel
                    )
                ) {
                    Text(
                        text = "Guardar", color = AzulOscuroProfundo, fontFamily = OpenSanBold
                    )
                }

            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBar(
    title: String,
    onBackClick: () -> Unit,
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
                    contentDescription = "Atrás",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}

