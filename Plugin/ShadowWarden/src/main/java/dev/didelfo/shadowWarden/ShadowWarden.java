package dev.didelfo.shadowWarden;

import dev.didelfo.shadowWarden.autocomplete.PermissionCompleter;
import dev.didelfo.shadowWarden.commands.staff.PermissionCommand;
import dev.didelfo.shadowWarden.commands.staff.LinkCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.WarnCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.ban.BanCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.ban.BanIPCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.ban.TempBanCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.ban.TempBanIPCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.mute.MuteCommand;
import dev.didelfo.shadowWarden.commands.staff.sanctions.mute.TempMuteCommand;
import dev.didelfo.shadowWarden.listeners.events.inventory.InventoryListener;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventChat;
import dev.didelfo.shadowWarden.listeners.events.players.PlayerEventLogger;
import dev.didelfo.shadowWarden.manager.connections.websocket.WSServer;
import dev.didelfo.shadowWarden.manager.database.ManagerDB;
import dev.didelfo.shadowWarden.manager.database.ManagerDBT;
import dev.didelfo.shadowWarden.manager.executor.ExecutorServices;
import dev.didelfo.shadowWarden.manager.inventory.InventoryManager;
import dev.didelfo.shadowWarden.manager.message.MessageManager;
import dev.didelfo.shadowWarden.security.E2EE.EphemeralKeyStore;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShadowWarden extends JavaPlugin {

    // Manager
    private ManagerDB dbm;
    private ManagerDBT dbmT;
    private InventoryManager invManager;
    private WSServer ws;
    private ExecutorServices executor;
    private MessageManager msgManager;
    private ToolManager t;

    // E2EE Websocket
    private EphemeralKeyStore e2ee;

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
        // Inicializadores sin condiciones
        this.dbmT = new ManagerDBT(pl);
        this.invManager = new InventoryManager(pl);
        this.msgManager = new MessageManager(pl);
        this.executor = new ExecutorServices(pl);
        this.t = new ToolManager(pl);


        // Inicializar manager
        if (!pl.getConfig().getString("dataBase.type").equals("NONE")) {
            this.dbm = new ManagerDB(pl);
        }

        if (pl.getConfig().getBoolean("websocket.enable")) {
            this.e2ee = new EphemeralKeyStore();
            this.e2ee.generateKeyPair();
            this.ws = new WSServer(pl, pl.getConfig().getInt("websocket.port"));
            executor.execute(() -> {
                    ws.start();
            });
        }


    }

    // Inicializdor de comandos
    private void initializeCommands(ShadowWarden pl){
        pl.getCommand("link").setExecutor(new LinkCommand(pl));
        pl.getCommand("permission").setExecutor(new PermissionCommand(pl));
        pl.getCommand("ban").setExecutor(new BanCommand(pl));
        pl.getCommand("tempban").setExecutor(new TempBanCommand(pl));
        pl.getCommand("banip").setExecutor(new BanIPCommand(pl));
        pl.getCommand("tempbanip").setExecutor(new TempBanIPCommand(pl));
        pl.getCommand("mute").setExecutor(new MuteCommand(pl));
        pl.getCommand("tempmute").setExecutor(new TempMuteCommand(pl));
        pl.getCommand("warn").setExecutor(new WarnCommand(pl));
    }

    // Inicializador de lisener
    private void initializeLisener(ShadowWarden pl){
        Bukkit.getPluginManager().registerEvents(new PlayerEventLogger(pl), pl);
        Bukkit.getPluginManager().registerEvents(new PlayerEventChat(pl), pl);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(pl), pl);
    }

    // Inicializador de autoCompletado
    private void initializeAutocomplete(ShadowWarden pl){
        pl.getCommand("permission").setTabCompleter(new PermissionCompleter(pl));
    }

    // Secuencia de inicio
    private void startupSquence(){
        //dbm.secuenciaInicioTablas();


    }

    // Getters de objetos utilies
    public ManagerDB getManagerDB() { return dbm;} // Manager BD
    public ManagerDBT getDbmT() {return dbmT;} // Manager DB Temporal
    public InventoryManager getInvManager() {return invManager; } // Manager de inventarios
    public MessageManager getMsgManager() {return  msgManager; } // manager de Mensajes (Colorines);;
    public ExecutorServices getExecutor() {return executor;} // Ejecutor de hilos
    public EphemeralKeyStore getE2ee() {return e2ee; } // La clase encargada del cifrado de los mensajes
    public WSServer getWs() {return ws;} // Obtenemos el wsocket
    public ToolManager getT(){return t;}


}