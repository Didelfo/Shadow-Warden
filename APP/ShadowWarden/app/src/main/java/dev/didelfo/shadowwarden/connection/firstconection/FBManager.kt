package dev.didelfo.shadowwarden.connection.firstconection

import android.content.Context
import android.util.Base64
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias

class FBManager {

    private fun conectar(): FirebaseDatabase {
        // Usa la URL específica de tu región
        val databaseUrl = "https://shadowwarden-e9645-default-rtdb.europe-west1.firebasedatabase.app"
        val app = Firebase.app
        return Firebase.database(app, databaseUrl)
    }


    fun generarLlave(context: Context, callback: (success: Boolean, alreadyExists: Boolean, error: Exception?) -> Unit) {
        val keym = KeyManager(context, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))
        val json = JSONCreator().loadObject(context, "user.json", User::class.java)

        try {
            // Generar el par de claves primero
            keym.generateKeyPair()

            val publicKeyBase64 = keym.getPublicKey().encoded.let {
                Base64.encodeToString(it, Base64.NO_WRAP)
            }
            val datos = mapOf(
                "keym" to publicKeyBase64,
                "keys" to "",
                "archivo" to ""
            )

            val databaseRef = conectar().reference.child(json.uuid)

            // Verificar si el registro ya existe
            databaseRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        // Caso 2: Ya existe
                        callback(false, true, null)
                    } else {
                        // Intentar crear el registro
                        databaseRef.setValue(datos)
                            .addOnSuccessListener {
                                // Caso 1: Éxito
                                callback(true, false, null)
                            }
                            .addOnFailureListener { error ->
                                // Caso 3: Error al crear
                                callback(false, false, error)
                            }
                    }
                } else {
                    // Caso 3: Error al verificar
                    callback(false, false, task.exception ?: Exception("Error al verificar registro"))
                }
            }
        } catch (e: Exception) {
            // Caso 3: Error en operaciones síncronas
            callback(false, false, e)
        }
    }

    fun borrarRegistro(context: Context, callback: (success: Boolean, notFound: Boolean, error: Exception?) -> Unit) {
        try {
            val json = JSONCreator().loadObject(context, "user.json", User::class.java)
            val databaseRef = conectar().reference.child(json.uuid)

            // Primero verificamos si existe el registro
            databaseRef.get().addOnCompleteListener { task ->
                when {
                    task.isSuccessful && task.result.exists() -> {
                        // El registro existe, procedemos a borrarlo
                        databaseRef.removeValue()
                            .addOnSuccessListener {
                                // Éxito al borrar
                                callback(true, false, null)
                            }
                            .addOnFailureListener { error ->
                                // Error al borrar
                                callback(false, false, error)
                            }
                    }
                    task.isSuccessful -> {
                        // El registro no existe
                        callback(false, true, null)
                    }
                    else -> {
                        // Error al verificar
                        callback(false, false, task.exception ?: Exception("Error al verificar registro"))
                    }
                }
            }
        } catch (e: Exception) {
            // Error en operaciones síncronas
            callback(false, false, e)
        }
    }


    // Obtener los datos cifrados del servidor
    fun obtenerDatosEncriptados(
        context: Context,
        callback: (success: Boolean, archivo: String?, keys: String?, error: Exception?) -> Unit
    ) {
        try {
            val json = JSONCreator().loadObject(context, "user.json", User::class.java)
            val databaseRef = conectar().reference.child(json.uuid)

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
                            callback(false, null, null, Exception("No se encontró el registro para la UUID: ${json.uuid}"))
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


    // Cambiamos los datos del archivo para mandar el token jeje
    fun actualizarArchivo(
        context: Context,
        nuevoArchivo: String,
        callback: (success: Boolean, error: Exception?) -> Unit
    ) {
        val json = JSONCreator().loadObject(context, "user.json", User::class.java)
        try {
            val databaseRef = conectar().reference.child(json.uuid)

            // Verificar si el nodo existe antes de intentar modificarlo
            databaseRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        // Actualizar solo el campo "archivo"
                        databaseRef.child("archivo").setValue(nuevoArchivo)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { error ->
                                callback(false, error)
                            }
                    } else {
                        callback(false, Exception("No se encontró el registro con la UUID: ${json.uuid}"))
                    }
                } else {
                    callback(false, task.exception ?: Exception("Error al acceder a la base de datos"))
                }
            }
        } catch (e: Exception) {
            callback(false, e)
        }
    }



}