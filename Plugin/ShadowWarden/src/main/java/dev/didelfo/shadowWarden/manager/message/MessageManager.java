package dev.didelfo.shadowWarden.manager.message;

import dev.didelfo.shadowWarden.ShadowWarden;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class MessageManager {

    private ShadowWarden plugin;
    private BukkitAudiences audiences;

    public MessageManager(ShadowWarden pl) {
        this.plugin = pl;
        this.audiences = BukkitAudiences.create(pl);
    }

    public void showMessage(Player p, MessageType type, String msg) {
        Component m = this.getPrefix(type).append(Component.text(msg).color(TextColor.color(0xffffff)));
        this.audiences.player(p).sendMessage(m);
    }

    public void showMessageNoPermission(Player p, MessageType type) {
        Component m = this.getPrefix(type).append(Component.text("No tienes permiso para ejecutar este comando.").color(TextColor.color(0xffffff)));
        this.audiences.player(p).sendMessage(m);
    }

    private Component getPrefix(MessageType type) {
        switch (type) {
            case Staff -> {
                return Component.text()
                        .append(Component.text("STAFF >> ").color(TextColor.color(0xff7f50)).decorate(TextDecoration.BOLD))
                        .build();
            }
            default -> {
                return Component.text("null");
            }
        }
    }


}
