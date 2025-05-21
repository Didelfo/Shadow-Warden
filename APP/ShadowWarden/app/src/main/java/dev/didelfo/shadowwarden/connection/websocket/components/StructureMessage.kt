package dev.didelfo.shadowwarden.connection.websocket.components

data class StructureMessage(
    val category: String,
    val action: String,
    val hmac: String,
    val nonce: String,
    val data: Map<String, Any>
)
