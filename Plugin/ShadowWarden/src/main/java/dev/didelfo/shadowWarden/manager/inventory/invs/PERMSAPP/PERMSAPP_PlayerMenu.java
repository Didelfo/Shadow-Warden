package dev.didelfo.shadowWarden.manager.inventory.invs.PERMSAPP;


import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.componets.User;
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

public class PERMSAPP_PlayerMenu {

    private static ShadowWarden pl;
    private static List<Integer> bottom_bar = Arrays.asList(45,46, 47, 48, 50, 51, 52, 53, 54);

    public PERMSAPP_PlayerMenu(ShadowWarden plugin) {
        this.pl = plugin;
    }

    public static Inventory get() {

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Player Menu");

        // Barra inferior
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        for (int i = 0; i < bottom_bar.size(); i++) {
            inv.setItem(bottom_bar.get(i), item);
        }

        // Ahora mostraremos los jugadores que hay como staff
        // Tiene un maximo de 45 jugadores es muy raro tener tantos staff

        User u = null;

        for (int i = 0; i < 45; i++) {
            u = pl.getInvManager().getListaJugadores().get(i);
            item = u.getHead();
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + u.getName());
            item.setItemMeta(meta);

            inv.setItem(i, item);
        }

        return inv;
    }

    public static void onClick(int slot, Player p) {
        // Nos aseguramos que no se pulsa ningun boton de decoracion
        if (!bottom_bar.contains(slot)) {
            // Establecemos el jugador del slot como jugador seleccionado sobre elq ue se aplicaran los cambios
            pl.getInvManager().setJugadorSeleccionado(pl.getInvManager().getListaJugadores().get(slot));
            pl.getLogger().info("Jugador seleccionado: " + pl.getInvManager().getJugadorSeleccionado().getName());

            // Limpiamos la lista de jugadores par no ocupar memoria
            pl.getInvManager().limpiarTodosUsers();

            // navegamos a otro inventario
        }
    }

}
