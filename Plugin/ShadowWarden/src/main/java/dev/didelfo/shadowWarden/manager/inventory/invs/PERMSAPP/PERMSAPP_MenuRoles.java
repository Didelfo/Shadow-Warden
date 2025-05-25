package dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP;


import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.utils.CreateCustomHead;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static dev.didelfo.shadowWarden.manager.inventory.AllMenus.PERMSAPP_MenuRoles;

public class PERMSAPP_MenuRoles {

    public PERMSAPP_MenuRoles() {}

    public static Inventory get() {

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Permission Home");

        ItemStack item = new CreateCustomHead().createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg0MzJhNTc1NmEwNGViZjA2MmQ3MmE2ZjMxYmQ2MmU4ZjRkODJhOTIxMjAzMzZhZTE5NzJmZTE4ZDM4NzBiYSJ9fX0=");
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GREEN + "HELPER");
        meta.setLore(List.of(
                " ",
                ChatColor.RED + "Guardar antes de pasar pagina",
                " "
        ));
        item.setItemMeta(meta);
        inv.setItem(1, item);


        item = new CreateCustomHead().createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U1MGM3MDk3OTk0MzEzZDk0MzIxNDJkYTc2NTFkYzZkZDYzMzU4N2UyZTFkZDlhNTYyYWJiYzc4NzhlZmI2NSJ9fX0=");
        meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "MODERADOR");
        meta.setLore(List.of(
                " ",
                ChatColor.RED + "Guardar antes de pasar pagina",
                " "
        ));
        item.setItemMeta(meta);
        inv.setItem(3, item);


        item = new CreateCustomHead().createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRkMjJkYjhjNmUyMzhmYjhjYzA4MTlkMDJhNjU0MDMyOTdkNjNiNjdjNmM3Y2U2YjQzYmM4MjkxODk4MzdmNCJ9fX0=");
        meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "ADMINISTRADOR");
        meta.setLore(List.of(
                " ",
                ChatColor.RED + "Guardar antes de pasar pagina",
                " "
        ));
        item.setItemMeta(meta);
        inv.setItem(5, item);


        item = new CreateCustomHead().createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU0YzFkZWQ5MjMxOWJkODM1NzNmMGYwMDQxZTczMDMzOGViN2JiNzk5N2ViNzFmZjU4M2MyOTA4MzIzODg4ZSJ9fX0=");
        meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.AQUA + "OWNER");
        meta.setLore(List.of(
                " ",
                ChatColor.RED + "Guardar antes de pasar pagina",
                " "
        ));
        item.setItemMeta(meta);
        inv.setItem(7, item);

        return inv;
    }

    public static void onClick(int slot, ShadowWarden pl, Player p) {
        switch (slot){
            case 1 -> {}
            case 3 -> {}
            case 5 -> {}
            case 7 -> {}
            default -> {}
        }

    }
}
