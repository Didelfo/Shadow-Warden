package dev.didelfo.shadowWarden.listeners.events.inventory;

import dev.didelfo.shadowWarden.ShadowWarden;
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

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){

    }


}
