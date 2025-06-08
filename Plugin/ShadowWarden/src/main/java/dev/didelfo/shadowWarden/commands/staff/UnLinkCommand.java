package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.FireBase;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UnLinkCommand implements CommandExecutor {

    private ShadowWarden plugin;

    public UnLinkCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("shadowwarden.staff.unlink")){
            plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
            return true;
        }

        EncryptedDatabase dbe = new EncryptedDatabase(plugin);
        dbe.connect();
        dbe.deleteUser(p.getName());
        dbe.close();

        plugin.getMsgManager().showMessage(p, MessageType.Staff, "Has sido desvinculado");
        return true;
    }
}
