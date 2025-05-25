package dev.didelfo.shadowWarden.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class CreateCustomHead {

    public CreateCustomHead() {
    }

    public ItemStack createCustomHead(String base64Texture) {
        // Crear el ItemStack básico
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Crear un perfil de jugador con la textura
        PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", base64Texture));

        // Aplicar el perfil a la cabeza
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public ItemStack givePlayerHead(Player p) {
        // Crear la cabeza del jugador
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Establecer el dueño de la cabeza (funciona para jugadores online)
        meta.setOwningPlayer(p.getServer().getOfflinePlayer(p.getUniqueId()));
        meta.setDisplayName("Cabeza de " + p.getName());

        head.setItemMeta(meta);

        // Devolvemos el item
        return head;
    }


}