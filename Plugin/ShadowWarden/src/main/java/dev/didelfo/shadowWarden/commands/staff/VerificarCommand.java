package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.FireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VerificarCommand implements CommandExecutor {

    private ShadowWarden plugin;

    public VerificarCommand(ShadowWarden pl){
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

        new FireBase(plugin).verificar(p);


        return true;
    }
}
