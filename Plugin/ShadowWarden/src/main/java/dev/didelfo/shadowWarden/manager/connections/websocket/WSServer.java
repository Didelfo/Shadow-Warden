package dev.didelfo.shadowWarden.manager.connections.websocket;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.websocket.components.ClientWebSocket;
import dev.didelfo.shadowWarden.manager.connections.websocket.components.MessageProcessor;
import dev.didelfo.shadowWarden.security.certificate.CertificateManager;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSServer extends WebSocketServer {
    private ToolManager t = new ToolManager();
    private ShadowWarden plugin;
    private final Map<WebSocket, ClientWebSocket> clients = new ConcurrentHashMap<>();
    private MessageProcessor mPro;

    public WSServer(ShadowWarden pl, int port){
        super(new InetSocketAddress(port));
        this.plugin = pl;
        this.mPro = new MessageProcessor(pl);

        try {
            CertificateManager certManager = new CertificateManager(plugin);
            this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(certManager.createSSLContext()));
            plugin.getLogger().info("[Shadow-Warden] -> WebSocket seguro iniciado con WSS.");
        } catch (Exception e) {
            plugin.getLogger().severe("[Shadow-Warden] -> No se pudo iniciar WSS: " + e.getMessage());
        }

    }


    @Override
    public void onOpen(WebSocket con, ClientHandshake clientHandshake) {
        // Cuando se abre la conexion lo guardamos de esta manera
        if (!clients.containsKey(con)) {
            clients.put(con, new ClientWebSocket(t.publicKeyToBase64(plugin.getE2ee().getPublicKey())));
            con.send(clients.get(con).getPublicKeyServer());
        } else {
            clients.remove(con);
            con.close();
        }
    }

    @Override
    public void onClose(WebSocket con, int i, String s, boolean b) {
        plugin.getLogger().info("[Movil] -> Desconectado");
    }

    @Override
    public void onMessage(WebSocket con, String s) {
        ClientWebSocket cli = clients.get(con);
        // Primer mensaje recibido para la primera conexion a cara perro
        if (!cli.getCifrado()){
            cli.setPublicKeyMovil(s);
            cli.setShareKey(plugin.getE2ee().getSharedSecret(t.publicKeyBase64ToPublicKey(s)));
            cli.setHmacKey(plugin.getE2ee().getHmacKey(cli.getShareKey()));
            cli.setCifrado(true);
        } else {
            // Ahora procesaremos todos los mensajes recibidos de esta conexion como mensajes cifrados
            mPro.process(s, con);
        }
    }

    @Override
    public void onError(WebSocket con, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public Map<WebSocket, ClientWebSocket> getClients() {
        return clients;
    }
}
