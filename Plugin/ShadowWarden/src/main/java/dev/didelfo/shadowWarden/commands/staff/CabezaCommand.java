package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.AllMenus;
import dev.didelfo.shadowWarden.utils.CreateCustomHead;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CabezaCommand implements CommandExecutor {

    private ShadowWarden plugin;

    public CabezaCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        Player p = (Player) sender;

        plugin.getInvManager().openInv(p, AllMenus.PERMSAPP_HomeMenu);

        return true;
    }
}
