package dev.didelfo.shadowwarden.connection.websocket.model

class ClientWebSocket {
    // Variables
    var publicKeyServer: String = ""
    var publicKeyMovil: String = ""
    var permission: List<String> = listOf()

    fun reset(){
        publicKeyMovil = ""
        publicKeyServer = ""
        permission = listOf()
    }




}