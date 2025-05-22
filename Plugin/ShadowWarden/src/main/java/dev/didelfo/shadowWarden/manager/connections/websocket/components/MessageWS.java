package dev.didelfo.shadowWarden.manager.connections.websocket.components;

public class MessageWS {

    private String data;
    private String signature;
    private String id;

    public MessageWS(String data, String signature, String id) {
        this.data = data;
        this.signature = signature;
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
