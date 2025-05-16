package dev.didelfo.shadowwarden.config.servers

data class ServerEncrip(
    val ip: String,
    val port: Int,
    val certificate: String
)
