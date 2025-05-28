package dev.didelfo.shadowwarden.security.keys

import android.content.Context
import java.security.PublicKey
import android.util.Base64
import com.google.gson.Gson
import dev.didelfo.shadowwarden.localfiles.Tokeen
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

class KeyProcessor {




}

data class ArchivoHMAC( val hmac: String, val nonce: String)