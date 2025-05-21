package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.utils.ToolManager;

import java.util.ArrayList;

public class ClientWebSocket {
    // Variables
    private String publicKeyServer = "";
    private String publicKeyMovil = "";
    private Boolean cifrado = false;


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

    public Boolean getCifrado() {
        return cifrado;
    }

    public void setCifrado(Boolean cifrado) {
        this.cifrado = cifrado;
    }
}


