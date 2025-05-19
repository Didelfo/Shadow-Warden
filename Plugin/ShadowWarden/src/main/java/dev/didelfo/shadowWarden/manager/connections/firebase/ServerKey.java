package dev.didelfo.shadowWarden.manager.connections.firebase;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class ServerKey {
    private final String keyAlgorithm = "EC";
    private final String curveName = "secp256r1";
    private final String keyAgreementAlgorithm = "ECDH";
    private final String cipherAlgorithm = "AES/GCM/NoPadding";
    private final int gcmTagLength = 128;
    private final int ivLength = 12;
    private final int aesKeyLength = 32;

    private KeyPair keyPair;

    public ServerKey(){}

    //Genera un par de claves EC temporal en memoria
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(curveName);
            keyPairGenerator.initialize(ecSpec);
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    // Obtiene la clave pública para enviar al cliente Android
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    //Genera el secreto compartido usando la clave pública del cliente
    public byte[] generateSharedSecret(PublicKey clientPublicKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(clientPublicKey, true);

        // Tomamos los primeros 32 bytes para AES-256
        byte[] sharedSecret = keyAgreement.generateSecret();
        return Arrays.copyOf(sharedSecret, aesKeyLength);
    }

    // Método de conveniencia para cifrar texto (útil para pruebas)
    public String encryptText(byte[] sharedSecret, String plaintext)
            throws InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[ivLength];
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(sharedSecret, "AES"),
                new GCMParameterSpec(gcmTagLength, iv));

        byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combinar IV + datos cifrados y codificar en Base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);
        outputStream.write(encryptedData);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // Metodo desencriptar
    public String decryptText(byte[] sharedSecret, String encryptedString) throws IOException {
        byte[] encryptedData;

        try {
            encryptedData = Base64.getDecoder().decode(encryptedString);
        } catch (IllegalArgumentException e) {
            throw new IOException("Error al decodificar Base64", e);
        }

        if (encryptedData.length < ivLength) {
            throw new IOException("Datos cifrados incompletos (no contiene IV válido)");
        }

        // Separar IV y datos cifrados
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, ivLength);
        byte[] cipherText = Arrays.copyOfRange(encryptedData, ivLength, encryptedData.length);

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(sharedSecret, "AES"),
                    new GCMParameterSpec(gcmTagLength, iv)
            );

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (GeneralSecurityException e) {
            throw new IOException("Error al desencriptar los datos", e);
        }
    }
}