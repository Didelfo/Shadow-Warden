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

        String message = event.getMessage();
        Player p = event.getPlayer();



    }


}
