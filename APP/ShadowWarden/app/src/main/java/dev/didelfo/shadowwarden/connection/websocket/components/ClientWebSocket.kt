package dev.didelfo.shadowwarden.connection.websocket.components

class ClientWebSocket {
    // Variables
    var publicKeyServer: String = ""
    var publicKeyMovil: String = ""
    var cifrado: Boolean = false


    fun reset(){
        publicKeyMovil = ""
        publicKeyServer= ""
        cifrado = false
    }




}