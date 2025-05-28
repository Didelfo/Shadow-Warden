package dev.didelfo.shadowwarden.connection.websocket.components

import android.util.Log

class MessageProcessor {



    fun classifyCategory(m: StructureMessage){

        when(m.category){
            "auth" -> {
                classifyAuth(m)
            }
            else -> {}
        }
    }

    private fun classifyAuth(m: StructureMessage){
        when(m.action){
            "GetCurrentUserPermissions" -> {
                // Savemos que es una lista de string
                val permissions: List<String> = m.data.get("permissions") as List<String>
                Log.d("Prueba", permissions.toString())
            }
            else -> {}
        }
    }


}