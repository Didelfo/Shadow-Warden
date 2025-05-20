package dev.didelfo.shadowWarden.security.hmac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;


public class HmacUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int NONCE_BYTE_SIZE = 16; // 128 bits

    public HmacUtil() {}

    //Calcula SHA-256 del token decodificado desde Base64
    private byte[] getTokenHash(String tokenBase64) throws Exception {
        byte[] tokenBytes = Base64.getDecoder().decode(tokenBase64);
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(tokenBytes);
    }

    // Genera una firma HMAC-SHA256 de: tokenHash + nonce
    public String generateHmac(String tokenBase64, byte[] secretKeyBytes, String nonce) throws Exception {
        byte[] tokenHash = getTokenHash(tokenBase64);

        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, HMAC_ALGORITHM);
        mac.init(keySpec);

        mac.update(tokenHash);
        mac.update(nonce.getBytes(StandardCharsets.UTF_8));

        byte[] hmacBytes = mac.doFinal();
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

     // Verifica si dos firmas HMAC coinciden
     public boolean verifyHmac(String calculatedSignature, String receivedSignature) {
        return calculatedSignature.equals(receivedSignature);
    }

     //Genera un nonce aleatorio de tamaño fijo (NONCE_BYTE_SIZE bytes)
    public String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[NONCE_BYTE_SIZE];
        random.nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }
}