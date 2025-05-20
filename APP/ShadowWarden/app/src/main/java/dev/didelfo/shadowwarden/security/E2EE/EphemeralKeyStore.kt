package dev.didelfo.shadowwarden.security.E2EE


import java.security.*
import java.security.spec.ECGenParameterSpec
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.*

object EphemeralKeyStore {
    // variables
    private var keyPair: KeyPair? = null
    private var sharedSecret: ByteArray? = null
    private var hmacKey: SecretKey? = null

    init {
        // Inicializamos el singelton
        generateKeyPair()
    }

    // Generar par de claves ECDH
    fun generateKeyPair() {
        clearKeys() // Limpiar claves anteriores
        val keyPairGenerator = KeyPairGenerator.getInstance("EC")
        val ecGenParameterSpec = ECGenParameterSpec("secp521r1")
        keyPairGenerator.initialize(ecGenParameterSpec, SecureRandom())
        keyPair = keyPairGenerator.generateKeyPair()
    }

    // Obtener clave pública para compartir
    fun getPublicKey(): PublicKey? {
        return keyPair?.public
    }

    // Generar clave compartida a partir de la clave pública del otro extremo
    fun generateSharedSecret(peerPublicKey: PublicKey) {
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(keyPair?.private)
        keyAgreement.doPhase(peerPublicKey, true)

        // Deriva la clave compartida usando KDF
        sharedSecret = deriveKey(keyAgreement.generateSecret())

        // Generar clave HMAC a partir del secreto compartido
        hmacKey = generateHmacKey(sharedSecret!!)
    }

    // Deriva una clave adecuada para AES-256-GCM
    private fun deriveKey(sharedSecret: ByteArray): ByteArray {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        return messageDigest.digest(sharedSecret).copyOf(32) // Tomamos 256 bits (32 bytes)
    }

    // Genera clave HMAC a partir del secreto compartido
    private fun generateHmacKey(sharedSecret: ByteArray): SecretKey {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val hmacKeyBytes = messageDigest.digest(sharedSecret + "HMAC".toByteArray()).copyOf(64)
        return SecretKeySpec(hmacKeyBytes, "HmacSHA512")
    }

    // Cifrar mensaje con AES-256-GCM y firmar con HMAC-SHA512
    fun encryptAndSign(message: String): Pair<ByteArray, ByteArray> {
        if (sharedSecret == null) throw IllegalStateException("Shared secret not generated")

        // Cifrado AES-256-GCM
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val keySpec = SecretKeySpec(sharedSecret!!, "AES")
        val parameterSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)
        val cipherText = cipher.doFinal(message.toByteArray(Charsets.UTF_8))

        // Concatenar IV + cipherText
        val encryptedData = iv + cipherText

        // Firmar con HMAC-SHA512
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(hmacKey)
        val signature = mac.doFinal(encryptedData)

        return Pair(encryptedData, signature)
    }

    // Verificar y descifrar mensaje
    fun verifyAndDecrypt(encryptedData: ByteArray, signature: ByteArray): String {
        if (sharedSecret == null) throw IllegalStateException("Shared secret not generated")
        if (hmacKey == null) throw IllegalStateException("HMAC key not generated")

        // Verificar firma HMAC
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(hmacKey)
        val calculatedSignature = mac.doFinal(encryptedData)

        if (!MessageDigest.isEqual(calculatedSignature, signature)) {
            throw SecurityException("HMAC verification failed")
        }

        // Descifrar AES-256-GCM
        val iv = encryptedData.copyOfRange(0, 12)
        val cipherText = encryptedData.copyOfRange(12, encryptedData.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(sharedSecret!!, "AES")
        val parameterSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)
        val decryptedBytes = cipher.doFinal(cipherText)

        return String(decryptedBytes, Charsets.UTF_8)
    }

    // Limpiar claves de memoria
    fun clearKeys() {
        keyPair = null
        sharedSecret?.fill(0)
        sharedSecret = null
        hmacKey = null
    }
}