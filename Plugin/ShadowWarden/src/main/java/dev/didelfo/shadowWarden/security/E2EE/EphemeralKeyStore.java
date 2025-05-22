package dev.didelfo.shadowWarden.security.E2EE;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

public class EphemeralKeyStore {
    private KeyPair keyPair;

    // Generar par de claves ECDH
    public void generateKeyPair() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp521r1");
            keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

    }

    // Obtener clave pública
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    // Generar clave compartida
    public byte[] getSharedSecret(PublicKey peerPublicKey) {
        KeyAgreement keyAgreement = null;
        try {
            keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(keyPair.getPrivate());
            keyAgreement.doPhase(peerPublicKey, true);

            // Deriva la clave compartida
            return deriveKey(keyAgreement.generateSecret());

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    // Deriva clave AES
    private byte[] deriveKey(byte[] sharedSecret) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] hash = messageDigest.digest(sharedSecret);
        return Arrays.copyOf(hash, 32); // 256 bits para AES
    }

    // Genera clave HMAC
    public SecretKey getHmacKey(byte[] sharedSecret) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hmacKeyBytes = messageDigest.digest(
                concatenate(sharedSecret, "HMAC".getBytes()));
        hmacKeyBytes = Arrays.copyOf(hmacKeyBytes, 64); // 512 bits para HMAC-SHA512
        return new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
    }

    // Cifrar y firmar
    public Pair<byte[], byte[]> encryptAndSign(String message, byte[] sharedSecret, SecretKey hmacKey) {

        try {
            // Cifrado AES-256-GCM
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            SecretKeySpec keySpec = new SecretKeySpec(sharedSecret, "AES");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
            byte[] cipherText = cipher.doFinal(message.getBytes());

            // IV + cipherText
            byte[] encryptedData = concatenate(iv, cipherText);

            // Firmar con HMAC
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(hmacKey);
            byte[] signature = mac.doFinal(encryptedData);

            return new Pair<>(encryptedData, signature);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            return null;
        }
    }

    // Verificar y descifrar
    public String verifyAndDecrypt(byte[] encryptedData, byte[] signature, byte[] sharedSecret, SecretKey hmacKey) {

        try {
            // Verificar firma
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(hmacKey);
            byte[] calculatedSignature = mac.doFinal(encryptedData);

            if (!MessageDigest.isEqual(calculatedSignature, signature)) {
                throw new SecurityException("HMAC verification failed");
            }

            // Descifrar
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, 12);
            byte[] cipherText = Arrays.copyOfRange(encryptedData, 12, encryptedData.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(sharedSecret, "AES");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            byte[] decryptedBytes = cipher.doFinal(cipherText);

            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    // Método auxiliar para concatenar arrays
    private byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Clase Pair simple para Java
    public static class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}