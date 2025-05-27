package dev.didelfo.shadowWarden.commands.staff;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import dev.didelfo.shadowWarden.manager.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PermissionCommand implements CommandExecutor {

    private ShadowWarden plugin;

    public PermissionCommand(ShadowWarden pl){
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)){
            return true;
        }

        // Si es un jugador Player p = (Player) sender;
        Player p = (Player) sender;

        // Si el jugador no tiene permisos
        if (!p.hasPermission("shadowwarden.staff.permission")){
            plugin.getMsgManager().showMessageNoPermission(p, MessageType.Permission);
            return true;
        }

        // Logica del comando

        // Si no tiene argumentos
        if (args.length == 0){
            plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission <Accion> <opciones>");
            return true;
        }

        // Primer argumento
        switch (args[0].toLowerCase()){
            case "createrol" -> {
                if (args.length > 1){
                    crearRol(args[1].toLowerCase(), p);
                    return true;
                } else {
                    plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission createrol <nombre del rol>");
                    return true;
                }
            }
            case "deleterol" -> {
                if (args.length > 1){
                    deleteRol(args[1].toLowerCase(), p);
                    return true;
                } else {
                    plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission deleterol <nombre del rol>");
                    return true;
                }
            }
            case "rol" -> {
                if (args.length > 1){
                    if (args.length > 2){
                        switch (args[2].toLowerCase()){
                            case "add" -> {
                                if (args.length > 3) {

                                    //args[2]

                                } else {
                                    plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission rol <add/delete> <permission>");
                                    return true;
                                }
                            }
                            case "delete" -> {
                                if (args.length > 3) {

                                    //args[2]

                                } else {
                                    plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission rol <add/delete> <permission>");
                                    return true;
                                }
                            }
                        }
                        return true;
                    } else {
                        plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission rol <add/delete> <permission>");
                        return true;
                    }
                } else {
                    plugin.getMsgManager().showMessage(p, MessageType.Permission, "/permission rol <add/delete> <permission>");
                    return true;
                }
            }
            case "user" -> {}
            default -> {}
        }




        return true;
    }

    // Funciones auxiliares para mantener la logica del comando limpia

    private void crearRol(String nombreRol, Player p){
        plugin.getExecutor().execute(()-> {
            EncryptedDatabase db = new EncryptedDatabase(plugin);
            db.connect();
            db.insertRol(nombreRol);
            db.close();
            plugin.getMsgManager().showMessage(p, MessageType.Permission, "Rol " + nombreRol + " creado con exito.");
        });
    }

    public void deleteRol(String nombreRol, Player p){
        plugin.getExecutor().execute(()-> {
            EncryptedDatabase db = new EncryptedDatabase(plugin);
            db.connect();
            // Obtenemos el id del rol a borrar
            int idRol = db.getidRolPorNombre(nombreRol);
            // Le ponemos a los usuarios un rol 0
            db.actualizarIdRol(idRol, 0);
            // Borramos el rol
            db.deleteRol(idRol);

            plugin.getMsgManager().showMessage(p, MessageType.Permission, "Rol " + nombreRol + " borrado con exito.");
        });
    }






}
