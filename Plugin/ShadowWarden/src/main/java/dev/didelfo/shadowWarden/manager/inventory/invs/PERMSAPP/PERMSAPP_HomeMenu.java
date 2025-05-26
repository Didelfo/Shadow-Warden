package dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP;


import dev.didelfo.shadowWarden.ShadowWarden;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static dev.didelfo.shadowWarden.manager.inventory.AllMenus.*;

public class PERMSAPP_HomeMenu {

    public PERMSAPP_HomeMenu() {}

    public static Inventory get() {

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Permission Home");

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Miembros Staff");
        meta.setLore(List.of(
                " ",
                ChatColor.WHITE + "Podras establecer roles",
                ChatColor.WHITE + "y permisos adicionales",
                ChatColor.WHITE + "a los jugadores",
                " "
        ));
        item.setItemMeta(meta);

        inv.setItem(2, item);

        item = new ItemStack(Material.COMMAND_BLOCK);
        meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Roles");
        meta.setLore(List.of(
                " ",
                ChatColor.WHITE + "Podras establecer permisos",
                ChatColor.WHITE + "en cada rol y luego",
                ChatColor.WHITE + "asignarlo a los jugadores",
                " "
        ));
        item.setItemMeta(meta);

        inv.setItem(6, item);

        return inv;
    }

    public static void onClick(int slot, ShadowWarden pl, Player p) {
        switch (slot){
            case 2 -> {
                    pl.getInvManager().closeInv(p);
                    pl.getInvManager().cargarJugadores();
                    pl.getInvManager().openInv(p, PERMSAPP_PlayerMenu);

            }
            case 6 -> {
                pl.getInvManager().closeInv(p);
                pl.getInvManager().openInv(p, PERMSAPP_MenuRoles);
            }
            default -> {}
        }

    }
}
