package dev.didelfo.shadowwarden.connection.websocket

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.didelfo.shadowwarden.connection.websocket.components.ClientWebSocket
import dev.didelfo.shadowwarden.connection.websocket.components.MessageWS
import dev.didelfo.shadowwarden.connection.websocket.components.StructureMessage
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
import kotlin.math.log

object WSController {

    private var client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentServerUrl: String? = null
    private var currentCertificate: String? = null
    private var cliente: ClientWebSocket = ClientWebSocket()
    private val pendingRequests = mutableMapOf<String, CancellableContinuation<StructureMessage>>()
    var claveCompartidaUsable by mutableStateOf(false)
    private var t = ToolManager()



// ==================================================
//           Funcionamiento princripal del WS
// ==================================================

    private val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            // Nada mas conectar mandamos la clave del movil
            sendMessage(JsonManager().objetToString(MessageWS("KeyExchange", cliente.publicKeyMovil, "")))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {

            // Ahora Usaremos un try para asegurarnos que solo recibimos un json valido
            try {
                val mensajeRecibido = JsonManager().stringToObjet(text, MessageWS::class.java)


                // Ahora procesaremos el mensaje segun el tipo
                when(mensajeRecibido.type){
                    // En este caso asumimos que solo tiene una llave en el campo "data"
                    "KeyExchange" -> {
                        // Aqui estaria la clave publica del servidor
                        cliente.publicKeyServer = mensajeRecibido.data
                        // Generamos la clave compartida
                        EphemeralKeyStore.generateSharedSecret(t.publicKeyBase64ToPublicKey(cliente.publicKeyServer))
                        // Indicamos que la clave compartida ya esta activa
                        claveCompartidaUsable = true
                    }
                    // En este caso asumimos que los datos estan encriptados
                    "Communication" -> {

                        // Como suponemos que hay esta encriptado lo desencriptamos usamos un try
                        // en caso de error de descifrado
                        try {
                            val mensajeDesencryp = t.decryptObjectMessage(mensajeRecibido)

                            // Establecemos un sistema hibrido de procesamiento si tenemos ID es porque hay una
                            // peticion esperando respuesta
                            if (mensajeDesencryp.id.isNotEmpty()){
                                pendingRequests[mensajeDesencryp.id]?.resumeWith(Result.success(mensajeDesencryp))
                                pendingRequests.remove(mensajeDesencryp.id)
                            } else {
                                // Cuando no tenga un ID sera un mensaje que procesaremos de manera normal
                            }
                        } catch (e: Exception){

                        }
                    }
                    // En caso que no tenga ninguno de estos tipos, que es imposible porque los controla nuestro sistema
                    else -> {}
                }



            } catch (e: Exception){

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

            // Generamos las claves y las guardamos en en el Cliente
            EphemeralKeyStore.clearKeys()
            EphemeralKeyStore.generateKeyPair()
            cliente.reset()
            cliente.publicKeyMovil = t.publicKeyToBase64(checkNotNull(EphemeralKeyStore.getPublicKey()))


            try {
                client = createSecureClient(server.certificate, server.ip)

                val request = Request.Builder()
                    .url(serverUrl)
                    .build()

                webSocket = client.newWebSocket(request, webSocketListener)

            } catch (e: Exception) {
                Log.d("prueba", "error al conectar: ${e.message.toString()}")
            }
        }
    }

    // Enviar un mensaje a través de WebSocket (asegúrate de que la conexión esté abierta)
    fun sendMessage(message: String) {
            webSocket?.send(message) ?: println("WebSocket no está conectado")
    }

    // Cerrar la conexión WebSocket de forma controlada
    fun closeConnection() {
        webSocket?.close(1000, "Cierre solicitado")
        webSocket = null
        currentServerUrl = null
    }

    // Metodo de mandar y esperar respuesta
    suspend fun sendAndWaitResponse(data: StructureMessage): StructureMessage {
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
