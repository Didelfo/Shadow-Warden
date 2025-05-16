package dev.didelfo.shadowwarden.utils.json

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class JsonEncripter(private val context: Context, private val keyAlias: String) {

    init {
        // Inicializar el Keystore y generar la clave si no existe
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        if (!keyStore.containsAlias(keyAlias)) {
            generateKey(keyAlias)
        }
    }

    // Generar una clave AES y almacenarla en el Keystore
    private fun generateKey(alias: String) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    // Cifrar el JSON
    fun encryptJson(json: String): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(json.toByteArray())

        // Devolver el IV y los datos cifrados
        return iv + encryptedBytes
    }

    // Descifrar el JSON
    fun decryptJson(encryptedData: ByteArray): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey

        val iv = encryptedData.copyOfRange(0, 12)
        val encryptedBytes = encryptedData.copyOfRange(12, encryptedData.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    // Guardar el archivo cifrado
    fun saveEncryptedFile(fileName: String, encryptedData: ByteArray) {
        val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fileOutputStream.write(encryptedData)
        fileOutputStream.close()
    }

    // Leer el archivo cifrado
    fun readEncryptedFile(fileName: String): ByteArray {
        val fileInputStream = context.openFileInput(fileName)
        val fileBytes = fileInputStream.readBytes()
        fileInputStream.close()
        return fileBytes
    }

}