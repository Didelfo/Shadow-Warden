package dev.didelfo.shadowWarden.manager.connections.firebase;

import com.google.gson.Gson;

public class UserAPIMc {
    private String name;
    private String id;

    public UserAPIMc(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public UserAPIMc(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public UserAPIMc fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, UserAPIMc.class);
    }

}
