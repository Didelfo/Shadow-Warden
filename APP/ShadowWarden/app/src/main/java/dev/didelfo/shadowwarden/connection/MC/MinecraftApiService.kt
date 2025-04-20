package dev.didelfo.shadowwarden.connection.MC

import retrofit2.http.GET
import retrofit2.http.Path

// Interfaz para la API
interface MinecraftApiService {
    @GET("users/profiles/minecraft/{nickname}")
    suspend fun getUUID(@Path("nickname") nickname: String): MinecraftUUIDResponse

}