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


    // Obtener los datos cifrados del servidor
    fun obtenerDatosEncriptados(
        callback: (success: Boolean, archivo: String?, keys: String?, error: Exception?) -> Unit
    ) {
        try {
            val databaseRef = conectar().reference.child(user.uuid)

            databaseRef.get().addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        val snapshot = task.result
                        if (snapshot.exists()) {
                            val archivo = snapshot.child("archivo").getValue(String::class.java)
                            val keys = snapshot.child("keys").getValue(String::class.java)

                            when {
                                archivo.isNullOrEmpty() || keys.isNullOrEmpty() -> {
                                    val camposFaltantes = mutableListOf<String>()
                                    if (archivo.isNullOrEmpty()) camposFaltantes.add("archivo")
                                    if (keys.isNullOrEmpty()) camposFaltantes.add("keys")

                                    callback(
                                        false,
                                        null,
                                        null,
                                        Exception("Los siguientes campos están vacíos: ${camposFaltantes.joinToString(", ")}")
                                    )
                                }
                                else -> {
                                    callback(true, archivo, keys, null)
                                }
                            }
                        } else {
                            callback(false, null, null, Exception("No se encontró el registro para la UUID: ${user.uuid}"))
                        }
                    }
                    else -> {
                        callback(false, null, null, task.exception ?: Exception("Error al leer la base de datos"))
                    }
                }
            }
        } catch (e: Exception) {
            callback(false, null, null, e)
        }
    }


    // Ponemos el token en el reggistro
    fun actualizarToken(
        token: String,
        callback: (success: Boolean, error: Exception?) -> Unit
    ) {
        try {
            conectar().reference.child(user.uuid).child("token")
                .setValue(token)
                .addOnSuccessListener {
                    // Éxito al guardar
                    callback(true, null)
                }
                .addOnFailureListener { error ->
                    // Error al guardar
                    callback(false, error)
                }
        } catch (e: Exception) {
            // Error en operaciones síncronas
            callback(false, e)
        }
    }

    fun obtenerDatosSeguridad(callback: (keys: String?, hmac: String?, nonce: String?, error: Exception?) -> Unit) {
        try {
            val databaseRef = conectar().reference.child(user.uuid)

            databaseRef.get().addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        val snapshot = task.result
                        if (snapshot.exists()) {
                            val keys = snapshot.child("keys").getValue(String::class.java) ?: ""
                            val hmac = snapshot.child("hmac").getValue(String::class.java) ?: ""
                            val nonce = snapshot.child("nonce").getValue(String::class.java) ?: ""

                            when {
                                keys.isBlank() || hmac.isBlank() || nonce.isBlank() ->
                                    callback(null, null, null, Exception("Algunos datos de seguridad están incompletos"))
                                else ->
                                    callback(keys, hmac, nonce, null)
                            }
                        } else {
                            callback(null, null, null, Exception("No se encontró el registro"))
                        }
                    }
                    else -> {
                        callback(null, null, null, task.exception ?: Exception("Error al leer los datos"))
                    }
                }
            }
        } catch (e: Exception) {
            callback(null, null, null, e)
        }
    }


}