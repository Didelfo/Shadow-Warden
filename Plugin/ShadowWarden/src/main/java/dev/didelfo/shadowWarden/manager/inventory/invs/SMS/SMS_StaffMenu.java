package dev.didelfo.shadowWarden.manager.inventory.invs.SMS;


import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SMS_StaffMenu {
    public SMS_StaffMenu() {
    }

    public static Inventory get() {
        Inventory inv = Bukkit.createInventory(null, 45, (ChatColor.LIGHT_PURPLE) + "Staff Menu");
        int[] bottom_bar = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        ItemStack item = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for(int i = 0; i < bottom_bar.length; ++i) {
            inv.setItem(bottom_bar[i], item);
        }

        item = new ItemStack(Material.PLAYER_HEAD);
        meta.setDisplayName((ChatColor.LIGHT_PURPLE) + "Staff List");
        item.setItemMeta(meta);
        item = new ItemStack(Material.GOLD_BLOCK);
        meta = item.getItemMeta();
        meta.displayName();
        meta.setLore(List.of(" ", (ChatColor.GOLD) + "CLICK PARA LINKEAR", " "));
        item.setItemMeta(meta);
        inv.setItem(24, item);
        return inv;
    }

    public static void onClick(int slot, ShadowWarden pl, Player p) {
        switch (slot) {
            case 24:
                if (p.hasPermission("shadowwardem.sms.link")) {
                    pl.getInvManager().closeInv(p);
                    generateQR(p);
                } else {
                    pl.getInvManager().closeInv(p);
                    pl.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                }
            case 20:
            case 21:
            case 22:
            case 23:
            default:
        }
    }

    private static void generateQR(Player p) {
        Map<String, Object> datos = new HashMap();
        Server server = Bukkit.getServer();
        datos.put("ip", server.getIp());
        datos.put("port", server.getPort());
    }
}

