package dev.didelfo.shadowWarden.manager.connections.websocket.model;

public class MessageWS {

    private String type;
    private String data;
    private String signature;


    public MessageWS() {}

    public MessageWS(String type, String data, String signature) {
        this.type = type;
        this.data = data;
        this.signature = signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
