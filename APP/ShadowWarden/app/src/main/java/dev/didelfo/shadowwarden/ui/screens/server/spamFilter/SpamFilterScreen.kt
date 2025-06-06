package dev.didelfo.shadowwarden.ui.screens.server.spamFilter

import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.AzulGrisElegante
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.MoradoPastel
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta


@Composable
fun SpamFilterScreen(navController: NavHostController) {

    var context = LocalContext.current
    var viewModel:SpamFilterScreenViewModel = SpamFilterScreenViewModel(context)


    Scaffold(
        topBar = {
            ToolBar(
                title = "Filtro Spam",
                {
                    navController.navigate(AppScreens.HomeScreen.route)
                    WSController.closeConnection()
                }
            )
        }
    ) { innerPadding ->
        main(
            modifier = Modifier.padding(innerPadding), viewModel
        )
    }
}



@Composable
private fun main(
    modifier: Modifier,
    viewmodel: SpamFilterScreenViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuroProfundo)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título con iconos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.spam),
                    contentDescription = "icono spam",
                    tint = MoradoPastel,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Filtro Spam",
                    color = MoradoPastel,
                    fontFamily = OpenSanBold,
                    fontSize = 24.sp
                )
                Icon(
                    painter = painterResource(R.drawable.spam),
                    contentDescription = "icono spam",
                    tint = MoradoPastel,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Modifica la configuración del filtro de spam.",
                color = VerdeMenta,
                fontFamily = OpenSanNormal,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de activar/desactivar
            Row{
                Button(
                    onClick = { WSController.cliente.enableSpam = true },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (WSController.cliente.enableSpam) MoradoPastel else AzulGrisElegante
                    )
                ) {
                    Text(
                        text = "Activo",
                        color = if (WSController.cliente.enableSpam) AzulOscuroProfundo else VerdeMenta,
                        fontFamily = OpenSanBold
                    )
                }

                Button(
                    onClick = { WSController.cliente.enableSpam = false },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!WSController.cliente.enableSpam) MoradoPastel else AzulGrisElegante
                    )
                ) {
                    Text(
                        text = "Desactivado",
                        color = if (!WSController.cliente.enableSpam) AzulOscuroProfundo else VerdeMenta,
                        fontFamily = OpenSanBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de tiempo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = WSController.cliente.time.toString(),
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("\\d+"))) {
                            WSController.cliente.time = newValue.toIntOrNull() ?: 0
                        }
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
                    modifier = Modifier
                        .width(70.dp)
                        .height(60.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Segundos",
                    color = VerdeMenta,
                    fontFamily = OpenSanBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            Button(
                onClick = { viewmodel.guardarConfig() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPastel),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Guardar",
                    color = AzulOscuroProfundo,
                    fontFamily = OpenSanBold
                )
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

