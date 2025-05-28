package dev.didelfo.shadowwarden.connection.websocket.components

data class StructureMessage(
    var id: String,
    var category: String,
    var action: String,
    var hmac: String,
    var nonce: String,
    var uuidMojan: String,
    var data: Map<String, Any>
)
