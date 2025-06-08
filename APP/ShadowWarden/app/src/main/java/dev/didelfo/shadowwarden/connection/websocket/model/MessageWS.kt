package dev.didelfo.shadowwarden.connection.websocket.model

data class MessageWS(
    var type: String,
    var data: String,
    var signature: String
)