package dev.didelfo.shadowwarden.ui.screens.server.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
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
import dev.didelfo.shadowwarden.ui.theme.RojoCoral
import dev.didelfo.shadowwarden.ui.theme.VerdeEsmeralda
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta
import dev.didelfo.shadowwarden.utils.tools.ToolManager
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

    var listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var showScrollToBottom by remember { mutableStateOf(false) }


    // Verificar si el usuario ha hecho scroll hacia arriba
    LaunchedEffect(listState.firstVisibleItemIndex) {
        showScrollToBottom = listState.firstVisibleItemIndex != 0
    }


    Scaffold(
        topBar = { toolBar("Chat del Servidor", {
            navController.navigate(AppScreens.HomeScreen.route)
        }) },
        bottomBar = { bottomBar(
            messageText,
            {messageText = it},
            {
                if (messageText.isNotBlank()){
                    viewModel.enviaalServidor(messageText)
                    messageText = ""
                }
            }
        ) }
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
            if (showMenu){
                ModerationOverlay({}, {}, {})
            }

        }
    }
}


// ==========================================
// Funciones composables auxiliares
// ==========================================


@Composable
private fun ModerationOverlay(
    onClick1: () -> Unit,
    onClick2: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss), // cerrar al tocar fuera
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AzulOscuroProfundo,
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clickable(enabled = false) {} // para que no se cierre al tocar dentro
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.shield),
                        contentDescription = "icono escudo",
                        tint = MoradoPastel,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Menu Moderación",
                        color = MoradoPastel,
                        fontFamily = OpenSanBold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Icon(
                        painter = painterResource(R.drawable.shield),
                        contentDescription = "icono escudo",
                        tint = MoradoPastel,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "Texto",
                    color = VerdeMenta,
                    fontFamily = OpenSanNormal,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onClick1,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeEsmeralda)
                    ) {
                        Text("Si", color = AzulOscuroProfundo, fontFamily = OpenSanBold)
                    }

                    Button(
                        onClick = onClick2,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RojoCoral)
                    ) {
                        Text("No", color = AzulOscuroProfundo, fontFamily = OpenSanBold)
                    }
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
                        onValueChange = onMessageChange ,
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
                        text = LocalTime.parse(message.hour).format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = VerdeMenta.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}