package dev.didelfo.shadowwarden.utils.json

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JSONCreator {

    private val gson = Gson()

    /**
     * Guarda un objeto en el almacenamiento interno como JSON.
     */
    fun <T> saveObject(context: Context, obj: T, fileName: String) {
        val file = File(context.filesDir, fileName)
        val jsonString = gson.toJson(obj)
        FileWriter(file).use { it.write(jsonString) }
    }

    /**
     * Carga un objeto desde el almacenamiento interno.
     */
    fun <T> loadObject(context: Context, fileName: String, clazz: Class<T>): T {
        val file = File(context.filesDir, fileName)
        //if (!file.exists()) return null

        FileReader(file).use { reader ->
            return gson.fromJson(reader, clazz)
        }
    }

    /**
     * Elimina un archivo del almacenamiento interno.
     */
    fun deleteFile(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.delete()
    }

    fun exist(context: Context, fileName: String): Boolean{
        val file = File(context.filesDir, fileName)
        return file.exists()
    }

    fun <T> stringObjet(json: String, clazz:Class<T>):T = gson.fromJson(json, clazz)


}