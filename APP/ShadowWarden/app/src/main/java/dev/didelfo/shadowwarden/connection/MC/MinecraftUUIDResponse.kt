package dev.didelfo.shadowwarden.connection.MC

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MinecraftUUIDResponse(
    @Json(name = "id") val uuid: String,
    val name: String
)