package dev.didelfo.shadowwarden.connection.FireBase

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dev.didelfo.shadowwarden.localfiles.User
import dev.didelfo.shadowwarden.security.keys.KeyManagerKeyStore
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.utils.tools.ToolManager

class FBManager(context: Context) {

    private val keymanager = KeyManagerKeyStore(context, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))
    private val user = ToolManager().getUser(context)

    private fun conectar(): FirebaseDatabase {
        // Usa la URL específica de tu región
        val databaseUrl = "https://shadowwarden-e9645-default-rtdb.europe-west1.firebasedatabase.app"
        val app = Firebase.app
        return Firebase.database(app, databaseUrl)
    }


    fun generarArchivoConKeyMovil(callback: (created: Boolean, alreadyExists: Boolean, error: Exception?) -> Unit) {
        try {
            // Generar el par de claves primero
            keymanager.generateKeyPair()

            // Key en base64
            val publicKeyBase64 = ToolManager().publicKeyToBase64(keymanager.getPublicKey())

            // Archivo que vamos a crear
            val datos = mapOf(
                "keym" to publicKeyBase64,
                "keys" to "",
                "archivo" to "",
                "token" to "",
                "hmac" to "",
                "nonce" to ""
            )

            val databaseRef = conectar().reference.child(user.uuid)

            // Verificar si el registro ya existe
            databaseRef.get().addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        if (task.result.exists()) {
                            // CASO 2: El archivo ya existe
                            callback(false, true, null)
                        } else {
                            // Intentar crear el registro
                            databaseRef.setValue(datos)
                                .addOnSuccessListener {
                                    // CASO 1: Se crea el archivo correctamente
                                    callback(true, false, null)
                                }
                                .addOnFailureListener { error ->
                                    // CASO 3: Error al crear el archivo
                                    callback(false, false, error)
                                }
                        }
                    }
                    else -> {
                        // CASO 3: Error al verificar la existencia del archivo
                        callback(false, false, task.exception ?: Exception("Error al verificar la existencia del registro"))
                    }
                }
            }
        } catch (e: Exception) {
            // CASO 3: Error en operaciones síncronas (generación de claves, etc.)
            callback(false, false, e)
        }
    }

    // Método genérico para actualizar cualquier tipo de campo
    fun <T> actualizarCampo(campo: String, valor: T, callback: (success: Boolean, error: Exception?) -> Unit) {
        try {
            conectar().reference.child(user.uuid).child(campo)
                .setValue(valor)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e)
                }
        } catch (e: Exception) {
            callback(false, e)
        }
    }

    // Método genérico para obtener cualquier tipo de campo
    fun <T> obtenerCampo(campo: String, clase: Class<T>, callback: (valor: T?, error: Exception?) -> Unit) {
        try {
            conectar().reference.child(user.uuid).child(campo)
                .get()
                .addOnSuccessListener { snapshot ->
                    val valor = snapshot.getValue(clase)
                    callback(valor, null)
                }
                .addOnFailureListener { e ->
                    callback(null, e)
                }
        } catch (e: Exception) {
            callback(null, e)
        }
    }

    fun borrarRegistro(callback: (success: Boolean, error: Exception?) -> Unit) {
        try {
            val databaseRef = conectar().reference.child(user.uuid)

            // Asumimos que el registro existe y procedemos directamente a borrarlo
            databaseRef.removeValue()
                .addOnSuccessListener {
                    // Éxito al borrar
                    callback(true, null)
                }
                .addOnFailureListener { error ->
                    // Error al borrar
                    callback(false, error)
                }
        } catch (e: Exception) {
            // Error en operaciones síncronas
            callback(false, e)
        }
    }


}