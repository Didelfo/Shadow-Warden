package dev.didelfo.shadowWarden.commands.staff.sanctions.ban;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.models.Sanction;
import dev.didelfo.shadowWarden.models.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/*
La funcion de esta clase es banear tanto desde el juego como desde consola, cada comando tendra el formato
/sancion <jugador> <duracion (14d)> <reazon> <banip(sera true si se banea la ip o false sino)>
 */


public class BanCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public BanCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)){
            // Si es un jugador

            Player p = (Player) sender;

            if (!p.hasPermission("shadowwarden.moderation.ban")){
                plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                return true;
            }

            if (args.length == 3){
                try {
                    // Obtenemos los datos introducidos
                    String name = args[0];
                    String razon = args[1];
                    Boolean banip = Boolean.parseBoolean(args[2]);

                    User user = plugin.getManagerDB().getInfoUser(name);

                    String ip = "";
                    if (banip){
                        ip = user.getIp();
                    }

                    // Obtenemos la uiid del jugador y la ip en caso de ser necesario
                    plugin.getManagerDB().addOrUpdateSanction(
                            user.getUuid(),
                            "ban",
                            razon,
                            "never",
                            p.getName(),
                            ip
                    );

                    Sanction sanInfo = plugin.getManagerDB().getInfoSanction(user.getUuid(), "ban", ip);

                    Player jugadorBan = Bukkit.getPlayer(UUID.fromString(user.getUuid()));
                    if (jugadorBan != null){
                        jugadorBan.kick(plugin.getMsgManager().showMessageBan(sanInfo));
                    }


                    plugin.getMsgManager().showMessage(p, MessageType.Sancion, "El jugador " + user.getName() + " fue baneado correctamente.");



                } catch (Exception e){
                    // En caso de un error con los datos le mostramos al jugador el formato correcto
                    plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/ban <Jugador> <Razón> <true/false (ban ip)>");
                }

            } else {
                // En caso de un error con los datos le mostramos al jugador el formato correcto
                plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/ban <Jugador> <Razón> <true/false (ban ip)>");
            }

            return true;
        } else {
            // Si es la consola

            if (args.length == 3){
                try {
                    // Obtenemos los datos introducidos
                    String name = args[0];
                    String razon = args[1];
                    Boolean banip = Boolean.parseBoolean(args[2]);

                    User user = plugin.getManagerDB().getInfoUser(name);

                    String ip = "";
                    if (banip){
                        ip = user.getIp();
                    }

                    // Obtenemos la uiid del jugador y la ip en caso de ser necesario
                    plugin.getManagerDB().addOrUpdateSanction(
                            user.getUuid(),
                            "ban",
                            razon,
                            "never",
                            "Console",
                            ip
                    );
                    Sanction sanInfo = plugin.getManagerDB().getInfoSanction(user.getUuid(), "ban", ip);

                    Player jugadorBan = Bukkit.getPlayer(UUID.fromString(user.getUuid()));
                    if (jugadorBan != null){
                        jugadorBan.kick(plugin.getMsgManager().showMessageBan(sanInfo));
                    }

                    plugin.getLogger().info("El jugador " + user.getName() + " fue baneado correctamente.");

                } catch (Exception e){
                    // En caso de un error con los datos le mostramos al jugador el formato correcto
                    plugin.getLogger().info("/ban <Jugador> <Razón> <true/false (ban ip)>");
                }

            } else {
                // En caso de un error con los datos le mostramos al jugador el formato correcto
                plugin.getLogger().info("/ban <Jugador> <Razón> <true/false (ban ip)>");
            }


            return true;
        }
    }
}
