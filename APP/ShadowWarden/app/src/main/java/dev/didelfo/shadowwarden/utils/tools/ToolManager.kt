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
import android.util.Base64
import dev.didelfo.shadowwarden.localfiles.User
import dev.didelfo.shadowwarden.utils.json.JsonManager

class ToolManager {

    fun stringToBase64(string: String): String{
        return Base64.encodeToString(string.toByteArray(), Base64.NO_WRAP)
    }

    fun publicKeyToBase64(publicKey: PublicKey): String {
        return Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
    }

    fun publicKeyBase64ToPublicKey(keyPublickBase64: String): PublicKey {
        // Le quitamos el base64
        val bytes = android.util.Base64.decode(keyPublickBase64, android.util.Base64.NO_WRAP)

        // 2. Crear especificación de clave X509
        val keySpec = X509EncodedKeySpec(bytes)

        // 3. Obtener KeyFactory para el algoritmo y le ponemos el tipo qu ele pusismos en java
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