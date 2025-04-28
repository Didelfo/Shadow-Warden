package dev.didelfo.shadowwarden.connection.websocket

import dev.didelfo.shadowwarden.config.servers.Server
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WSController {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentServerUrl: String? = null

    // WebSocketListener para manejar los eventos de WebSocket
    private val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("Conexión WebSocket abierta con el servidor: $currentServerUrl")
            webSocket.send("Hola, servidor!")  // Mensaje inicial
            this@WSController.webSocket = webSocket // Guardamos la referencia al WebSocket
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Mensaje recibido: $text")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("Error en la conexión: ${t.message}")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("Conexión cerrada: $reason")
        }

    }

    // Iniciar la conexión WebSocket con una URL dinámica
    fun startConnection(server: Server) {

        val serverUrl = "ws://${server.ip}:${server.port}"

        if (serverUrl != currentServerUrl) {
            // Si la URL es diferente, cambiamos la URL actual y cerramos la conexión anterior si existe
            closeConnection()  // Cerrar cualquier conexión previa
            currentServerUrl = serverUrl  // Actualizamos la URL del servidor

            // Creamos una nueva solicitud WebSocket con la URL proporcionada
            val request = Request.Builder()
                .url(serverUrl)  // Usamos la URL proporcionada
                .build()

            // Iniciamos la nueva conexión
            client.newWebSocket(request, webSocketListener)
        }
    }

    // Enviar un mensaje a través de WebSocket (asegúrate de que la conexión esté abierta)
    fun sendMessage(message: String) {
        webSocket?.send(message) ?: println("WebSocket no está conectado")
    }

    // Cerrar la conexión WebSocket de forma controlada
    fun closeConnection() {
        webSocket?.close(1000, "Cierre solicitado")  // Cerrar la conexión con un código y motivo
        webSocket = null  // Limpiar la referencia al WebSocket
        currentServerUrl = null  // Limpiar la URL actual
    }

    // Verificar si la conexión está abierta
    fun isConnected(): Boolean {
        return webSocket != null
    }

    // Obtener la URL del servidor actual
    fun getCurrentServerUrl(): String? {
        return currentServerUrl
    }
}
