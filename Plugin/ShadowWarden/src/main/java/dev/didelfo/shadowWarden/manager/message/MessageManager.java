package dev.didelfo.shadowWarden.manager.message;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.models.Sanction;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
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

    public void showMessageAPP(Player p, String name, String msg) {
        Component m = Component.text(name + " » ").color(TextColor.color(0xa05de1)).decorate(TextDecoration.BOLD).append(
                Component.text(msg).color(TextColor.color(0xffffff))
        );
        this.audiences.player(p).sendMessage(m);
    }

    public void showMessageNoPermission(Player p, MessageType type) {
        Component m = this.getPrefix(type).append(Component.text("No tienes permiso para ejecutar este comando.").color(TextColor.color(0xffffff)));
        this.audiences.player(p).sendMessage(m);
    }


    public Component showMessageBan(Sanction sanInfo){

        String duracion = "";
        if (sanInfo.getExpire().equals("never")){
            duracion = "PERMANENTE\n";
        } else {
            duracion = plugin.getT().getTiempoRestante(sanInfo.getExpire());
        }

        return Component.text("ESTAS BANEADO\n").color(TextColor.color(0xff3333)).decorate(TextDecoration.BOLD)
                .append(Component.text("\n"))
                .append(Component.text("Termina en: ").color(TextColor.color(0x9370db)).decorate(TextDecoration.BOLD))
                .append(Component.text(duracion + "\n\n").color(TextColor.color(0x33ff33)))
                .append(Component.text("Razón: ").color(TextColor.color(0x9370db)).decorate(TextDecoration.BOLD))
                .append(Component.text(sanInfo.getReason() + "\n\n").color(TextColor.color(0xffffff)))
                .append(Component.text("Baneado por: ").color(TextColor.color(0x9370db)).decorate(TextDecoration.BOLD))
                .append(Component.text(sanInfo.getStaffName() + "\n\n").color(TextColor.color(0x00ffff)))
                .append(Component.text("¿Se trata de un error? Contactanos en discord\n").color(TextColor.color(0xffffff)))
                .append(Component.text("Link Discord").color(TextColor.color(0x00ffff)));

    }

    private Component getPrefix(MessageType type) {
        switch (type) {
            case Staff -> {
                return Component.text()
                        .append(Component.text("STAFF » ").color(TextColor.color(0xff7f50)).decorate(TextDecoration.BOLD))
                        .build();
            }

            //o&#n
            case Permission -> {
                return Component.text()
                        .append(Component.text("P").color(TextColor.color(0xE43A96)).decorate(TextDecoration.BOLD))
                        .append(Component.text("e").color(TextColor.color(0xE2459A)).decorate(TextDecoration.BOLD))
                        .append(Component.text("r").color(TextColor.color(0xE0519E)).decorate(TextDecoration.BOLD))
                        .append(Component.text("m").color(TextColor.color(0xDD5CA2)).decorate(TextDecoration.BOLD))
                        .append(Component.text("i").color(TextColor.color(0xDB68A6)).decorate(TextDecoration.BOLD))
                        .append(Component.text("s").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("s").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("i").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("o").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("n").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .append(Component.text(" » ").color(TextColor.color(0xD973AA)).decorate(TextDecoration.BOLD))
                        .build();
            }

            // Chat
            case Chat -> {
                return Component.text()
                        .append(Component.text("C").color(TextColor.color(0x54DAF4)).decorate(TextDecoration.BOLD))
                        .append(Component.text("H").color(TextColor.color(0x45C4DD)).decorate(TextDecoration.BOLD))
                        .append(Component.text("A").color(TextColor.color(0x37AEC5)).decorate(TextDecoration.BOLD))
                        .append(Component.text("T").color(TextColor.color(0x2898AE)).decorate(TextDecoration.BOLD))
                        .append(Component.text(" » ").color(TextColor.color(0x2898AE)).decorate(TextDecoration.BOLD))
                        .build();
            }

            // Sancion
            case Sancion -> {
                return Component.text()
                        .append(Component.text("S").color(TextColor.color(0x9863E7)).decorate(TextDecoration.BOLD))
                        .append(Component.text("A").color(TextColor.color(0x9154ED)).decorate(TextDecoration.BOLD))
                        .append(Component.text("N").color(TextColor.color(0x8B44F4)).decorate(TextDecoration.BOLD))
                        .append(Component.text("C").color(TextColor.color(0x8435FA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("I").color(TextColor.color(0x8435FA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("Ó").color(TextColor.color(0x8435FA)).decorate(TextDecoration.BOLD))
                        .append(Component.text("N").color(TextColor.color(0x8435FA)).decorate(TextDecoration.BOLD))
                        .append(Component.text(" » ").color(TextColor.color(0x8435FA)).decorate(TextDecoration.BOLD))
                        .build();
            }

            default -> {
                return Component.text("null");
            }
        }
    }


}
