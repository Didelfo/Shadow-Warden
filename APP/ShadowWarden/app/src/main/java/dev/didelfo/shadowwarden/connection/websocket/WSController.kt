package dev.didelfo.shadowwarden.connection.websocket

import android.util.Log
import dev.didelfo.shadowwarden.config.servers.Server
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.ByteArrayInputStream
import javax.net.ssl.SSLContext
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64
import javax.net.ssl.*

object WSController {

    private var client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentServerUrl: String? = null
    private var currentCertificate: String? = null


// ==================================================
//           Funcionamiento princripal del WS
// ==================================================

    private val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@WSController.webSocket = webSocket
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("prueba", text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d("prueba", t.message.toString())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("prueba", "Cerrado por: ${reason}")
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
