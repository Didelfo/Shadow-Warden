package dev.didelfo.shadowWarden.manager.connections.websocket.components;

import java.util.Map;

public class StructureMessage {
    private String id;
    private String category;
    private String action;
    private String hmac;
    private String nonce;
    private String uuidMojan;
    private Map<String, Object> data;

    public StructureMessage(){}

    public StructureMessage(String id, String category, String action, String hmac, String nonce, String uuidMojan, Map<String, Object> data) {
        this.id = id;
        this.category = category;
        this.action = action;
        this.hmac = hmac;
        this.nonce = nonce;
        this.uuidMojan = uuidMojan;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getUuidMojan() {
        return uuidMojan;
    }

    public void setUuidMojan(String uuidMojan) {
        this.uuidMojan = uuidMojan;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
