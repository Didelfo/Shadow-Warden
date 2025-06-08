package dev.didelfo.shadowWarden.autocomplete;


import dev.didelfo.shadowWarden.ShadowWarden;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TempBanCompleter implements TabCompleter {

    private ShadowWarden plugin;

    public TempBanCompleter(ShadowWarden pl) {
        this.plugin = pl;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            List<String> jugadores = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> jugadores.add(player.getName()));
            // Autocompletar acciones
            List<String> acciones = jugadores;
            String partial = args[0];
            return acciones.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            // Autocompletar acciones
            List<String> acciones = Arrays.asList("4h", "8h", "12h", "1d", "2d", "4d");
            return acciones.stream()
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        if (args.length == 4) {
            // Autocompletar acciones
            List<String> acciones = Arrays.asList("true", "false");
            return acciones.stream()
                    .filter(s -> s.startsWith(args[3]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}