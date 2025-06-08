package dev.didelfo.shadowWarden.listeners.events.players;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.models.Sanction;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;


public class PlayerEventLogger implements Listener {

    private ShadowWarden plugin;

    public PlayerEventLogger(ShadowWarden pl){
        this.plugin = pl;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        Player p = event.getPlayer();
        InetSocketAddress address = p.getAddress();

        String ip = "";

        if (address != null) {
            ip = address.getAddress().getHostAddress();
        }



        // Guardamos los datos de este jugador en nuestra BD
        plugin.getManagerDB().addOrUpdateUser(
                p.getUniqueId().toString(),
                p.getName(),
                ip
        );

        // Verificamos Si esta baneado normal
        if (
                plugin.getManagerDB().isPlayerSancionado(p.getUniqueId().toString(), "ban", ip)
        ){

            Sanction sanInfo = plugin.getManagerDB().getInfoSanction(p.getUniqueId().toString(), "ban", ip);
            // Si el jugado resta baneado preparamos un mensaje para la desconexion

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, plugin.getMsgManager().showMessageBan(sanInfo));
        }
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
