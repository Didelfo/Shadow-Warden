package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import com.google.gson.Gson;
import dev.didelfo.shadowWarden.utils.ToolManager;

import java.util.ArrayList;

public class ClientWebSocket {
    // Variables
    private String publicKeyServer;
    private String publicKeyMovil;
    private byte[] shareKey;
    private boolean verific;
    private String hmac;
    private ArrayList<String> permission;
    private SybscriptionType suscripcion;
    private OperationsInitial operation;

    public ClientWebSocket(String publickeyserver){
        this.publicKeyServer = publickeyserver;
        this.suscripcion = SybscriptionType.Initial;
        this.operation = OperationsInitial.sendKeyPublicServerToMovil;
    }


    public String getToSend(){
        ClientMenssage m = new ClientMenssage();
        m.setKeyPubicServer(publicKeyServer);
        m.setSuscripcion(suscripcion.name());
        m.setOperation(operation.name());

        return new Gson().toJson(m);
    }

}


