package dev.didelfo.shadowwarden.utils.json

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class JsonManager {

    private val gson = Gson()

    fun <T> saveObject(context: Context, obj: T, fileName: String) {
        val file = File(context.filesDir, fileName)
        val jsonString = gson.toJson(obj)
        FileWriter(file).use { it.write(jsonString) }
    }

    fun <T> loadObject(context: Context, fileName: String, clazz: Class<T>): T {
        val file = File(context.filesDir, fileName)
        //if (!file.exists()) return null

        FileReader(file).use { reader ->
            return gson.fromJson(reader, clazz)
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.delete()
    }

    fun exist(context: Context, fileName: String): Boolean{
        val file = File(context.filesDir, fileName)
        return file.exists()
    }

    fun <T> stringToObjet(json: String, clazz:Class<T>):T = gson.fromJson(json, clazz)

    fun objetToString(obj: Any): String = gson.toJson(obj)

}