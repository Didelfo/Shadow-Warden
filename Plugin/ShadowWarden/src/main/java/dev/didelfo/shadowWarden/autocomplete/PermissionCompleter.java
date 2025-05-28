package dev.didelfo.shadowWarden.autocomplete;


import com.google.gson.annotations.Since;
import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.database.EncryptedDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PermissionCompleter implements TabCompleter {

    private ShadowWarden plugin;
    private static List<String> acciones;

    public PermissionCompleter(ShadowWarden pl) {
        this.plugin = pl;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            // Autocompletar acciones
            List<String> acciones = getUser();
            String partial = args[0].toLowerCase();
            return acciones.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            // Autocompletar acciones
            List<String> acciones = getRoles();
            String partial = args[0].toLowerCase();
            return acciones.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }


        /*

        if (args.length == 2 && args[0].equalsIgnoreCase("teleport")) {
            // Autocompletar jugadores conectados
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }


         */
        return Collections.emptyList(); // Sin sugerencias
    }


    private List<String> getUser() {
        EncryptedDatabase db = new EncryptedDatabase(plugin);
        db.connect();
        List<String> users = db.getAllUser();
        db.close();
        return users;
    }

    private List<String> getRoles() {
            EncryptedDatabase db = new EncryptedDatabase(plugin);
            db.connect();
            List<String> rol = db.getAllRol();
            db.close();
            return rol;
    }


}