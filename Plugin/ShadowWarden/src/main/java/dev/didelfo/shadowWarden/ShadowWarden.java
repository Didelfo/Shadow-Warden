package dev.didelfo.shadowWarden;

import dev.didelfo.shadowWarden.commands.StaffListCommand;
import dev.didelfo.shadowWarden.listeners.events.inventory.InventoryListener;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventChat;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventLogger;
import dev.didelfo.shadowWarden.manager.db.ManagerDB;
import dev.didelfo.shadowWarden.manager.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShadowWarden extends JavaPlugin {

    private ManagerDB dbm;
    private InventoryManager invManager;


    @Override
    public void onEnable() {
        // Plugin startup logic

        // Configuracion por defecto
        saveDefaultConfig();

        initializeObjets(this);
        initializeLisener(this);
        initializeCommands(this);
        initializeAutocomplete(this);

        // SEcuencia de inicio
        startupSquence();



        /*
        String host = "localhost";
        int port = 8887;

        WebSocketServer server = new SimpleWebSocketServer(new InetSocketAddress(host, port));
        server.start();
        System.out.println("Servidor WebSocket escuchando en ws://" + host + ":" + port);
        */

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    // Inicializador de objetos
    private void initializeObjets(ShadowWarden pl){
        this.dbm = new ManagerDB(pl);
        this.invManager = new InventoryManager(pl);
    }

    // Inicializdor de comandos
    private void initializeCommands(ShadowWarden pl){
        pl.getCommand("stafflist").setExecutor(new StaffListCommand(pl));
    }

    // Inicializador de lisener
    private void initializeLisener(ShadowWarden pl){
        Bukkit.getPluginManager().registerEvents(new PlayerEventLogger(pl), pl);
        Bukkit.getPluginManager().registerEvents(new PlayerEventChat(pl), pl);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(pl), pl);
    }

    // Inicializador de autoCompletado
    private void initializeAutocomplete(ShadowWarden pl){

    }

    // Secuencia de inicio
    private void startupSquence(){
        dbm.secuenciaInicioTablas();
    }

    // Getters de objetos utilies
    public ManagerDB getManagerDB() { return dbm;} // Gestor DB
    public InventoryManager getInvManager() {return invManager; } // Manager de inventarios


}