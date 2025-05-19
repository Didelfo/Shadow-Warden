package dev.didelfo.shadowwarden.security.keys.alias

class GetAliasKey() {


    fun getKey(alias: KeyAlias): String{

        return when (alias){
            KeyAlias.KeyToken -> "ShadowWardenTokenKey"
            KeyAlias.KeyEncripCertificado -> "ShadowWardenKeyEncripCertificate"
            KeyAlias.KeyServerEncrip -> "ShadowWardenKeyEncripServers"
        }
    }

}