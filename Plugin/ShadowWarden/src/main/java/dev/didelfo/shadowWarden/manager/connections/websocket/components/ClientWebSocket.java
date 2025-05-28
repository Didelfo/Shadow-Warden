package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.utils.ToolManager;

import javax.crypto.SecretKey;
import java.util.ArrayList;

public class ClientWebSocket {
    // Variables
    private String publicKeyServer = "";
    private String publicKeyMovil = "";
    private byte[] shareKey;
    private SecretKey hmacKey;
    private String subscription;


    public ClientWebSocket(){}

    public ClientWebSocket(String publickeyserver){
        this.publicKeyServer = publickeyserver;
    }

    public String getPublicKeyServer() {
        return publicKeyServer;
    }

    public void setPublicKeyServer(String publicKeyServer) {
        this.publicKeyServer = publicKeyServer;
    }

    public String getPublicKeyMovil() {
        return publicKeyMovil;
    }

    public void setPublicKeyMovil(String publicKeyMovil) {
        this.publicKeyMovil = publicKeyMovil;
    }

    public byte[] getShareKey() {
        return shareKey;
    }

    public void setShareKey(byte[] shareKey) {
        this.shareKey = shareKey;
    }

    public SecretKey getHmacKey() {
        return hmacKey;
    }

    public void setHmacKey(SecretKey hmacKey) {
        this.hmacKey = hmacKey;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}


