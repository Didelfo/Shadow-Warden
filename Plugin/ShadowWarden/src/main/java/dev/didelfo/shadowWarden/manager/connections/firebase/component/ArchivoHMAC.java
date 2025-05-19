package dev.didelfo.shadowWarden.manager.connections.firebase.component;

public class ArchivoHMAC {
    private String hmac;
    private String nonce;

    public ArchivoHMAC(String hmac, String nonce) {
        this.hmac = hmac;
        this.nonce = nonce;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
