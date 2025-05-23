package dev.didelfo.shadowwarden.connection.websocket.components

data class StructureMessage(
    var id: String,
    val category: String,
    val action: String,
    val hmac: String,
    val nonce: String,
    val uuidMojan: String,
    val data: Map<String, Any>
)
