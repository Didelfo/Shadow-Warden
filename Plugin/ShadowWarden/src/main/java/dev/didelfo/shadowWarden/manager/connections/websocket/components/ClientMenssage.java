package dev.didelfo.shadowWarden.manager.connections.websocket.components;

public class ClientMenssage{
    private String keyPubicServer;
    private String keyPublicMovil;
    private String suscripcion;
    private String operation;

    public ClientMenssage(){}

    public ClientMenssage(String keyPubicServer, String keyPublicMovil, String suscripcion, String operation) {
        this.keyPubicServer = keyPubicServer;
        this.keyPublicMovil = keyPublicMovil;
        this.suscripcion = suscripcion;
        this.operation = operation;
    }

    public String getKeyPubicServer() {
        return keyPubicServer;
    }

    public void setKeyPubicServer(String keyPubicServer) {
        this.keyPubicServer = keyPubicServer;
    }

    public String getKeyPublicMovil() {
        return keyPublicMovil;
    }

    public void setKeyPublicMovil(String keyPublicMovil) {
        this.keyPublicMovil = keyPublicMovil;
    }

    public String getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(String suscripcion) {
        this.suscripcion = suscripcion;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
