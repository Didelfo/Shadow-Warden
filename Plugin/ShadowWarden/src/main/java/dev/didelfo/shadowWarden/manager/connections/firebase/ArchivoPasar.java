package dev.didelfo.shadowWarden.manager.connections.firebase;

import com.google.gson.Gson;

public class ArchivoPasar {
    private String ip;
    private int port;
    private String certificado;


    public ArchivoPasar(String ip, int port, String certificado) {
        this.ip = ip;
        this.port = port;
        this.certificado = certificado;

    }

    public String toJson() throws Exception{
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ArchivoPasar fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ArchivoPasar.class);
    }


}
