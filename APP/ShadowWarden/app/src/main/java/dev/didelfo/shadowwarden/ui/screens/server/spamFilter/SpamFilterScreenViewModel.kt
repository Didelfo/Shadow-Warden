package dev.didelfo.shadowwarden.ui.screens.server.spamFilter

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.didelfo.shadowwarden.connection.websocket.WSController
import dev.didelfo.shadowwarden.connection.websocket.model.MessageWS
import dev.didelfo.shadowwarden.connection.websocket.model.StructureMessage
import dev.didelfo.shadowwarden.security.E2EE.EphemeralKeyStore
import dev.didelfo.shadowwarden.security.HMAC.HmacHelper
import dev.didelfo.shadowwarden.ui.navigation.AppNavigator
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.utils.json.JsonManager
import dev.didelfo.shadowwarden.utils.tools.ToolManager

class SpamFilterScreenViewModel(context: android.content.Context) : ViewModel(){

    var cont = context

    fun guardarConfig(){

        val hmacTool = HmacHelper()
        val nonce = hmacTool.generateNonce()

        try {

            val key = checkNotNull(EphemeralKeyStore.getShared())

            val hmac = hmacTool.generateHmac(
                ToolManager().getToken(cont).token,
                key,
                nonce
            )

            val msg = StructureMessage(
                "",
                "config",
                "SetConfigSpamFilter",
                hmac,
                nonce,
                ToolManager().getUser(cont).uuid,
                mapOf<String, Any>(
                    "enable" to WSController.cliente.enableSpam,
                    "time" to WSController.cliente.time.toInt()
                )
            )

            Log.d("prueba", "" + WSController.cliente.time.toInt())

            val pair = EphemeralKeyStore.encryptAndSign(JsonManager().objetToString(msg))

            val msgFinal = MessageWS(
                "Communication",
                ToolManager().byteArrayToBase64(pair.first),
                ToolManager().byteArrayToBase64(pair.second)
            )

            WSController.sendMessage(JsonManager().objetToString(msgFinal))


            AppNavigator.navController?.navigate(AppScreens.HomeScreen.route)
            WSController.closeConnection()

        } catch (e: Exception){
            AppNavigator.navController?.navigate(AppScreens.HomeScreen.route)
            WSController.closeConnection()
        }

    }


}