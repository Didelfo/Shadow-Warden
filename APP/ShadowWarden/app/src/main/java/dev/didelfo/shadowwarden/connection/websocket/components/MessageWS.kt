package dev.didelfo.shadowwarden.connection.websocket.components

import com.google.gson.annotations.SerializedName

data class MessageWS(
    var type: String,
    var data: String,
    var signature: String
)