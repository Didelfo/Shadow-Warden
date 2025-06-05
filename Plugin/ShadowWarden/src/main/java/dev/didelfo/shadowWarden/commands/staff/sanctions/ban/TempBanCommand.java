package dev.didelfo.shadowWarden.commands.staff.sanctions.ban;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.firebase.KeyTemporalFireBase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import dev.didelfo.shadowWarden.models.Sanction;
import dev.didelfo.shadowWarden.models.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TempBanCommand implements CommandExecutor {

    private ShadowWarden plugin;
    private final Map<String, KeyTemporalFireBase> claves = new HashMap<>();

    public TempBanCommand(ShadowWarden pl) {
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)) {
            // Si es un jugador

            Player p = (Player) sender;

            if (!p.hasPermission("shadowwarden.moderation.tempban")) {
                plugin.getMsgManager().showMessageNoPermission(p, MessageType.Staff);
                return true;
            }

            if (args.length == 4) {
                try {
                    // Extraemos los datos en variables para trabajar mas comodos
                    String jugador = args[0];
                    String duracion = args[1];
                    String razon = args[2];
                    String banip = args[3];

                    duracion = duracion.toLowerCase(); // 🔽 Normaliza a minúsculas

                    char letra = duracion.charAt(duracion.length() - 1);
                    String numeroStr = duracion.substring(0, duracion.length() - 1);

                    int numero = Integer.parseInt(numeroStr);
                    // Validamos el numero
                    if (numero > 0) {

                        // Si hemos llegado aqui tenemos la letra y el numero correctos
                        // Asi que primero vamos a calcular el tiempo de cuando experia el ban
                        LocalDateTime now = LocalDateTime.now();

                        // Le sumamos el tiempo
                        switch (letra) {
                            case 's' -> {
                                now = now.plusSeconds(numero);
                            }
                            case 'm' -> {
                                now = now.plusMinutes(numero);
                            }
                            case 'h' -> {
                                now = now.plusHours(numero);
                            }
                            case 'd' -> {
                                now = now.plusDays(numero);
                            }
                            default -> {
                            }
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String expire = now.format(formatter);

                        // Ahora obtenemos la informacion del jugador
                        User user = plugin.getManagerDB().getInfoUser(jugador);

                        String ip = "";
                        if (Boolean.parseBoolean(banip)) {
                            ip = user.getIp();
                        }

                        // Ahora que tenemos todos los datos los guardamos
                        plugin.getManagerDB().addOrUpdateSanction(
                                user.getUuid(),
                                "ban",
                                razon,
                                expire,
                                p.getName(),
                                ip
                        );

                        String tiempoRestante = plugin.getT().getTiempoRestante(expire);

                        Sanction sanInfo = plugin.getManagerDB().getInfoSanction(user.getUuid(), "ban", user.getIp());

                        Player jugadorBan = Bukkit.getPlayer(UUID.fromString(user.getUuid()));
                        if (jugadorBan != null) {
                            jugadorBan.kick(plugin.getMsgManager().showMessageBan(sanInfo));
                        }

                        plugin.getMsgManager().showMessage(p, MessageType.Sancion, "El jugador " + user.getName() +
                                " fue baneado correctamente durante: " + tiempoRestante + ".");


                    } else {
                        plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
                    }

                } catch (Exception e) {
                    plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
                }
            } else {
                plugin.getMsgManager().showMessage(p, MessageType.Sancion, "/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
            }


            return true;
        } else {
            // Si es la consola

            if (args.length == 4) {
                try {
                    // Extraemos los datos en variables para trabajar mas comodos
                    String jugador = args[0];
                    String duracion = args[1].toLowerCase();
                    String razon = args[2];
                    String banip = args[3];

                    char letra = duracion.charAt(duracion.length() - 1);
                    String numeroStr = duracion.substring(0, duracion.length() - 1);

                    int numero = Integer.parseInt(numeroStr);
                    // Validamos el numero
                    if (numero > 0) {

                        // Si hemos llegado aqui tenemos la letra y el numero correctos
                        // Asi que primero vamos a calcular el tiempo de cuando experia el ban
                        LocalDateTime now = LocalDateTime.now();

                        // Le sumamos el tiempo
                        switch (letra) {
                            case 's' -> {
                                now = now.plusSeconds(numero);
                            }
                            case 'm' -> {
                                now = now.plusMinutes(numero);
                            }
                            case 'h' -> {
                                now = now.plusHours(numero);
                            }
                            case 'd' -> {
                                now = now.plusDays(numero);
                            }
                            default -> {
                            }
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String expire = now.format(formatter);

                        // Ahora obtenemos la informacion del jugador
                        User user = plugin.getManagerDB().getInfoUser(jugador);

                        String ip = "";
                        if (Boolean.parseBoolean(banip)) {
                            ip = user.getIp();
                        }

                        // Ahora que tenemos todos los datos los guardamos
                        plugin.getManagerDB().addOrUpdateSanction(
                                user.getUuid(),
                                "ban",
                                razon,
                                expire,
                                "Console",
                                ip
                        );

                        String tiempoRestante = plugin.getT().getTiempoRestante(expire);

                        Sanction sanInfo = plugin.getManagerDB().getInfoSanction(user.getUuid(), "ban", user.getIp());

                        Player jugadorBan = Bukkit.getPlayer(UUID.fromString(user.getUuid()));
                        if (jugadorBan != null) {
                            jugadorBan.kick(plugin.getMsgManager().showMessageBan(sanInfo));
                        }

                        plugin.getLogger().info("El jugador " + user.getName() +
                                " fue baneado correctamente durante: " + tiempoRestante + ".");


                    } else {
                        plugin.getLogger().info("/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
                    }
                } catch (Exception e) {
                    plugin.getLogger().info("/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
                }
            } else {
                plugin.getLogger().info("/tempban <Jugador> <Duracion(2d)> <Razón> <true/false (ban ip)>");
            }


            return true;
        }
    }
}
