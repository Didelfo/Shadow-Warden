package dev.didelfo.shadowwarden.localfiles

data class Servers(
    val listaServidores: ArrayList<Server>
)

data class Server(
    val name:String,
    val ip:String,
    val port:Int,
    val certificate: String
)

data class ServerTemporal(
    val ip: String,
    val port: Int,
    val certificado: String
)
