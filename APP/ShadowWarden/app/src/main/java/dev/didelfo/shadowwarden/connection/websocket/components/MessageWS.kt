package dev.didelfo.shadowwarden.connection.websocket.components

import com.google.gson.annotations.SerializedName

data class MessageWS(
    var data: String,
    var signature: String,
    var id: String
) {
    override fun toString(): String = "{\"data\": \"${data}\",  \"signature\": \"${signature}\", \"id\": \"${id}\"}"
}