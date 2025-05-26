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

import java.util.Arrays;
import java.util.List;

public class PERMSAPP_PermissionMenu {

    private static List<Integer> bottom_bar = Arrays.asList(46, 47, 48, 50, 51, 52, 53);

    public PERMSAPP_PermissionMenu() {}

    public static Inventory get() {

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Permission Menu");

        // Barra inferior
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = 0; i < bottom_bar.size(); i++) {
            inv.setItem(bottom_bar.get(i), item);
        }

        String back_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzNGJhMjQxZjg3M2Q1ZWY0YzUyNmViMjkxYjVjMTZkNTA3ZDVhMGM2ZjFhMmU2NTAzZWM1OWIzNjNhMzY3NSJ9fX0=";
        String save_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=";
        String next_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M2MDJkYmE5Zjk3MDFhZTAwMzllNzQ4MWNlYmY2MWM1OGZlOGQzOWQyOWM5MjdiNDg4YmVlNDIyZDlhNjJkNCJ9fX0=";

        CreateCustomHead head = new CreateCustomHead();

        item = head.createCustomHead(back_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "BACK");
        item.setItemMeta(meta);

        inv.setItem(45, item);

        item = head.createCustomHead(save_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "SAVE");
        item.setItemMeta(meta);

        inv.setItem(49, item);

        item = head.createCustomHead(next_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "NEXT");
        item.setItemMeta(meta);

        inv.setItem(53, item);

        // Permisos de BD


        return inv;
    }

    public static void onClick(int slot, ShadowWarden pl, Player p) {
        switch (slot){
            case 45 -> {} // Izquierda
            case 49 -> {} // Centro
            case 53 -> {} // Derecha
            default -> {
                // Nos aseguramos que no se pulsa ningun boton de decoracion
                if(!bottom_bar.contains(slot)){

                }
            }
        }

    }
}
