package dev.didelfo.shadowwarden.ui.screens.server


import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
fun ChatScreen(
    navController: NavHostController
) {
    // Estado de los mensajes
    val messages = remember { generateSampleMessages() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var showScrollToBottom by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    // Scroll to bottom cuando cambian los mensajes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    // Verificar si el usuario ha hecho scroll hacia arriba
    LaunchedEffect(listState.firstVisibleItemIndex) {
        showScrollToBottom = listState.firstVisibleItemIndex < messages.size - 5
    }

    // Función para manejar el envío de mensajes
    fun handleSendMessage() {
        if (messageText.isNotBlank()) {
            val newMessage = Message(
                id = messages.size + 1,
                sender = "judor",
                content = messageText,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
            messages.add(newMessage)
            messageText = ""
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_revert),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            Column {
                // Botón para ir al final
                if (showScrollToBottom) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            },
                            modifier = Modifier
                                .shadow(4.dp, RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Scroll to bottom",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Barra de entrada
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menú desplegable
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    ) {
                        IconButton(
                            onClick = { showDropdown = true }
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_more),
                                contentDescription = "More options"
                            )
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Opción 1") },
                                onClick = { showDropdown = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Opción 2") },
                                onClick = { showDropdown = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Opción 3") },
                                onClick = { showDropdown = false }
                            )
                        }
                    }

                    // Campo de texto
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .onKeyEvent { event ->
                                if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                                    handleSendMessage()
                                    true
                                } else {
                                    false
                                }
                            },
                        placeholder = { Text("Escribe un mensaje...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    // Botón de enviar
                    IconButton(
                        onClick = { handleSendMessage() },
                        enabled = messageText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar mensaje",
                            tint = if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageItem(message = message)
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val isUser = message.sender == "judor"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "${message.sender}:",
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = message.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = message.timestamp,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
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