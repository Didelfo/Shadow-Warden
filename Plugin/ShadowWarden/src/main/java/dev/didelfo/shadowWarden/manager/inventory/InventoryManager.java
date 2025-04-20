package dev.didelfo.shadowWarden.manager.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffAdd;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private ShadowWarden pluign;
    private Map<String, AllMenus> open_menus = new HashMap<>();


    public InventoryManager(ShadowWarden pl){
        this.pluign = pl;
    }


    public void openInv(Player p, AllMenus menu){
        Inventory inv = null;

        switch (menu){
            case SMS_StaffList -> {
                inv = SMS_StaffList.get();
            }
            case SMS_StaffAdd -> {
                inv = SMS_StaffAdd.get();
            }
            default -> {}
        }

        if (inv!= null) {
            open_menus.put(p.getUniqueId().toString(), menu);
            p.openInventory(inv);
        }

    }

}
