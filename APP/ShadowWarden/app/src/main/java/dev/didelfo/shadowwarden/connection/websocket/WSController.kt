package dev.didelfo.shadowwarden.connection.websocket

import android.util.Log
import dev.didelfo.shadowwarden.connection.websocket.components.ClientWebSocket
import dev.didelfo.shadowwarden.connection.websocket.components.MessageWS
import dev.didelfo.shadowwarden.localfiles.Server
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.tools.ToolManager
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.ByteArrayInputStream
import javax.net.ssl.SSLContext
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64
import javax.net.ssl.*
import java.util.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException

object WSController {

    private var client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentServerUrl: String? = null
    private var currentCertificate: String? = null
    private var cliente: ClientWebSocket = ClientWebSocket()
    private val pendingRequests = mutableMapOf<String, CancellableContinuation<MessageWS>>()
    private var t = ToolManager()



// ==================================================
//           Funcionamiento princripal del WS
// ==================================================

    private val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            cliente.publicKeyMovil = t.publicKeyToBase64(checkNotNull(EphemeralKeyStore.getPublicKey()))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // Primera conexion a cara perro
            if (!cliente.cifrado){
                cliente.publicKeyServer = text
                EphemeralKeyStore.generateSharedSecret(t.publicKeyBase64ToPublicKey(text))
                sendMessage(cliente.publicKeyMovil)
                cliente.cifrado = true
            } else {

                val mensajeRecibido = JsonManager().stringToObjet(text, MessageWS::class.java)
                val id = mensajeRecibido.id

                // Si tiene una ID lo trataremos como una peticion que esta esperando respuesta,
                // sino tiene ID lo vamos a interpretar como un mensaje normal
                if (id.isNotEmpty()){

//                    pendingRequests[id]?.resume(mensajeRecibido)
                    pendingRequests[id]?.resumeWith(Result.success(mensajeRecibido))
                    pendingRequests.remove(id)
                } // falta poner else
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d("prueba", t.message.toString())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            pendingRequests.values.forEach { cont ->
                cont.resumeWithException(IOException("Conexión cerrada: $reason"))
            }
            pendingRequests.clear()
        }
    }


// ========================================
//         Metodos de control
// ========================================


    fun connect(server: Server) {

        // Url de conexion
        val serverUrl = "wss://${server.ip}:${server.port}"

        if (serverUrl != currentServerUrl || server.certificate != currentCertificate) {
            closeConnection()
            currentServerUrl = serverUrl
            currentCertificate = server.certificate

            try {
                client = createSecureClient(server.certificate, server.ip)

                val request = Request.Builder()
                    .url(serverUrl)
                    .build()

                webSocket = client.newWebSocket(request, webSocketListener)

                EphemeralKeyStore.clearKeys()
                EphemeralKeyStore.generateKeyPair()

            } catch (e: Exception) {
                Log.d("prueba", "error al conectar: ${e.message.toString()}")
            }
        }
    }

    // Enviar un mensaje a través de WebSocket (asegúrate de que la conexión esté abierta)
    fun sendMessage(message: String) {
        if (!cliente.cifrado) {
            webSocket?.send(message) ?: println("WebSocket no está conectado")
        } /// poner else
    }

    // Cerrar la conexión WebSocket de forma controlada
    fun closeConnection() {
        webSocket?.close(1000, "Cierre solicitado")
        webSocket = null
        currentServerUrl = null
    }

    // Metodo de mandar y esperar respuesta
    suspend fun sendAndWaitResponse(data: MessageWS): MessageWS {
        return suspendCancellableCoroutine { continuation ->
            val id = UUID.randomUUID().toString()

            // Guardamos la continuación asociada al requestId
            pendingRequests[id] = continuation

            // Creamos el mensaje con requestId
            var msg = data
            msg.id = id

            //Mandamos el mensaje
            sendMessage(JsonManager().objetToString(msg))

            // Timeout opcional (ejemplo: 10 segundos)
            val timeoutJob = CoroutineScope(Dispatchers.IO).launch {
                delay(10_000)
                pendingRequests.remove(id)?.let {
                    it.resumeWithException(TimeoutException("No se recibió respuesta en 10 segundos"))
                }
            }

            // Cancelar timeout si se resuelve antes
            continuation.invokeOnCancellation {
                timeoutJob.cancel()
                pendingRequests.remove(id)
            }
        }
    }

    // Verificar si la conexión está abierta
    fun isConnected(): Boolean {
        return webSocket != null
    }

    // Obtener la URL del servidor actual
    fun getCurrentServerUrl(): String? {
        return currentServerUrl
    }


// ========================================
//         Metodos para certificado
// ========================================

    private fun createSecureClient(base64Cert: String, ip: String): OkHttpClient {
        val certificate = parseCertificate(base64Cert)
        val trustManager = createPinningTrustManager(certificate)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustManager), null)

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { hostname, _ ->
                hostname.equals(ip, ignoreCase = true)
            }
            .build()
    }

    private fun parseCertificate(base64Cert: String): X509Certificate {
        val bytes = Base64.getDecoder().decode(base64Cert)
        val factory = CertificateFactory.getInstance("X.509")
        return factory.generateCertificate(ByteArrayInputStream(bytes)) as X509Certificate
    }

    private fun createPinningTrustManager(cert: X509Certificate): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers() = arrayOf(cert)

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                if (!chain.any { it.encoded.contentEquals(cert.encoded) }) {
                    throw SSLException("Certificado del servidor no coincide con el esperado")
                }
            }
        }
    }
}
