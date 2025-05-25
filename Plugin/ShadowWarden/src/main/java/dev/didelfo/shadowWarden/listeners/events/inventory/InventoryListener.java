package dev.didelfo.shadowWarden.listeners.events.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.AllMenus;
import dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP.*;
import dev.didelfo.shadowWarden.manager.inventory.invs.SMS.*;
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player p = (Player) event.getWhoClicked();

        // Verificar si el jugador tiene un menú abierto
        if (this.plugin.getInvManager().getOpen_menus().containsKey(p.getUniqueId())) {
            // Cancelar el evento primero para evitar cualquier interacción no deseada
            event.setCancelled(true);

            // Verificar si hay un item en el slot clickeado
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            AllMenus menuAbierto = plugin.getInvManager().getOpen_menus().get(p.getUniqueId());
            int slot = event.getSlot();

            switch (menuAbierto) {
                // Menus staff
                case SMS_StaffMenu -> SMS_StaffMenu.onClick(slot, this.plugin, p);
                // Menus Administracion permisos
                case PERMSAPP_HomeMenu -> PERMSAPP_HomeMenu.onClick(slot, this.plugin, p);
                case PERMSAPP_MenuRoles -> PERMSAPP_MenuRoles.onClick(slot, this.plugin, p);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Player p = (Player)event.getPlayer();
        this.plugin.getInvManager().closeInv(p);
    }


}
