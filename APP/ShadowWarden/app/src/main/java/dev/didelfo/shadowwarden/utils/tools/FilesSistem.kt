package dev.didelfo.shadowwarden.utils.tools


import android.content.Context
import android.util.Log
import java.io.File

fun FilesSistem(context: Context, tag: String = "APP_FILES") {
    // Obtener el directorio files de la aplicación
    val filesDir = context.filesDir

    Log.d(tag, "═══════════════════════════════════════")
    Log.d(tag, "📁 Directorio de la aplicación: ${filesDir.absolutePath}")
    Log.d(tag, "═══════════════════════════════════════")

    // Función recursiva para listar archivos
    fun listFiles(directory: File, indent: String = "") {
        val files = directory.listFiles() ?: return

        for (file in files) {
            if (file.isDirectory) {
                Log.d(tag, "$indent📂 ${file.name}/")
                listFiles(file, "$indent    ")
            } else {
                val size = "%.2f KB".format(file.length() / 1024.0)
                Log.d(tag, "$indent📄 ${file.name} ($size)")
            }
        }
    }

    // Listar archivos principales
    listFiles(filesDir)

    // También listar el directorio cache si lo necesitas
    val cacheDir = context.cacheDir
    Log.d(tag, "\n═══════════════════════════════════════")
    Log.d(tag, "📁 Directorio cache: ${cacheDir.absolutePath}")
    Log.d(tag, "═══════════════════════════════════════")
    listFiles(cacheDir)

    Log.d(tag, "═══════════════════════════════════════")
    Log.d(tag, "✅ Fin del listado de archivos")
    Log.d(tag, "═══════════════════════════════════════")
}