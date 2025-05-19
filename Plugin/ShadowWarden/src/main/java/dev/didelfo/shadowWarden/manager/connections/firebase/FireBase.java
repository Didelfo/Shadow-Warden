package dev.didelfo.shadowWarden.manager.connections.firebase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.component.ArchivoHMAC;
import dev.didelfo.shadowWarden.manager.connections.firebase.component.ArchivoPasar;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.security.certificate.CertificateManager;
import dev.didelfo.shadowWarden.security.hmac.HmacUtil;
import okhttp3.*;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FireBase {

    private static final String FIREBASE_URL = "https://shadowwarden-e9645-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final OkHttpClient client = new OkHttpClient();
    private ShadowWarden plugin;

    public FireBase(ShadowWarden pl){
        this.plugin = pl;
    }


// ==============================
//        Metodo Principal
// ==============================

    public void link(Player p){
        plugin.getExecutor().execute(() -> {

            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Comenzando el linkeo....");

            String uuidMojan = uuidMojan(p);
            // Comprobamos que existe el registro y alguien con esta uuid esta intentando vincular este servidor
            if (existeUUID(uuidMojan)){

                // Ahora que tenemos seguro que existe generamos un par de llaves aqui.
                ServerKey key = new ServerKey();
                key.generateKeyPair();

                try {
                    // Desciframos la llave
                    byte[] publickeyBytes = Base64.getDecoder().decode(obtenerKeym(uuidMojan));
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publickeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("EC");

                    // Obtenemos la clave compartida
                    byte[] shareKey = key.generateSharedSecret(keyFactory.generatePublic(keySpec));

                    // Generamos el archivo que encriptaremos
                    ArchivoPasar archivo = new ArchivoPasar(
                            plugin.getConfig().getString("websocket.ip"),
                            plugin.getConfig().getInt("websocket.port"),
                            new CertificateManager(plugin).getCertificateAsString()
                    );

                    String archivotxt = archivo.toJson();

                    String archivoEncrip = key.encryptText(shareKey, archivotxt);

                    String keys = Base64.getEncoder().encodeToString(key.getPublicKey().getEncoded());

                    actualizarArchivoYKeys(uuidMojan, archivoEncrip, keys);


                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Linkeado con exito. Continua en la app.");

                } catch (Exception e) {
                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Error");
                    throw new RuntimeException(e);
                }
            } else {
                plugin.getMsgManager().showMessage(p, MessageType.Staff, "No hay ninguna solicitud activa.");
            }
        });
    }

    // Metodo para obtener el token

    public synchronized void verificar(Player p){
        plugin.getExecutor().execute(() -> {

            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Comenzando la verificación....");

            String uuidMojan = uuidMojan(p);
            // Comprobamos que existe el registro y alguien con esta uuid esta intentando vincular este servidor
            if (existeUUID(uuidMojan)){
                // Ahora que tenemos seguro que existe generamos un par de llaves aqui.
                ServerKey key = new ServerKey();
                key.generateKeyPair();

                try {
                    // Desciframos la llave
                    byte[] publickeyBytes = Base64.getDecoder().decode(obtenerKeym(uuidMojan));
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publickeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("EC");

                    // Obtenemos la clave compartida
                    byte[] shareKey = key.generateSharedSecret(keyFactory.generatePublic(keySpec));

                    String desencriptado = key.decryptText(shareKey, obtenerArchivo(uuidMojan));

                    if (verificarToken(desencriptado, uuidMojan)){
                        // Obtenemos la base de datos con la que guardaremos el token
                        EncryptedDatabase bdEncrip = new EncryptedDatabase(plugin);

                        // Guardamos el token en la base de datos
                        bdEncrip.connect();

                        // Comprobamos si existe ya este registro 
                        if (bdEncrip.getAllTokens().contains(p.getName())){
                            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Esta cuenta ya esta registrada");
                            bdEncrip.close();
                        } else {

                            bdEncrip.insertToken(uuidMojan, p, desencriptado);
                            bdEncrip.close();

                            // Conesguimos el HMAC
                            String nonce = HmacUtil.generateNonce();
                            String hmac = HmacUtil.generateHmac(desencriptado, shareKey, nonce);

                            String archivo = new Gson().toJson(new ArchivoHMAC(hmac, nonce));

                            String archivoEncrip = key.encryptText(shareKey, archivo);


                            // Ahora actualizamos los archivos y pasamos el HMAC para verificar en el movil
                            actualizarArchivoYKeys(
                                    uuidMojan,
                                    archivoEncrip,
                                    Base64.getEncoder().encodeToString(key.getPublicKey().getEncoded())
                            );


                            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Verificado con exito. Continua en la app.");
                        }
                    } else {
                        plugin.getMsgManager().showMessage(p, MessageType.Staff, "Tu token no es valido.");
                    }
                } catch (Exception e) {
                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Error");
                    throw new RuntimeException(e);
                }
            } else {
                plugin.getMsgManager().showMessage(p, MessageType.Staff, "No hay ninguna solicitud activa.");
            }
        });
    }

// ==============================
//        Metodos consultas
// ==============================

    // Comprobar que existe un registro con esta uuid
    private boolean existeUUID(String uuid){
        String url = FIREBASE_URL + uuid + ".json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            plugin.getLogger().info(responseBody);
            plugin.getLogger().info(uuid);
            return !responseBody.equals("null"); // null sino
        } catch (IOException e) {
            plugin.getLogger().info("Error existe UUID");
            throw new RuntimeException(e);
        }
    }

    // Verificar el token
    private boolean verificarToken(String tokenBase64, String uuid){
        byte[] decodedBytes = Base64.getDecoder().decode(tokenBase64);
        String token = new String(decodedBytes);

        if (token.startsWith(uuid)){
            return  true;
        } else {
            return  false;
        }
    }

    // Obtener el la key del movil
    private static String obtenerKeym(String uuid) {
        String url = FIREBASE_URL + uuid + "/keym.json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (responseBody.equals("null")) {
                throw new RuntimeException("UUID no existe o no tiene 'keym'");
            }
            // Elimina las comillas del JSON
            return responseBody.replaceAll("\"", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtener el token de archivo
    private String obtenerArchivo(String uuid) {
        String url = FIREBASE_URL + uuid + "/archivo.json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (responseBody.equals("null")) {
                throw new RuntimeException("UUID no existe o no tiene 'archivo'");
            }
            // Elimina las comillas del JSON
            return responseBody.replaceAll("\"", "");
        } catch (IOException e) {
            throw new RuntimeException("Error al obtener el archivo de Firebase", e);
        }
    }

    // Actualizar datos
    private static void actualizarArchivoYKeys(String uuid, String archivo, String keys) {
        String url = FIREBASE_URL + uuid + ".json";

        JsonObject updates = new JsonObject();
        updates.addProperty("archivo", archivo);
        updates.addProperty("keys", keys);

        RequestBody body = RequestBody.create(
                updates.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al actualizar: " + response.body().string());
            }
            System.out.println("Actualizado correctamente");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String uuidMojan(Player p){
        String url = "https://api.mojang.com/users/profiles/minecraft/";
        OkHttpClient client1 = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url + p.getName())
                .get()
                .build();

        try (Response response = client1.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    return null;
                }
                throw new IOException("Error en la API: " + response.code());
            }

            String responseBody = response.body().string();
            if (responseBody.isEmpty()) {
                return null;
            }

            UserAPIMc user = new UserAPIMc().fromJson(responseBody);

            return user.getId();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
