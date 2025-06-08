package dev.didelfo.shadowWarden.commands.staff.sanctions.mute;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.models.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MuteCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public MuteCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)){
            // Si es un jugador

            Player p = (Player) sender;

            if (!p.hasPermission("shadowwarden.moderation.mute")){
                plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                return true;
            }

            if (args.length == 2){
                String jugador = args[0];
                String razon = args[1];

                User user = plugin.getManagerDB().getInfoUser(jugador);
                plugin.getManagerDB().addOrUpdateSanction(user.getUuid(), "mute", razon, "never", p.getName(), "");

                Player jugadord = Bukkit.getPlayer(user.getName());
                if (jugadord != null){
                    plugin.getMsgManager().showMessage(jugadord, MessageType.Sancion, "Has sido muteado permanentemente. ¿Se trata de un error? Contactanos en Discord");
                }

                plugin.getMsgManager().showMessage(p, MessageType.Sancion, "El jugador " + user.getName() + " ha sido muteado permanentemente");

            } else {
                plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/mute <Jugador> <Razón>");
            }


            return true;
        } else {
            // Si es la consola

            if (args.length == 2){
                String jugador = args[0];
                String razon = args[1];

                User user = plugin.getManagerDB().getInfoUser(jugador);
                plugin.getManagerDB().addOrUpdateSanction(user.getUuid(), "mute", razon, "never", "Console", "");

                Player jugadord = Bukkit.getPlayer(user.getName());
                if (jugadord != null){
                    plugin.getMsgManager().showMessage(jugadord, MessageType.Sancion, "Has sido muteado permanentemente. ¿Se trata de un error? Contactanos en Discord");
                }

                plugin.getLogger().info("El jugador " + user.getName() + " ha sido muteado permanentemente");

            } else {
                plugin.getLogger().info("/mute <Jugador> <Razón>");
            }


            return true;
        }
    }
}
