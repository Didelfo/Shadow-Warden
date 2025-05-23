package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.security.E2EE.EphemeralKeyStore;
import dev.didelfo.shadowWarden.utils.ToolManager;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.permissions.Permission;
import org.java_websocket.WebSocket;

import javax.swing.plaf.SplitPaneUI;
import java.util.*;
import java.util.stream.Collectors;


public class MessageProcessor {

    private ShadowWarden pl;
    private ToolManager t = new ToolManager();

    public MessageProcessor(ShadowWarden p){
        this.pl = p;
    }

    public void process(String m, WebSocket con){
        pl.getLogger().info("Metodo proceess");
        // procesamos el mensaje en otro hilo
        pl.getExecutor().execute(() -> {

            ClientWebSocket c = pl.getWs().getClients().get(con);


            pl.getLogger().info("mensaje: " + m);

            pl.getLogger().info("Antes de obtener el objeto mensaje");
            // Procesamos el mensaje ademas huardaremos la Id  en caso de que tenga

            try {
                MessageWS mensajeCifrado = t.stringToObject(m, MessageWS.class);
            } catch (Exception e) {
                pl.getLogger().severe(e.getMessage());
                throw new RuntimeException(e);
            }

            /*
            pl.getLogger().info("Despues deobtener el mensaje");
            String id = mensajeCifrado.getId();
            pl.getLogger().info("ID Mensaje: " + id);

            pl.getLogger().info("Antes de descifrar el mensaje");

            if (c.getShareKey() != null) {
                pl.getLogger().info("key shared no null");
                // Desencriptamos el mensaje
                String msgDescifrado = pl.getE2ee().verifyAndDecrypt(
                        t.base64ToByteArray(mensajeCifrado.getData()),
                        t.base64ToByteArray(mensajeCifrado.getSignature()),
                        c.getShareKey(),
                        c.getHmacKey()
                );
                pl.getLogger().info("Despues de descifrar el mensaje");


                StructureMessage peticion = t.stringToObject(msgDescifrado, StructureMessage.class);

                classifyCategory(peticion, id, con);
            } else {
                pl.getLogger().info("key Share null");
            }

             */
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
            // Obtenemos el uuid del server para obtener el user del server
            EncryptedDatabase dbE = new EncryptedDatabase(pl);
            dbE.connect();
            String uuid = dbE.getUuidServer(p.getUuidMojan());
            dbE.close();

            // Si no es null es que existe por lo que sabemos que va a tener permisos
            if (uuid != null) {
                User user = LuckPermsProvider.get().getUserManager().getUser(UUID.fromString(uuid));
                Set<String> permissions = user.getCachedData().getPermissionData().getPermissionMap().keySet();

                List<String> shadowWardenPerms = permissions.stream()
                        .filter(per -> per.startsWith("shadowwarden."))
                        .collect(Collectors.toList());

                // DEBUG BORRAR
                pl.getLogger().info(shadowWardenPerms.toString());

                // Preparamos el objeto
                StructureMessage m = t.getStructure(p.getUuidMojan(), pl, c);
                m.setCategory("auth");
                m.setAction("GetCurrentUserPermissions");

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("permissions", shadowWardenPerms);
                m.setData(mapa);

                // Encriptamos el objeto y lo guardamos en el formato de envio

                EphemeralKeyStore.Pair<byte[], byte[]> datos = pl.getE2ee().encryptAndSign(t.objectToString(m), c.getShareKey(), c.getHmacKey());
                MessageWS msg = new MessageWS(
                        t.byteArrayToBase64(datos.first),
                        t.byteArrayToBase64(datos.second),
                        id
                );

                con.send(t.objectToString(msg));

            }

        } else {
            // Si es falsa no podemos procesar esta solicitud la rellenamos y el campo data lo dejamos vacio
            // Nota: recibimos un objeto que puede estar vacio sino se encuentra esa uuid en nuestra base de datos
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
