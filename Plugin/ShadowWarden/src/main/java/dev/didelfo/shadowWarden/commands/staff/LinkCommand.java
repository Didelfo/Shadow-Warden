package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.FireBase;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.inventory.AllMenus;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class LinkCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public LinkCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("shadowwardem.staff.link")){
            plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
            return true;
        }

        if (claves.containsKey(p.getUniqueId().toString())){
            new FireBase(plugin).verificacionSelector(p, claves.get(p.getUniqueId().toString()));
        } else {
            KeyTemporalFireBase k = new KeyTemporalFireBase();
            claves.put(p.getUniqueId().toString(), k);
            new FireBase(plugin).verificacionSelector(p, k);

            // Luego de 60 segundos borra las claves
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                claves.remove(p.getUniqueId().toString());
                plugin.getMsgManager().showMessage(p, MessageType.Staff, "Claves caducadas");
            }, 20L * 60); // 60 segundos
        }

        return true;
    }
}
