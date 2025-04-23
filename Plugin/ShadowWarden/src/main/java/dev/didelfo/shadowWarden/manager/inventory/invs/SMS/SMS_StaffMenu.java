package dev.didelfo.shadowWarden.manager.inventory.invs.SMS;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SMS_StaffMenu {

    public SMS_StaffMenu() {}

    public static Inventory get() {

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Staff Menu");

        int[] bottom_bar = {46, 47, 48, 50, 51, 52, 53};

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(" ");
        item.setItemMeta(meta);


        return inv;
    }

    public void onClick(int slot) {

        switch (slot){

            default -> {}
        }

    }
}
