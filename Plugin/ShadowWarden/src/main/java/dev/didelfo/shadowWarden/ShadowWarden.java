package dev.didelfo.shadowWarden;

import dev.didelfo.shadowWarden.commands.StaffListCommand;
import dev.didelfo.shadowWarden.listeners.events.inventory.InventoryListener;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventChat;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventLogger;
import dev.didelfo.shadowWarden.manager.connections.websocket.WSManager;
import dev.didelfo.shadowWarden.manager.database.ManagerDB;
import dev.didelfo.shadowWarden.manager.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShadowWarden extends JavaPlugin {

    // Manager
    private ManagerDB dbm;
    private InventoryManager invManager;
    private WSManager wsManager;


    @Override
    public void onEnable() {
        // Plugin startup logic

        // Configuracion por defecto
        saveDefaultConfig();

        initializeObjets(this);
        initializeLisener(this);
        initializeCommands(this);
        initializeAutocomplete(this);

        // Secuencia de inicio
        startupSquence();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    // Inicializador de objetos
    private void initializeObjets(ShadowWarden pl){
        // Inicializar manager
        if (!pl.getConfig().getString("dataBase.type").equals("NONE")) {
            this.dbm = new ManagerDB(pl);
        }

        if (pl.getConfig().getBoolean("websocket.enable")) {
            this.wsManager = new WSManager(pl);
        }

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
        //dbm.secuenciaInicioTablas();
    }

    // Getters de objetos utilies
    public ManagerDB getManagerDB() { return dbm;} // Manager BD
    public InventoryManager getInvManager() {return invManager; } // Manager de inventarios
    public WSManager getWsManager() {return  wsManager; } // Manager de WS


}