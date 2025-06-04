package dev.didelfo.shadowWarden.commands.staff.sanctions;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WarnCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public WarnCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)){
            // Si es un jugador

            Player p = (Player) sender;

            if (!p.hasPermission("shadowwarden.staff.warn")){
                plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                return true;
            }


            return true;
        } else {
            // Si es la consola


            return true;
        }
    }
}
