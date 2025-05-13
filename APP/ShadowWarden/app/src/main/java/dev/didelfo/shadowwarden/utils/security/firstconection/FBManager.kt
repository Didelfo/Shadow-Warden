package dev.didelfo.shadowwarden.utils.security.firstconection

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dev.didelfo.shadowwarden.config.user.User
import dev.didelfo.shadowwarden.utils.json.JSONCreator
import dev.didelfo.shadowwarden.utils.security.keys.GetAliasKey
import dev.didelfo.shadowwarden.utils.security.keys.KeyAlias
import dev.didelfo.shadowwarden.utils.security.keys.SecureManager
import dev.didelfo.shadowwarden.viewModel.AddServerScreenViewModel

class FBManager {

    private fun conectar():FirebaseDatabase {

        return FirebaseDatabase.getInstance()
    }


    fun generarLlave(context: Context): Boolean{

        var keym: KeyManager = KeyManager(context, GetAliasKey().getKey(KeyAlias.KeyEncripCertificado))


        var json = JSONCreator().loadObject(context, "user.json", User::class.java)

        keym.generateKeyPair()

        val publicKeyBase64 = keym.getPublicKey().encoded.let {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
        val datos = mapOf(
            "keym" to publicKeyBase64,
            "keys" to "",
            "archivo" to ""
        )


        var correcto: Boolean = false

        conectar().reference.child(json.uuid)
            .setValue(datos)
            .addOnSuccessListener {
                correcto =  true
            }
            .addOnFailureListener { error ->
                correcto = false
            }
        return correcto
    }


}