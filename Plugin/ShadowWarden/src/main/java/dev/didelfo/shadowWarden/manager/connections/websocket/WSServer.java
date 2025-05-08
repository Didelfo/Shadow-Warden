package dev.didelfo.shadowWarden.manager.connections.websocket;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.security.certificate.CertificateManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArraySet;

public class WSServer extends WebSocketServer {

    private ShadowWarden plugin;
    private final CopyOnWriteArraySet<WebSocket> clients = new CopyOnWriteArraySet<>();

    public WSServer(ShadowWarden pl, int port){
        super(new InetSocketAddress(port));
        this.plugin = pl;

        try {
            CertificateManager certManager = new CertificateManager(plugin);
            this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(certManager.createSSLContext()));
            plugin.getLogger().info("[Shadow-Warden] -> WebSocket seguro iniciado con WSS.");
        } catch (Exception e) {
            plugin.getLogger().severe("[Shadow-Warden] -> No se pudo iniciar WSS: " + e.getMessage());
        }

    }


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        plugin.getLogger().info("[Movil] -> Conectado");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        plugin.getLogger().info("[Movil] -> Desconectado");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage("[Movil] -> " + s);
            });
        });


    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
