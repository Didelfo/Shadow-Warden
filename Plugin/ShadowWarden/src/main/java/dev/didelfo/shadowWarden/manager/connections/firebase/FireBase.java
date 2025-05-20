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
import dev.didelfo.shadowWarden.utils.ToolManager;
import okhttp3.*;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FireBase {

    private static final String FIREBASE_URL = "https://shadowwarden-e9645-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final OkHttpClient client = new OkHttpClient();
    private ShadowWarden plugin;
    private ToolManager t = new ToolManager();
    private HmacUtil hmacTool = new HmacUtil();

    public FireBase(ShadowWarden pl){
        this.plugin = pl;
    }

// ==============================
//        Metodo Principal
// ==============================

    public void verificacionSelector(Player p){
        String uuidMojan = uuidMojan(p);

        // Reutilizamos el comando /link parara cuadno no hay token usar una funcion
        // y ucando si hay token usar otra

        if (obtenerToken(uuidMojan) == null) {
            link(p, uuidMojan);
        } else {
            verificar(p, uuidMojan);
        }
    }


    private void link(Player p, String uuidMojan){
        plugin.getExecutor().execute(() -> {

            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Comenzando el linkeo....");

            // Comprobamos que existe el registro y alguien con esta uuid esta intentando vincular este servidor
            if (existeUUID(uuidMojan)){

                // Ahora que tenemos seguro que existe generamos un par de llaves aqui.
                KeyTemporalFireBase key = new KeyTemporalFireBase();
                key.generateKeyPair();

                try {
                    // Desciframos la llave
                    PublicKey keyMovil = t.publicKeyBase64ToPublicKey(obtenerKeym(uuidMojan));

                    // Obtenemos la clave compartida
                    byte[] shareKey = key.generateSharedSecret(keyMovil);

                    // Generamos el archivo que encriptaremos
                    ArchivoPasar archivo = new ArchivoPasar(
                            plugin.getConfig().getString("websocket.ip"),
                            plugin.getConfig().getInt("websocket.port"),
                            new CertificateManager(plugin).getCertificateAsString()
                    );

                    String archivotxt = archivo.toJson();

                    String archivoEncrip = key.encryptText(shareKey, archivotxt);

                    String keys = t.publicKeyToBase64(key.getPublicKey());

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
    private void verificar(Player p, String uuidMojan){
        plugin.getExecutor().execute(() -> {

            plugin.getMsgManager().showMessage(p, MessageType.Staff, "Comenzando la verificación....");

            // Comprobamos que existe el registro y alguien con esta uuid esta intentando vincular este servidor
            if (existeUUID(uuidMojan)){
                // Ahora que tenemos seguro que existe generamos un par de llaves aqui.
                KeyTemporalFireBase key = new KeyTemporalFireBase();
                key.generateKeyPair();

                try {
                    // Obtenemos la clave compartida
                    byte[] shareKey = key.generateSharedSecret(t.publicKeyBase64ToPublicKey(obtenerKeym(uuidMojan)));

                    // Actualizamos la clave del servidor
                    actualizarKeys(uuidMojan, t.publicKeyToBase64(key.getPublicKey()));

                    // Obtenemos el token
                    String token = key.decryptText(shareKey, obtenerToken(uuidMojan));

                    // Vamos a verificar si el token es correcto usando la uuid
                    if (comprobarToken(token, uuidMojan)){

                        // Guardamos el Token de manera segura
                        EncryptedDatabase dbEn = new EncryptedDatabase(plugin);
                        dbEn.connect();
                        dbEn.insertToken(uuidMojan, p, token);
                        dbEn.close();

                        // Si el token es correcto procedemos a generar nuestro HMAC
                        String nonce = hmacTool.generateNonce();
                        String hmac = hmacTool.generateHmac(token, shareKey, nonce);
                        String hmacEncrip = key.encryptText(shareKey, hmac);

                        // Actualizamos el hmac y none en la base de datos
                        actualizarHmacYNonce(uuidMojan, hmacEncrip, nonce);

                        // Informamos al usuario
                        plugin.getMsgManager().showMessage(p, MessageType.Staff, "Validación exitosa. Continue en la App.");

                    } else {
                        plugin.getMsgManager().showMessage(p, MessageType.Staff, "El token no es valido. Intentelo de nuevo");
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

    // Metodo para comprobar si el token es validdo
    private boolean comprobarToken(String token, String uuid){
        String tokenPlano = t.base64ToString(token);
        if (tokenPlano.startsWith(uuid)){
            return true;
        } else {
            return false;
        }
    }


    // Actualiza los campos hmac y nonce de un usuario en Firebase.
    public static void actualizarHmacYNonce(String uuid, String hmac, String nonce) {
        String url = FIREBASE_URL + uuid + ".json";

        // Crear el objeto JSON con las actualizaciones
        JsonObject updates = new JsonObject();
        updates.addProperty("hmac", hmac);
        updates.addProperty("nonce", nonce);

        RequestBody body = RequestBody.create(
                updates.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)  // Usamos PATCH para actualizar solo estos campos
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al actualizar: " + response.body().string());
            }
            System.out.println("HMAC y Nonce actualizados correctamente");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage());
        }
    }

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

    // Actualizar Keys
    public static void actualizarKeys(String uuid, String keys) {
        String url = FIREBASE_URL + uuid + "/keys.json";

        // Crear el cuerpo de la petición (solo actualiza 'keys')
        JsonObject update = new JsonObject();
        update.addProperty("keys", keys);

        RequestBody body = RequestBody.create(
                update.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)  // Usa PATCH para no sobrescribir otros campos
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al actualizar 'keys': " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage());
        }
    }

    // Obtener el token
    public static String obtenerToken(String uuid) {
        String url = FIREBASE_URL + uuid + "/token.json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // Firebase devuelve "null" como string si el campo no existe
            if (responseBody.equals("null") || responseBody.trim().isEmpty()) {
                return null;  // Token vacío o inexistente
            }

            // Elimina comillas JSON si las hay
            return responseBody.replaceAll("\"", "");
        } catch (IOException e) {
            throw new RuntimeException("Error al obtener token: " + e.getMessage());
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
