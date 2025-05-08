package dev.didelfo.shadowWarden.security.certificate;

import dev.didelfo.shadowWarden.ShadowWarden;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertificateManager {

    private ShadowWarden plugin;
    private final File keystoreFile;
    private String keystorePassword;
    private final String alias = "shadowwarden";

    public CertificateManager(ShadowWarden pl) throws Exception {

        this.plugin = pl;
        this.keystorePassword = pl.getConfig().getString("websocket.pass");

        File folder = new File(plugin.getDataFolder(), "security");

        if (!folder.exists()) folder.mkdirs();

        this.keystoreFile = new File(folder, "wss_keystore.jks");
        if (!keystoreFile.exists()) {
            generateSelfSignedCertificate();
        }
    }

    private void generateSelfSignedCertificate() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        X509Certificate cert = SelfSignedCertGenerator.generate("CN=ShadowWarden", keyPair, plugin);

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        ks.setKeyEntry(alias, keyPair.getPrivate(), keystorePassword.toCharArray(), new java.security.cert.Certificate[]{cert});

        try (FileOutputStream fos = new FileOutputStream(keystoreFile)) {
            ks.store(fos, keystorePassword.toCharArray());
        }
    }

    public SSLContext createSSLContext() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            ks.load(fis, keystorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keystorePassword.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, new SecureRandom());
        return context;
    }

    public String getCertificateAsString() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystoreFile)) {
            ks.load(fis, keystorePassword.toCharArray());
        }

        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        return Base64.getEncoder().encodeToString(cert.getEncoded());
    }


}
