package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.utils.ToolManager;

import java.util.ArrayList;

public class ClientWebSocket {
    // Variables
    private String publicKeyServer;
    private String publicKeyMovil;


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
}


