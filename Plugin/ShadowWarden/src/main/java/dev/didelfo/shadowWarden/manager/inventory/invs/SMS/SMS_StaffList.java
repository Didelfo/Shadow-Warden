package dev.didelfo.shadowWarden.manager.inventory.invs.SMS;

import dev.didelfo.shadowWarden.utils.CreateCustomHead;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SMS_StaffList {

    public SMS_StaffList() {}


    public static Inventory get() {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Staff List");

        int[] bottom_bar = {46, 47, 48, 50, 51, 52, 53};

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = 0; i < bottom_bar.length; i++) {
            inv.setItem(bottom_bar[i], item);
        }

        String back_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI4YjhjZjQwNWVhZjYwNmEwMjEwZjAzMDNiMDEzMTc5ZjhmMTJlYWE5NTgyNDEyOWViZWVmOWU0NGI2ODIzMCJ9fX0=";
        String add_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNjNWU5YTYxZGM5OTIzMmQ0NWU2ZjMyNWEyYjMwZGMzMGQ0MTdjYWMyODJlYmU0ZmU3Nzg3YWQ1ODk0MTQxMCJ9fX0=";
        String next_head = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRjZGE2ZTNjNmRjYTdlOWI4YjZiYTNmZWJmNWNkMDkxN2Y5OTdiNjRiMmFlZjE4YzNmNzczNzY1ZTNhNTc5In19fQ==";

        CreateCustomHead head = new CreateCustomHead();

        item = head.createCustomHead(back_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Back");
        item.setItemMeta(meta);

        inv.setItem(45, item);


        item = head.createCustomHead(add_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add");
        item.setItemMeta(meta);

        inv.setItem(49, item);


        item = head.createCustomHead(next_head);
        meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Next");
        item.setItemMeta(meta);

        inv.setItem(53, item);


        return inv;
    }


    public void onClick(int slot){

        switch (slot){
            case 45 -> {}
            case 49 -> {}
            case 53 -> {}
            default -> {}
        }

    }




}
