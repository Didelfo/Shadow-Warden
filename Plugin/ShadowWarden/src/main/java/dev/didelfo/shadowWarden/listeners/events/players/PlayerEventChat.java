package dev.didelfo.shadowWarden.listeners.events.players;

import dev.didelfo.shadowWarden.ShadowWarden;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class PlayerEventChat implements Listener {

    private ShadowWarden plugin;

    public PlayerEventChat(ShadowWarden pl){
        this.plugin = pl;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){

        Player p = event.getPlayer();
        String msg = event.getMessage();

        // Guarda el mensaje en la BD
        onMessage(
                p.getUniqueId().toString(),
                p.getName().toString(),
                msg
        );

    }

    private void onMessage(String uuid, String name, String msg){
        plugin.getExecutor().execute(() -> {
            plugin.getDbmT().open();
            plugin.getDbmT().onChat(uuid, name, msg);
            plugin.getDbmT().close();
        });
    }

}
