package dev.didelfo.shadowwarden.connection.MC



import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create



// Clase principal para manejar la lógica
class MinecraftApi{
    val moshi = Moshi.Builder()
        .add(com.squareup.moshi.KotlinJsonAdapterFactory())
        .build()

    val uuidAPI: MinecraftApiService

    init {
        uuidAPI = Retrofit.Builder()
            .baseUrl("https://api.mojang.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create()

    }

    suspend fun getUUID(nickname: String): MinecraftUUIDResponse?{
        try {
            var uuid = uuidAPI.getUUID(nickname)
            return uuid
        } catch (e: Exception) {
            return null
        }
    }


}
