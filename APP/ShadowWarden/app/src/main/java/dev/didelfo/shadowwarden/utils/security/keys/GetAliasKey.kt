package dev.didelfo.shadowwarden.utils.security.keys

class GetAliasKey() {


    fun getKey(alias: KeyAlias): String{

        return when (alias){
            KeyAlias.KeyToken -> "ShadowWardenTokenKey"
            KeyAlias.KeyEncripCertificado -> "ShadowWardenKeyEncripCertificate"
            KeyAlias.KeyServerEncrip -> "ShadowWardenKeyEncripServers"
        }
    }

}