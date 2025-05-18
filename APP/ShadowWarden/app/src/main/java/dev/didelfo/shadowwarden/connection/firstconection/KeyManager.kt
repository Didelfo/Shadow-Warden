package dev.didelfo.shadowwarden.connection.firstconection

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
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

    // Generamos la clave
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

    // obtener clave publica
    fun getPublicKey(): PublicKey {
        return keyStore.getCertificate(alias)?.publicKey
            ?: throw IllegalStateException("No existe la clave para el alias: $alias")
    }

    // Generar clave compartida
    fun generateSharedSecret(serverPublicKey: PublicKey): ByteArray {
        val privateKeyEntry = keyStore.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
            ?: throw IllegalStateException("No existe la clave privada para el alias: $alias")

        return KeyAgreement.getInstance("ECDH").apply {
            init(privateKeyEntry.privateKey)
            doPhase(serverPublicKey, true)
        }.generateSecret().copyOf(32) // 32 bytes para AES-256
    }



    // Desencriptar string
    @Throws(IOException::class)
    fun decryptString(sharedSecret: ByteArray, encryptedString: String): String {
        val encryptedBytes = try {
            Base64.decode(encryptedString, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            throw IOException("Formato Base64 inválido", e)
        }

        if (encryptedBytes.size < 12) {
            throw IOException("Datos cifrados incompletos (no contienen IV)")
        }

        // Separar IV (12 bytes para GCM) y los datos cifrados
        val iv = encryptedBytes.copyOfRange(0, 12)
        val encryptedData = encryptedBytes.copyOfRange(12, encryptedBytes.size)

        // Configurar AES-GCM
        val cipher = try {
            Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(
                    Cipher.DECRYPT_MODE,
                    SecretKeySpec(sharedSecret, "AES"),
                    GCMParameterSpec(128, iv)
                )
            }
        } catch (e: Exception) {
            throw IOException("Error al inicializar el cifrado", e)
        }

        return try {
            String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw IOException("Error al descifrar los datos", e)
        }
    }

    // Encriptar
    @Throws(IOException::class)
    fun encryptString(sharedSecret: ByteArray, plainText: String): String {
        // Generar IV aleatorio (12 bytes recomendado para GCM)
        val iv = ByteArray(12) { (Math.random() * 256).toInt().toByte() }

        Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(sharedSecret, "AES"),
                GCMParameterSpec(128, iv)
            )
        }.run {
            val cipherText = doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            // Combinamos IV + datos cifrados y los codificamos en Base64
            val combined = ByteArray(iv.size + cipherText.size).apply {
                System.arraycopy(iv, 0, this, 0, iv.size)
                System.arraycopy(cipherText, 0, this, iv.size, cipherText.size)
            }

            return Base64.encodeToString(combined, Base64.NO_WRAP)
        }
    }


    // Elimina la clave del KeyStore (opcional, para limpieza)
    fun deleteKey() {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }
}