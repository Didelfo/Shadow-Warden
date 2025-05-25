package dev.didelfo.shadowWarden.manager.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_HomeMenu;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.PERMSAPP_MenuRoles;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffAdd;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffList;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private ShadowWarden pluign;
    private Map<UUID, AllMenus> open_menus = new HashMap<>();


    public InventoryManager(ShadowWarden pl){
        this.pluign = pl;
    }


    public void openInv(Player p, AllMenus menu){
        Inventory inv = null;

        switch (menu){
            case SMS_StaffList -> {
                inv = SMS_StaffList.get();
            }
            case SMS_StaffMenu -> {
                inv = SMS_StaffMenu.get();
            }

            // Gestion de permisos de usuarios y rol
            case PERMSAPP_HomeMenu -> {
                inv = PERMSAPP_HomeMenu.get();
            }
            case PERMSAPP_MenuRoles -> {
                inv = PERMSAPP_MenuRoles.get();
            }


            default -> {}
        }

        if (inv != null) {
            open_menus.put(p.getUniqueId(), menu);
            p.openInventory(inv);
        }

    }

    public void closeInv(Player p){
        if (open_menus.containsKey(p.getUniqueId())){
            open_menus.remove(p.getUniqueId());
            p.closeInventory();
        }
    }

    public Map<UUID, AllMenus> getOpen_menus() {return open_menus; }
}
