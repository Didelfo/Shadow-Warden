package dev.didelfo.shadowwarden.ui.screens.server.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.screens.home.view
import dev.didelfo.shadowwarden.ui.theme.AzulGrisElegante
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.MoradoLila
import dev.didelfo.shadowwarden.ui.theme.MoradoPastel
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeEsmeralda
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ChatMessage(
    var hour: String,
    var uuid: String,
    var name: String,
    var message: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {

    val viewModel: ChatScreenViewModel = ChatScreenViewModel(LocalContext.current)

    // Estado de los mensajes
    val messages by WSController.cliente.messages.collectAsState()

    // Menu de moderacion
    var showMenu by remember { mutableStateOf(false) }
    var chatSeleccionado: ChatMessage? = null

    var textDuracion by remember { mutableStateOf("") }


    var listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var showScrollToBottom by remember { mutableStateOf(false) }


    // Verificar si el usuario ha hecho scroll hacia arriba
    LaunchedEffect(listState.firstVisibleItemIndex) {
        showScrollToBottom = listState.firstVisibleItemIndex != 0
    }


    Scaffold(
        topBar = {
            toolBar("Chat del Servidor", {
                navController.navigate(AppScreens.HomeScreen.route)
            })
        },
        bottomBar = {
            if (!showMenu) {
                bottomBar(
                    messageText,
                    { messageText = it },
                    {
                        if (messageText.isNotBlank()) {
                            viewModel.enviaalServidor(messageText)
                            messageText = ""
                        }
                    })
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulOscuroProfundo)
        ) {
            // Lista de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                reverseLayout = true
            ) {
                items(messages) { message ->
                    MessageItem(
                        message,
                        viewModel,
                        {
                            // Al hacer click
                            chatSeleccionado = message
                            showMenu = true

                        })
                }
            }

            // Botón flotante para ir al final (solo visible si haces scroll hacia arriba)
            AnimatedVisibility(
                visible = showScrollToBottom,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 102.dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                        }
                    },
                    modifier = Modifier
                        .background(VerdeMenta, shape = RoundedCornerShape(50))
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = "Ir al final",
                        tint = AzulOscuroProfundo,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Menu de moderacion
            if (showMenu && (chatSeleccionado != null)) {
                ModerationOverlay(chatSeleccionado, viewModel, { showMenu = false; viewModel.resetDuracion(); viewModel.resetTipo() })
            }

        }
    }
}


// ==========================================
// Funciones composables auxiliares
// ==========================================


@Composable
private fun ModerationOverlay(
    mensjae: ChatMessage,
    viewModel: ChatScreenViewModel,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AzulOscuroProfundo,
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.shield),
                        contentDescription = "icono escudo",
                        tint = MoradoPastel,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Menu Moderación",
                        color = MoradoPastel,
                        fontFamily = OpenSanBold,
                        fontSize = 26.sp
                    )
                    Spacer(Modifier.width(5.dp))
                    Icon(
                        painter = painterResource(R.drawable.shield),
                        contentDescription = "icono escudo",
                        tint = MoradoPastel,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "¿Qué sanción deseas dar?",
                    color = VerdeMenta,
                    fontFamily = OpenSanNormal,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = "Mensaje: ${mensjae.message}",
                    color = VerdeMenta,
                    fontFamily = OpenSanNormal,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))

                // Botones tipo sancion
                Row {
                    Button(
                        onClick = {
                            viewModel.resetTipo()
                            viewModel.MostrarMuteo = true
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.MostrarMuteo) MoradoPastel else AzulGrisElegante
                        )
                    ) {
                        Text(
                            "Muteo", color = if (viewModel.MostrarMuteo) AzulOscuroProfundo else VerdeMenta,
                            fontFamily = OpenSanBold
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.resetTipo()
                            viewModel.MostrarWarn = true
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.MostrarWarn) MoradoPastel else AzulGrisElegante
                        )
                    ) {
                        Text(
                            "Warn", color = if (viewModel.MostrarWarn) AzulOscuroProfundo else VerdeMenta,
                            fontFamily = OpenSanBold
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.resetTipo()
                            viewModel.MostrarBaneo = true
                        },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.MostrarBaneo) MoradoPastel else AzulGrisElegante
                        )
                    ) {
                        Text(
                            "Baneo", color = if (viewModel.MostrarBaneo) AzulOscuroProfundo else VerdeMenta,
                            fontFamily = OpenSanBold
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                // Contenido condicional con animación
                val showDuration by remember { derivedStateOf { viewModel.MostrarMuteo || viewModel.MostrarBaneo } }
                AnimatedVisibility(
                    visible = showDuration,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val valoresPermitidos = viewModel.valoresPermitidos
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Duración: ",
                                color = VerdeMenta,
                                fontFamily = OpenSanBold,
                                fontSize = 18.sp
                            )
                            TextField(
                                value = viewModel.duracionSeleccionada.toString(),
                                onValueChange = { newValue ->
                                    if (newValue.length <= 2 && (newValue.all { it.isDigit() } || newValue.isEmpty())) {
                                        if (newValue.isNotEmpty()) {
                                            val numero = newValue.toInt()
                                            val closestIndex =
                                                valoresPermitidos.indexOfFirst { it >= numero }
                                                    .takeIf { it != -1 } ?: valoresPermitidos.lastIndex
                                            viewModel.updateSliderIndex(closestIndex)
                                        } else {
                                            viewModel.updateSliderIndex(0)
                                        }
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
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Slider(
                            value = viewModel.sliderIndex.toFloat(),
                            onValueChange = { newIndex ->
                                viewModel.updateSliderIndex(newIndex.toInt())
                            },
                            valueRange = 0f..(valoresPermitidos.size - 1).toFloat(),
                            steps = valoresPermitidos.size - 1,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = MoradoPastel,
                                activeTrackColor = MoradoPastel,
                                inactiveTrackColor = MoradoPastel.copy(alpha = 0.4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Unidad de Tiempo:",
                            color = VerdeMenta,
                            fontFamily = OpenSanBold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    viewModel.resetDuracion()
                                    viewModel.DuracionSegundos = true
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.DuracionSegundos) MoradoPastel else AzulGrisElegante
                                ),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(
                                    text = "S",
                                    color = if (viewModel.DuracionSegundos) AzulOscuroProfundo else VerdeMenta,
                                    fontFamily = OpenSanBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.resetDuracion()
                                    viewModel.Duracionminutos = true
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.Duracionminutos) MoradoPastel else AzulGrisElegante
                                ),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(
                                    text = "M",
                                    color = if (viewModel.Duracionminutos) AzulOscuroProfundo else VerdeMenta,
                                    fontFamily = OpenSanBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.resetDuracion()
                                    viewModel.DuracionHoras = true
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.DuracionHoras) MoradoPastel else AzulGrisElegante
                                ),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(
                                    text = "H",
                                    color = if (viewModel.DuracionHoras) AzulOscuroProfundo else VerdeMenta,
                                    fontFamily = OpenSanBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.resetDuracion()
                                    viewModel.DuracionDias = true
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.DuracionDias) MoradoPastel else AzulGrisElegante
                                ),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(
                                    text = "D",
                                    color = if (viewModel.DuracionDias) AzulOscuroProfundo else VerdeMenta,
                                    fontFamily = OpenSanBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.resetDuracion()
                                    viewModel.DuracionInfinito = true
                                },
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.DuracionInfinito) MoradoPastel else AzulGrisElegante
                                ),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(
                                    text = "∞",
                                    color = if (viewModel.DuracionInfinito) AzulOscuroProfundo else VerdeMenta,
                                    fontFamily = OpenSanBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Mostrar campo de razón con animación
                val showReason by remember {
                    derivedStateOf {
                        viewModel.MostrarWarn || viewModel.MostrarBaneo || viewModel.MostrarMuteo
                    }
                }

                AnimatedVisibility(
                    visible = showReason,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Razón:",
                            color = VerdeMenta,
                            fontFamily = OpenSanBold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        TextField(
                            value = viewModel.TextoRazon,
                            onValueChange = { newText ->
                                viewModel.TextoRazon = newText
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .border(2.dp, MoradoPastel)
                                .background(MoradoLila),
                            textStyle = TextStyle(color = AzulOscuroProfundo),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MoradoLila,
                                unfocusedContainerColor = MoradoLila,
                                disabledContainerColor = MoradoLila,
                                cursorColor = AzulOscuroProfundo,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))


                Button(
                    onClick = {
                        viewModel.validarBoton()
                        if(viewModel.botonValido){

                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.botonValido){
                        MoradoPastel
                    }  else {
                        RojoCoral
                    })
                ) {
                    Text(text = if(viewModel.botonValido){
                        "Sancionar"
                    } else {
                        "Reintentar"
                    }, color = AzulOscuroProfundo, fontFamily = OpenSanBold)
                }

            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun toolBar(
    title: String,
    onIcon: () -> Unit
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
            IconButton(onClick = onIcon) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Atras",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}

@Composable
private fun bottomBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AzulGrisElegante
            )
            .padding(bottom = 12.dp) // Separación de la barra de navegación
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor flotante para icono + campo de texto
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(AzulOscuroProfundo, RoundedCornerShape(28.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Acción menú */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.label),
                            contentDescription = "Opciones",
                            tint = VerdeMenta,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    TextField(
                        value = messageText,
                        onValueChange = onMessageChange,
                        placeholder = {
                            Text(
                                "Escribe un mensaje...",
                                color = VerdeMenta,
                                fontFamily = OpenSanNormal,
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AzulOscuroProfundo,
                            unfocusedContainerColor = AzulOscuroProfundo,
                            disabledContainerColor = AzulOscuroProfundo,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Botón de envío estilizado
            Box(
                modifier = Modifier
                    .background(VerdeMenta, RoundedCornerShape(32.dp))
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = onSendMessage,
                    enabled = messageText.isNotBlank(),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send1),
                        contentDescription = "Enviar mensaje",
                        tint = AzulOscuroProfundo,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun MessageItem(
    message: ChatMessage,
    viewModel: ChatScreenViewModel,
    onClick: () -> Unit
) {
    val isUser = message.name == ToolManager().getUser(viewModel.cont).nick
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp), // Reducido vertical padding
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = screenWidth * 0.7f)
                .background(
                    color = if (isUser) AzulVerdosoOscuro else AzulGrisElegante,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp // Reducido padding vertical interno
                )
            ) {
                if (!isUser) {
                    Text(
                        text = message.name,
                        color = VerdeMenta.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = OpenSanBold,
                        modifier = Modifier.padding(bottom = 2.dp) // Reducido espacio bajo el nombre
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = message.message,
                        color = VerdeMenta,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = OpenSanNormal,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 4.dp), // Espacio mínimo entre texto y hora
                        softWrap = true
                    )

                    Text(
                        text = LocalTime.parse(message.hour)
                            .format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = VerdeMenta.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}