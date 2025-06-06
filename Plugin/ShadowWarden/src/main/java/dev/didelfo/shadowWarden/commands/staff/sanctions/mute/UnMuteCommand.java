package dev.didelfo.shadowWarden.commands.staff.sanctions.mute;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.models.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/*
La funcion de esta clase es banear tanto desde el juego como desde consola, cada comando tendra el formato
/sancion <jugador> <duracion (14d)> <reazon> <banip(sera true si se banea la ip o false sino)>
 */


public class UnMuteCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public UnMuteCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)){
            // Si es un jugador

            Player p = (Player) sender;

            if (!p.hasPermission("shadowwarden.moderation.unmute")){
                plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                return true;
            }

            if (args.length == 1){

                String jugador = args[0];
                User user = plugin.getManagerDB().getInfoUser(jugador);
                plugin.getManagerDB().deleteSancion(user.getUuid(), "mute");

            } else {
                plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/unmute <Jugador>");
            }
            return true;
        } else {
            // Si es la consola

            if (args.length == 1){

                String jugador = args[0];
                User user = plugin.getManagerDB().getInfoUser(jugador);
                plugin.getManagerDB().deleteSancion(user.getUuid(), "mute");

            } else {
                plugin.getLogger().info("/unmute <Jugador>");
            }
            return true;
        }
    }
}
