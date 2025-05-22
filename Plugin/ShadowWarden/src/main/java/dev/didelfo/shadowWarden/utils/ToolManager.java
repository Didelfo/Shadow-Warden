package dev.didelfo.shadowWarden.utils;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.websocket.components.ClientWebSocket;
import dev.didelfo.shadowWarden.manager.connections.websocket.components.StructureMessage;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.security.hmac.HmacUtil;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

public class ToolManager {

    private Gson g = new Gson();
    private HmacUtil h = new HmacUtil();

    // -------------- PublicKey -----------------------

    public PublicKey publicKeyBase64ToPublicKey(String base64) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String publicKeyToBase64(PublicKey key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // ------------------------ Base64 ---------------------------

    public String base64ToString(String base64) {
         return new String(Base64.getDecoder().decode(base64));
    }

    public String stringToBase64(String texto){
        return Base64.getEncoder().encodeToString(texto.getBytes());
    }


    // ------- Objetos a String y vicebersa -----------
    public <T> T stringToObject(String s, Class<T> clazz) {
        return new Gson().fromJson(s, clazz);
    }

    public String objectToString(Object o){
        return g.toJson(o);
    }


    // Convierte un String Base64 a ByteArray
    public byte[] base64ToByteArray(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    // Convierte un ByteArray a String Base64
    public String byteArrayToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // -------------- Verificar HMAC facil------------

    public Boolean verificarHmac(StructureMessage m, ClientWebSocket c, ShadowWarden p){
        String hmacServidor = h.generateHmac(
                getToken(m.getUuidMojan(), p),
                c.getShareKey(),
                m.getNonce()
        );

        // Si el hmacServidor no es nulo lo comprobamosk sino devolvemos false directamente
        if (hmacServidor != null){
            return h.verifyHmac(hmacServidor, m.getHmac());
        } else {
            return false;
        }
    }

    // Me lo devuelve con un hmac nonce y uuid del jugador
    public StructureMessage getStructure(String uuid, ShadowWarden p, ClientWebSocket c){
        String token = getToken(uuid, p);
        if (token != null) {
            String nonce = h.generateNonce();

            String hmac = h.generateHmac(token, c.getShareKey(), nonce);


            return new StructureMessage("", "", hmac, nonce, uuid, Collections.emptyMap());
        }
        return new StructureMessage("", "", "", "", "", Collections.emptyMap());
    }

    private String getToken(String uuidMojan, ShadowWarden p){
        EncryptedDatabase dbE = new EncryptedDatabase(p);
        dbE.connect();
        String token = dbE.getToken(uuidMojan);
        dbE.close();

        if (token != null){
            return token;
        } else {
            return null;
        }
    }

}
