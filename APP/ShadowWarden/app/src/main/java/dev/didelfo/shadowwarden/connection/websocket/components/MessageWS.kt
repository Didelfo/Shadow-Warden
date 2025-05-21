package dev.didelfo.shadowwarden.connection.websocket.components

data class MessageWS(
    var data: String,
    var signature: String,
    var id: String
)