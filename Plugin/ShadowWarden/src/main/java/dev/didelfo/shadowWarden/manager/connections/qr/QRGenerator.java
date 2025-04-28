package dev.didelfo.shadowWarden.manager.connections.qr;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class QRGenerator {

    public QRGenerator() {
    }

    public static void giveQRMap(Player player, Map<String, Object> jsonData) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonData);
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta)mapItem.getItemMeta();
        MapView mapView = Bukkit.createMap(player.getWorld());

        for(MapRenderer renderer : mapView.getRenderers()) {
            mapView.removeRenderer(renderer);
        }

        mapView.addRenderer(new QRMapRender(jsonString));
        mapMeta.setMapView(mapView);
        mapMeta.setDisplayName(String.valueOf(ChatColor.LIGHT_PURPLE) + "Escaneame");
        mapMeta.setLore(List.of(" ", String.valueOf(ChatColor.GOLD) + "Click Izquierdo -> Cerrar", String.valueOf(ChatColor.GOLD) + "Click Derecho -> Cerrar"));
        mapItem.setItemMeta(mapMeta);
        player.getInventory().addItem(new ItemStack[]{mapItem});
    }

}
