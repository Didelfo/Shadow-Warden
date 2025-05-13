package dev.didelfo.shadowwarden.utils.security.firstconection

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class KeyManager(private val context: Context, private val alias: String) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    /**
     * Genera un par de claves EC (ECDSA para firma o ECDH para acuerdo de clave).
     */
    fun generateKeyPair() {
        if (!keyStore.containsAlias(alias)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore"
            )
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_AGREE_KEY
            ).apply {
                setDigests(KeyProperties.DIGEST_SHA256)
                setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1")) // Curva P-256
                setUserAuthenticationRequired(false) // Opcional: requerir autenticación biométrica
            }.build()

            keyPairGenerator.initialize(spec)
            keyPairGenerator.generateKeyPair()
        }
    }

    /**
     * Obtiene la clave pública para enviar al servidor.
     */
    fun getPublicKey(): PublicKey {
        return keyStore.getCertificate(alias)?.publicKey
            ?: throw IllegalStateException("No existe la clave para el alias: $alias")
    }

    /**
     * Genera el secreto compartido ECDH (32 bytes para P-256).
     * ¡Asegúrate de que el servidor usa el mismo método!
     */
    fun generateSharedSecret(serverPublicKey: PublicKey): ByteArray {
        val privateKeyEntry = keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
            ?: throw IllegalStateException("No existe la clave privada para el alias: $alias")

        return KeyAgreement.getInstance("ECDH").apply {
            init(privateKeyEntry.privateKey)
            doPhase(serverPublicKey, true)
        }.generateSecret().copyOf(32) // 32 bytes para AES-256
    }

    /**
     * Descifra un archivo usando AES-GCM con el secreto compartido.
     * @param encryptedFile Archivo con formato: [IV (12 bytes)][datos cifrados].
     */
    @Throws(IOException::class)
    fun decryptFile(sharedSecret: ByteArray, encryptedFile: File): String {
        if (!encryptedFile.exists()) {
            throw IOException("El archivo cifrado no existe")
        }

        return encryptedFile.inputStream().use { inputStream ->
            // Leer IV (12 bytes para GCM)
            val iv = ByteArray(12).also {
                if (inputStream.read(it) != it.size) {
                    throw IOException("IV incompleto (se esperaban 12 bytes)")
                }
            }

            // Leer datos cifrados (resto del archivo)
            val encryptedData = inputStream.readBytes()

            // Configurar AES-GCM
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(
                    Cipher.DECRYPT_MODE,
                    SecretKeySpec(sharedSecret, "AES"),
                    GCMParameterSpec(128, iv)
                )
            }

            String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8)
        }
    }

    /**
     * Elimina la clave del KeyStore (opcional, para limpieza).
     */
    fun deleteKey() {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }
}