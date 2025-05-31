package dev.didelfo.shadowWarden.listeners.events.players;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.ChatMessage;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.ClientWebSocket;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.MessageWS;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.StructureMessage;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.security.E2EE.EphemeralKeyStore;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.java_websocket.WebSocket;

import java.time.LocalTime;
import java.util.*;


public class PlayerEventChat implements Listener {

    private ShadowWarden plugin;
    private ToolManager t;
    private List<Player> jugadores = new ArrayList<>();

    public PlayerEventChat(ShadowWarden pl){
        this.plugin = pl;
        this.t = pl.getT();
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){

        Player p = event.getPlayer();
        String msg = event.getMessage();

        // Comprobamos que no este baneado

        // Comprobamos que no este en la lista
        if (!jugadores.contains(p)){

            // Añadimos al jugador a la lista para evitar spam, pero comprobamos que el filtro este activado
            if (plugin.getConfig().getBoolean("spamfilter.enable")) {
                jugadores.add(p);

                // Eliminamos al jugador
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    jugadores.remove(p);
                }, 20L * plugin.getConfig().getInt("spamfilter.time")); // el tiempo se sacara de la configuracion
            }



            // Guardamos el mensaje en la BDT
            onMessage(
                    p.getUniqueId().toString(),
                    p.getName().toString(),
                    msg
            );
        } else {
            // Le mostramos al jugador que no escriba tan rapido
            plugin.getMsgManager().showMessage(p, MessageType.Chat, "Por favor, no escribas tan rapido.");
            // Cancelamos el evento
            event.setCancelled(true);
        }
    }

    private void onMessage(String uuid, String name, String msg){
        plugin.getExecutor().execute(() -> {
            // Lo guardamos en la base de datos
            plugin.getDbmT().onChat(uuid, name, msg);
            plugin.getDbmT().close();

            // Recorremos las conexiones activas y si alguna esta suscrita le mandamos el mensaje

            for (Map.Entry<WebSocket, ClientWebSocket> entry : plugin.getWs().getClients().entrySet()){
                WebSocket con = entry.getKey();
                ClientWebSocket cli = entry.getValue();

                if (cli.getSubscription().equals("chat")){
                    // Le mandamos el mensaje a esa conexión
                    HashMap<String, Object> data = new HashMap<>();
                    ChatMessage ms = new ChatMessage();
                    ms.setHour(LocalTime.now().toString());
                    ms.setUuid(uuid);
                    ms.setName(name);
                    ms.setMessage(msg);

                    data.put("mensaje", ms);
                    StructureMessage msgg = new StructureMessage("", "chat", "MessageSend", "", "", "", data);

                    String mCifrado = t.objectToString(msgg);

                    EphemeralKeyStore.Pair<byte[], byte[]> pair = plugin.getE2ee().encryptAndSign(
                            mCifrado,
                            cli.getShareKey(),
                            cli.getHmacKey()
                    );

                    MessageWS m = new MessageWS(
                            "Communication",
                            t.byteArrayToBase64(pair.first),
                            t.byteArrayToBase64(pair.second)
                    );

                    // Enviamos el mensaje
                    con.send(t.objectToString(m));
                }
            }
        });
    }
}
