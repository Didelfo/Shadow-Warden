package dev.didelfo.shadowwarden.utils.security.json

class GetAliasKey() {


    fun getKey(alias: KeyAlias): String{

        return when (alias){
            KeyAlias.KeyToken -> "ShadowWardenTokenKey"
        }
    }

}