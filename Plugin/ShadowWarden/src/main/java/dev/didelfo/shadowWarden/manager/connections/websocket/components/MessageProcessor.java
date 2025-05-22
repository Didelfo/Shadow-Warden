package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.security.E2EE.EphemeralKeyStore;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.java_websocket.WebSocket;


public class MessageProcessor {

    private ShadowWarden pl;
    private ToolManager t;

    public MessageProcessor(ShadowWarden p){
        this.pl = p;
    }

    public void process(String m, WebSocket con){
        // procesamos el mensaje en otro hilo
        pl.getExecutor().execute(() -> {

            ClientWebSocket c = pl.getWs().getClients().get(con);

            // Procesamos el mensaje ademas huardaremos la Id  en caso de que tenga
            MessageWS mensajeCifrado = t.stringToObject(m, MessageWS.class);
            String id = mensajeCifrado.getId();

            // Desencriptamos el mensaje
            String msgDescifrado = pl.getE2ee().verifyAndDecrypt(
                    t.base64ToByteArray(mensajeCifrado.getData()),
                    t.base64ToByteArray(mensajeCifrado.getSignature()),
                    c.getShareKey(),
                    c.getHmacKey()
            );

            StructureMessage peticion = t.stringToObject(msgDescifrado, StructureMessage.class);

            classifyCategory(peticion, id, con);
        });
    }


// ========================================
//     Clasificacion segun su categoria
// ========================================

    private void classifyCategory(StructureMessage p, String id, WebSocket con) {
        switch (p.getCategory()){
            case "auth" -> {
                processAuth(p, id, con);
            }
            default -> {}
        }
    }

// ========================================
//     Procesar Categoria auth
// ========================================

    private void processAuth(StructureMessage p, String id, WebSocket con){
        switch (p.getAction()){
            case "IdentifyAndCheckPermissions" -> {IdentifyAndCheckPermissions(p, id, con);}
            case "" -> {}
            default -> {}
        }
    }

    // ------------- Funcion segun peticion -------------------
    private void IdentifyAndCheckPermissions(StructureMessage p, String id, WebSocket con){
        ClientWebSocket c = pl.getWs().getClients().get(con);
        if(t.verificarHmac(p, c, pl)) {
            // Si es verdadero quiere decir que es un usuario valido

        } else {
            // Si es falsa no podemos procesar esta solicitud
            StructureMessage e = t.getStructure(p.getUuidMojan(), pl, c);
            e.setCategory("auth");
            e.setAction("GetCurrentUserPermissions");

            EphemeralKeyStore.Pair<byte[], byte[]> datos = pl.getE2ee().encryptAndSign(t.objectToString(e),c.getShareKey(), c.getHmacKey());

            if (datos != null){
                MessageWS msg = new MessageWS(t.byteArrayToBase64(datos.first), t.byteArrayToBase64(datos.second), id);
                con.send(t.objectToString(msg));
            }
            
            // Cerramos la conexion
            pl.getWs().getClients().remove(con);
            con.close();

        }
    }


}
