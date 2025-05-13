package dev.didelfo.shadowwarden.utils.security.certificate

import java.security.KeyPairGenerator
import java.security.KeyPair
import java.security.KeyStore
import java.security.PublicKey
import javax.crypto.KeyGenerator
import java.security.MessageDigest
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.bitcoinj.crypto.MnemonicCode

class KeyCertificateTemp(private val keyAlias: String) {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    // Método para generar mnemónico
    fun generateTemporaryKeyAndMnemonic(): List<String> {
        val publicKey = generateRSAKeyPairInKeyStore()
        val publicKeyHash = hashPublicKey(publicKey)
        return hashToMnemonic(publicKeyHash)
    }

    private fun generateRSAKeyPairInKeyStore(): PublicKey {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(2048)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setUserAuthenticationRequired(false)
            .build()

        keyPairGenerator.initialize(keyGenParameterSpec)
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()

        return keyPair.public
    }

    private fun getPublicKeyFromKeyStore(): PublicKey {
        val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.PrivateKeyEntry
            ?: throw IllegalStateException("Key not found in KeyStore")
        return entry.certificate.publicKey
    }

    private fun hashPublicKey(publicKey: PublicKey): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(publicKey.encoded)
    }

    private fun hashToMnemonic(hash: ByteArray): List<String> {
        val entropy = hash.copyOfRange(0, 16) // 128 bits para 12 palabras
        return MnemonicCode.INSTANCE.toMnemonic(entropy)
    }

    fun deleteTemporaryKey() {
        keyStore.deleteEntry(keyAlias)
    }
}
