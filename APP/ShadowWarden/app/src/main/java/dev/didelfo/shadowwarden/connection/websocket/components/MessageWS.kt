package dev.didelfo.shadowwarden.connection.websocket.components

import com.google.gson.annotations.SerializedName

data class MessageWS(
    @SerializedName("data") var data: String,
    @SerializedName("signature") var signature: String,
    @SerializedName("id") var id: String
)