package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PermissionCommand implements CommandExecutor {

    private ShadowWarden plugin;

    public PermissionCommand(ShadowWarden pl) {
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        // Si es un jugador Player p = (Player) sender;
        Player p = (Player) sender;

        // Si el jugador no tiene permisos
        if (!p.hasPermission("shadowwarden.staff.permission")) {
            plugin.getMsgManager().showMessageNoPermission(p, MessageType.Permission);
            return true;
        }

        // Logica del comando

        // Si no tiene argumentos
        if (args.length == 0) {
            plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission <User> <Rol>");
            return true;
        }

        // Tendremos que comproboar que tiene 2 argumedntos validos, es decir que la longitud tiene que ser 2
        if (args.length == 2) {

            comprobar(args[0], args[1], p);
            return true;
        } else {
            plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission <User> <Rol>");
            return true;
        }
    }


    private void comprobar(String user, String rol, Player p) {
        plugin.getExecutor().execute(() -> {
            EncryptedDatabase db = new EncryptedDatabase(plugin);
            db.connect();
            List<String> users = db.getAllUser();
            List<String> roles = db.getAllRol();

            if (users.contains(user) && roles.contains(rol)) {

                // Obtenemos el idDel Rol y se lo ponemos al usuario
                int idRol = db.getidRolPorNombre(rol);
                db.actualizarRolUser(user, idRol);
                db.close();

                plugin.getMsgManager().showMessage(p, MessageType.Permission, "Rol de: " + user + " Cambiado a -> " + rol);

            } else {
                db.close();
                plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission <User> <Rol>");
            }
        });
    }


}
