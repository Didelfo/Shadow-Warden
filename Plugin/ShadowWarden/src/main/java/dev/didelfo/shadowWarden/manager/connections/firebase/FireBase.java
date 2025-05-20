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
                    PublicKey keyMovil = t.publicKeyBase64ToPublicKey(obtenerCampo(uuidMojan, "keym"));

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

                    actualizarCampo(uuidMojan, "keys", t.publicKeyToBase64(key.getPublicKey()));

                    // SE ejequtoa hasta aqui
                    actualizarCampo(uuidMojan, "archivo", archivoEncrip);

                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Linkeado con exito. Continua en la app.");

                } catch (Exception e) {
                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Error");
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
                    byte[] shareKey = key.generateSharedSecret(t.publicKeyBase64ToPublicKey(obtenerCampo(uuidMojan, "keym")));

                    // Actualizamos la clave del servidor
                    actualizarCampo(uuidMojan, "keys", t.publicKeyToBase64(key.getPublicKey()));

                    // Obtenemos el token
                    String token = key.decryptText(shareKey, obtenerCampo(uuidMojan, "token"));

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
                        actualizarCampo(uuidMojan, "hmac", hmacEncrip);
                        actualizarCampo(uuidMojan, "nonce", nonce);

                        // Informamos al usuario
                        plugin.getMsgManager().showMessage(p, MessageType.Staff, "Validación exitosa. Continue en la App.");

                    } else {
                        plugin.getMsgManager().showMessage(p, MessageType.Staff, "El token no es valido. Intentelo de nuevo");
                    }

                } catch (Exception e) {
                    plugin.getMsgManager().showMessage(p, MessageType.Staff, "Error");
                    plugin.getLogger().severe(e.getMessage());
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
//        String tokenPlano = t.base64ToString(token);
        if (token.startsWith(uuid)){
            return true;
        } else {
            return false;
        }
    }

    // Comprobar que existe un registro con esta uuid
    private boolean existeUUID(String uuid){
        String url = FIREBASE_URL + uuid + ".json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            return !responseBody.equals("null"); // null sino
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para obtener el valor de un campo específico
    private static String obtenerCampo(String uuid, String campo) {
        String url = FIREBASE_URL + uuid + "/" + campo + ".json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // Firebase devuelve "null" como string si el campo no existe
            if (responseBody == null || responseBody.equals("null")) {
                return null;
            }

            // Elimina las comillas del JSON si las hay
            return responseBody.replaceAll("\"", "").trim();
        } catch (IOException e) {
            throw new RuntimeException("Error al obtener el campo '" + campo + "': " + e.getMessage());
        }
    }

    // Metodo actualizar campos
    private static void actualizarCampo(String uuid, String campo, String valor) {
        String url = FIREBASE_URL + uuid + ".json";  // Nota: Cambiado para apuntar al nodo principal

        // Crear un objeto JSON con el campo a actualizar
        JsonObject update = new JsonObject();
        update.addProperty(campo, valor);

        RequestBody body = RequestBody.create(
                update.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)  // Usa PATCH para actualizar solo el campo especificado
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error al actualizar '" + campo + "': " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar '" + campo + "': " + e.getMessage(), e);
        }
    }


    // Obtener el token
    public static String obtenerToken(String uuid) {
        String url = FIREBASE_URL + uuid + "/token.json";
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // Firebase devuelve "null" como string si el campo no existe
            if (responseBody == null || responseBody.trim().isEmpty() || responseBody.equals("\"\"")) {
                return null;
            }

            // Elimina las comillas del JSON
            return responseBody.replaceAll("\"", "").trim(); // Elimina espacios extra
        } catch (IOException e) {
            throw new RuntimeException("Error al obtener token: " + e.getMessage());
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
