package dev.didelfo.shadowwarden.utils.tools

import android.content.Context
import com.google.gson.Gson
import dev.didelfo.shadowwarden.localfiles.Tokeen
import dev.didelfo.shadowwarden.security.keys.alias.GetAliasKey
import dev.didelfo.shadowwarden.security.keys.alias.KeyAlias
import dev.didelfo.shadowwarden.utils.json.JsonEncripter
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import dev.didelfo.shadowwarden.localfiles.User
import dev.didelfo.shadowwarden.utils.json.JsonManager

class ToolManager {

    fun stringToBase64(string: String): String {
        return Base64.getEncoder().encodeToString(string.toByteArray())
    }

    fun publicKeyToBase64(publicKey: PublicKey): String {
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    fun publicKeyBase64ToPublicKey(keyPublickBase64: String): PublicKey {
        val bytes = Base64.getDecoder().decode(keyPublickBase64)
        val keySpec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(keySpec)
    }

    // Obtenemos el token de manera rapida
    fun getToken(cont: Context):Tokeen{
        val json = JsonEncripter(cont, GetAliasKey().getKey(KeyAlias.KeyToken))
        return Gson().fromJson(json.decryptJson(json.readEncryptedFile("token.dat")),Tokeen::class.java)
    }

    fun getUser(cont: Context): User = JsonManager().loadObject(cont, "user.json", User::class.java)


}