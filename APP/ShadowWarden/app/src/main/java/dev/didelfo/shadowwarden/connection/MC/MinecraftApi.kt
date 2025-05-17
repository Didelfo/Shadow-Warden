package dev.didelfo.shadowwarden.connection.MC

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MinecraftApi {
    private val client = OkHttpClient()
    private val mojangApiUrl = "https://api.mojang.com/users/profiles/minecraft/"
    private val skinApiUrl = "https://crafatar.com/renders/head/"
    private val gson = Gson()

    suspend fun getPlayerUUID(username: String): UserMinecraft? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$mojangApiUrl$username")
                    .build()

                val response = client.newCall(request).execute()
                response.use {
                    if (response.isSuccessful) {
                        val json = response.body?.string() ?: return@withContext null
                        gson.fromJson(json, UserMinecraft::class.java)
                    } else {
                        null
                    }
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    suspend fun getSkin(user: UserMinecraft, context: Context): File? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$skinApiUrl${user.id}?overlay")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return@withContext null

                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null

                val file = File(context.filesDir, "skin.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                file
            } catch (e: Exception) {
                null
            }
        }
    }
}