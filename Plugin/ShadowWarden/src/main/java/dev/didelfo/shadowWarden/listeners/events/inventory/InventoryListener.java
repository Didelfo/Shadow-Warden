package dev.didelfo.shadowWarden.listeners.events.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.AllMenus;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffList;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.SMS_StaffMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    private ShadowWarden plugin;

    public InventoryListener(ShadowWarden pl){
        this.plugin = pl;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player p = (Player)event.getWhoClicked();
        if (this.plugin.getInvManager().getOpen_menus().containsKey(p.getUniqueId()) && (event.getCurrentItem().getType() != Material.BARRIER || event.getCurrentItem() != null)) {

            AllMenus menuAbierto = plugin.getInvManager().getOpen_menus().get(p.getUniqueId());
            int slot = event.getSlot();
            event.setCancelled(true);

            switch (menuAbierto) {
                case SMS_StaffMenu -> SMS_StaffMenu.onClick(slot, this.plugin, p);
//                case 2 -> SMS_StaffList.onClick(slot, this.plugin, p);
            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Player p = (Player)event.getPlayer();
        this.plugin.getInvManager().closeInv(p);
    }


}
