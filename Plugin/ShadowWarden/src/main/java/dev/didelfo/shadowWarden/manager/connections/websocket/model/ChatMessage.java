package dev.didelfo.shadowWarden.manager.connections.websocket.model;

public class ChatMessage {
    private String hour;
    private String uuid;
    private String name;
    private String message;

    public ChatMessage() {}

    public ChatMessage(String hour, String uuid, String name, String message) {
        this.hour = hour;
        this.uuid = uuid;
        this.name = name;
        this.message = message;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}