package dev.didelfo.shadowWarden.listeners.events.players;

import dev.didelfo.shadowWarden.ShadowWarden;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerEventLogger implements Listener {

    private ShadowWarden plugin;

    public PlayerEventLogger(ShadowWarden pl){
        this.plugin = pl;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();

        // Tabla user
        plugin.getManagerDB().onJoinUser(
                p.getUniqueId().toString(),
                p.getName()
        );

        // Tabla ip

        // Tabla time

        // Tabla KDR

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){

    }

    @EventHandler
    public void onPlayerKill(EntityDamageByEntityEvent event){

    }

}
