package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.security.E2EE.EphemeralKeyStore;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.java_websocket.WebSocket;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class MessageProcessor {

    private ShadowWarden pl;
    private ToolManager t;

    public MessageProcessor(ShadowWarden p) {
        this.pl = p;
        this.t = p.getT();
    }

    public void process(MessageWS m, WebSocket con) {

        // procesamos el mensaje en otro hilo
        pl.getExecutor().execute(() -> {

            // Traemos el cliente que lo necesitaremos
            ClientWebSocket c = pl.getWs().getClients().get(con);

            // Tratamos de desencriptar el mensaje y procesarlo
            try {
                // Obtenemos el mensaje descifrado
                StructureMessage msgDescryp = t.stringToObject(pl.getE2ee().verifyAndDecrypt(
                                t.base64ToByteArray(m.getData()),
                                t.base64ToByteArray(m.getSignature()),
                                c.getShareKey(),
                                c.getHmacKey()
                        ),
                        StructureMessage.class
                );

                // Verificamos el HMAC que nos viene para comprobar la identidad del jugador
                // para ello tenemos que crear un HMAC con los datos y comprarlas
                String HMACServer = t.generarHMACServidor(msgDescryp.getUuidMojan(), c, msgDescryp.getNonce());
                // Verificamos si el HMAC es valido antes de procesar la peticion
                if (t.verificarHmac(HMACServer, msgDescryp.getHmac())) {
                    classifyCategory(msgDescryp, con);
                }
            } catch (Exception e) {
                pl.getLogger().info(e.getMessage());
            }
        });
    }


// ========================================
//     Clasificacion segun su categoria
// ========================================

    private void classifyCategory(StructureMessage p, WebSocket con) {
        // Antes de clasificar el mensaje vamos a verificar que sea valido el hmac para no gastar
        // recrusos en un mensaje no valido
        String HMACSever = t.generarHMACServidor(
                p.getUuidMojan(),
                pl.getWs().getClients().get(con),
                p.getNonce()
        );

        // Si es verdadero ya procesamos el mensaje sino directamente cerramos la conexion
        if (t.verificarHmac(HMACSever, p.getHmac())) {
            switch (p.getCategory()) {
                case "auth" -> {
                    processAuth(p, con);
                }
                default -> {
                }
            }
        } else {
            pl.getWs().closeConection(con);
        }
    }

// ========================================
//     Procesar Categoria auth
// ========================================

    private void processAuth(StructureMessage p, WebSocket con) {
        switch (p.getAction()) {
            case "IdentifyAndCheckPermissions" -> {
                try {
                    IdentifyAndCheckPermissions(p, con);
                } catch (Exception e){
                    e.printStackTrace();

                }

            }
            case "" -> {
            }
            default -> {
            }
        }
    }

    // ------------- Funcion segun peticion -------------------
    private void IdentifyAndCheckPermissions(StructureMessage p, WebSocket con) {
        ClientWebSocket c = pl.getWs().getClients().get(con);

        // Obtenemos el uuid del server para obtener el user del server para conocer sus permisos
        EncryptedDatabase dbE = new EncryptedDatabase(pl);
        dbE.connect();
        String uuid = dbE.getUuidServer(p.getUuidMojan());
        dbE.close();



        // Si no es null es que existe por lo que sabemos que va a tener permisos
        if (uuid != null) {

            List<String> perm = new ArrayList();
            perm.add("shadowwarden.app.ui.chat");

            pl.getLogger().info("Lista de permisos");
            pl.getLogger().info(perm.toString());

            // Preparamos el objeto del mensaje que vamos a mandar
            StructureMessage mensajeEnviar = new StructureMessage();
            mensajeEnviar.setId(p.getId());
            mensajeEnviar.setCategory("auth");
            mensajeEnviar.setAction("GetCurrentUserPermissions");

            Map<String, Object> mapa = new HashMap<>();
            mapa.put("permissions", perm);
            mensajeEnviar.setData(mapa);

            // Encriptamos el objeto y lo guardamos en el formato de envio
            EphemeralKeyStore.Pair<byte[], byte[]> datos = pl.getE2ee().encryptAndSign(
                    t.objectToString(mensajeEnviar),
                    c.getShareKey(),
                    c.getHmacKey()
            );

            MessageWS msg = new MessageWS(
                    "Communication",
                    t.byteArrayToBase64(datos.first),
                    t.byteArrayToBase64(datos.second)
            );

            con.send(t.objectToString(msg));


        } else {
            // Como no hemos obtenido su UUID no existe por lo que no va a tener permisos
            // Asi que directamente cerramos la conexion
            pl.getWs().closeConection(con);
        }



    }




}
