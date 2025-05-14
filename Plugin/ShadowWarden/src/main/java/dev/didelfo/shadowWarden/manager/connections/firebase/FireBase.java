package dev.didelfo.shadowWarden.manager.connections.firebase;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Player;

import java.io.IOException;

public class FireBase {

    private static final String FIREBASE_URL = "https://shadowwarden-e9645-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final OkHttpClient client = new OkHttpClient();
    private ShadowWarden plugin;

    public FireBase(ShadowWarden pl){
        this.plugin = pl;
    }


// ==============================
//        Metodo Principal
// ==============================

    public void link(String uuid, Player p){

        plugin.getMsgManager().showMessage(p, MessageType.Staff, "Existe: " + existeUUID(uuidLimpia(uuid)));;

    }

// ==============================
//        Metodos consultas
// ==============================

    private static boolean existeUUID(String uuid){
        String url = FIREBASE_URL + uuid + ".json";
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            return !responseBody.equals("null"); // Si no existe, Firebase devuelve "null"
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



// ==============================
//        Metodos complementarios
// ==============================


    private String uuidLimpia(String uuid){
        return uuid.replaceAll("-", "");
    }




}
