package dev.didelfo.shadowWarden.security.certificate;


import dev.didelfo.shadowWarden.ShadowWarden;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

public class SelfSignedCertGenerator {

    public static X509Certificate generate(String dn, KeyPair keyPair, ShadowWarden pl) throws Exception {
        long now = System.currentTimeMillis();
        Date from = new Date(now);
        Date to = new Date(now + (365L * 24 * 60 * 60 * 1000)); // 1 año

        // Guardar fecha de expiración en la configuración
        pl.getConfig().set("websocket.expiration", to.toString());
        pl.saveConfig();

        // Generar número de serie aleatorio
        BigInteger sn = new BigInteger(64, new SecureRandom());

        // Crear emisor y sujeto (auto-firmado)
        X500Name issuer = new X500Name(dn);
        X500Name subject = issuer;

        // Convertir PublicKey a SubjectPublicKeyInfo
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(
                keyPair.getPublic().getEncoded());

        // Construir el certificado
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                issuer,
                sn,
                from,
                to,
                subject,
                publicKeyInfo
        );

        // Firmar el certificado
        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        var certHolder = certBuilder.build(signerBuilder.build(keyPair.getPrivate()));

        // Convertir a X509Certificate
        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }
}