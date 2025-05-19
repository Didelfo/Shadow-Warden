package dev.didelfo.shadowwarden.security.HMAC


import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HmacHelper {

    private val HMAC_ALGORITHM = "HmacSHA256"
    private val HASH_ALGORITHM = "SHA-256"
    private val NONCE_BYTE_SIZE = 16 // 128 bits


    // Genera un nonce aleatorio de tamaño fijo
    fun generateNonce(): String {
        val nonce = ByteArray(NONCE_BYTE_SIZE)
        SecureRandom().nextBytes(nonce)
        return Base64.encodeToString(nonce, Base64.NO_WRAP)
    }

    // Obtiene SHA-256 del token decodificado desde Base64
    private fun getTokenHash(tokenBase64: String): ByteArray {
        val tokenBytes = Base64.decode(tokenBase64, Base64.DEFAULT)
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        return digest.digest(tokenBytes)
    }

    // Genera una firma HMAC-SHA256 de: tokenHash + nonce
    fun generateHmac(tokenBase64: String, secretKeyBytes: ByteArray, nonce: String): String {
        val tokenHash = getTokenHash(tokenBase64)

        val mac = Mac.getInstance(HMAC_ALGORITHM)
        val keySpec = SecretKeySpec(secretKeyBytes, HMAC_ALGORITHM)
        mac.init(keySpec)

        mac.update(tokenHash)       // Hash del token
        mac.update(nonce.toByteArray())  // Nonce

        val hmacBytes = mac.doFinal()
        return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
    }

    // Verifica si dos firmas HMAC coinciden
    fun verifyHmac(local: String, recivido: String): Boolean {
        return local == recivido
    }

}