package dev.didelfo.shadowwarden.ui.screens.server.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.AzulGrisElegante
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.AzulVerdosoOscuro
import dev.didelfo.shadowwarden.ui.theme.Cian
import dev.didelfo.shadowwarden.ui.theme.OpenSanBold
import dev.didelfo.shadowwarden.ui.theme.OpenSanNormal
import dev.didelfo.shadowwarden.ui.theme.VerdeMenta
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class Message(
    val id: Int,
    val sender: String,
    val content: String,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {

    val viewModel: ChatScreenViewModel = ChatScreenViewModel()

    // Estado de los mensajes
    val messages = remember { generateSampleMessages() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var showScrollToBottom by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }


    // Verificar si el usuario ha hecho scroll hacia arriba
    LaunchedEffect(listState.firstVisibleItemIndex) {
        showScrollToBottom = listState.firstVisibleItemIndex != 0
    }


    Scaffold(
        topBar = { toolBar("Chat del Servidor", {
            navController.navigate(AppScreens.HomeScreen.route)
        }) },
        bottomBar = { bottomBar() }
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
                    MessageItem(message = message)
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
        }
    }
}


// ==========================================
// Funciones composables auxiliares
// ==========================================

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
//    messageText: String,
//    onMessageChange: (String) -> Unit,
//    onSendMessage: () -> Unit
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
                        value = "",
                        onValueChange = { } ,
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
                    onClick = {},
                    enabled = true,
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
fun MessageItem(message: Message) {
    val isUser = message.sender == "judor"
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                        text = message.sender,
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
                        text = message.content,
                        color = VerdeMenta,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = OpenSanNormal,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = 4.dp), // Espacio mínimo entre texto y hora
                        softWrap = true
                    )

                    Text(
                        text = message.timestamp,
                        color = VerdeMenta.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}






// Función para generar 50 mensajes de ejemplo
private fun generateSampleMessages(): SnapshotStateList<Message> {
    val messages = mutableStateListOf<Message>()
    val senders = listOf("judor", "amigo1", "amigo2", "bot", "admin")
    val sampleTexts = listOf(
        "¡Hola! ¿Cómo estás?",
        "¿Qué planes tienes para hoy?",
        "Nos vemos más tarde",
        "¿Has visto la última película?",
        "Estoy trabajando en un nuevo proyecto",
        "¿Quieres jugar algo?",
        "El servidor se reiniciará en 5 minutos",
        "¡Feliz cumpleaños!",
        "Reunión a las 3pm",
        "¿Has terminado el informe?",
        "El código necesita revisión",
        "¡Buena partida!",
        "Necesito ayuda con esto",
        "¿Vas a la conferencia?",
        "Checkea el último commit",
        "El servidor está caído",
        "Actualización disponible",
        "Mira este enlace interesante",
        "¿Recibiste mi correo?",
        "Llamame cuando puedas"
    )

    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val now = System.currentTimeMillis()

    for (i in 1..50) {
        val randomHour = Random.nextInt(0, 24)
        val randomMinute = Random.nextInt(0, 60)
        val time = now - Random.nextLong(0, 7 * 24 * 60 * 60 * 1000) // Últimos 7 días
        val date = Date(time)

        messages.add(
            Message(
                id = i,
                sender = senders[Random.nextInt(senders.size)],
                content = sampleTexts[Random.nextInt(sampleTexts.size)] + " " +
                        if (Random.nextBoolean()) "(mensaje #$i)" else "",
                timestamp = dateFormat.format(date)
            )
        )
    }

    // Ordenar por timestamp (más reciente primero)
    messages.sortByDescending { it.timestamp }

    return messages
}