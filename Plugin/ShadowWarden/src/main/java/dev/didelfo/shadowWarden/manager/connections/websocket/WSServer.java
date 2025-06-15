package dev.didelfo.shadowWarden.manager.connections.websocket;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.ClientWebSocket;
import dev.didelfo.shadowWarden.manager.connections.websocket.components.MessageProcessor;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.MessageWS;
import dev.didelfo.shadowWarden.security.certificate.CertificateManager;
import dev.didelfo.shadowWarden.utils.ToolManager;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSServer extends WebSocketServer {
    private ShadowWarden plugin;
    private ToolManager t;
    private final Map<WebSocket, ClientWebSocket> clients = new ConcurrentHashMap<>();
    private MessageProcessor mPro;

    public WSServer(ShadowWarden pl, int port){
        super(new InetSocketAddress(port));
        this.plugin = pl;
        this.t  = plugin.getT();
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
        // Cuando se abre la conexion creamos un objeto cliente
        clients.put(con, new ClientWebSocket(t.publicKeyToBase64(plugin.getE2ee().getPublicKey())));

        // Creamos un objeto y se lo mandamos con la clave para establecer la conexion segura
        try {
            // Creamos el objeto que contiene la llave
            MessageWS msgClave = new MessageWS(
                    "KeyExchange",
                    clients.get(con).getPublicKeyServer(),
                    ""
            );

            // Pasamos el objeto a cadena de texto y lo enviamos
            con.send(t.objectToString(msgClave));
        } catch (Exception e){
            plugin.getLogger().info(e.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket con, int i, String s, boolean b) {
        closeConection(con);
    }

    @Override
    public void onMessage(WebSocket con, String s) {
        ClientWebSocket cli = clients.get(con);
        // Usamos un try para asegurar que veine en el formato correcto
        try {
            MessageWS msgRecibido = t.stringToObject(s, MessageWS.class);

            switch (msgRecibido.getType()){
                // Si es de este tipo directamente cogemos los datos
                case "KeyExchange" -> {
                    // Establecemos la clave Publica del movil
                    cli.setPublicKeyMovil(msgRecibido.getData());
                    // Guardamos la clave compartida en el objeto
                    cli.setShareKey(plugin.getE2ee().getSharedSecret(t.publicKeyBase64ToPublicKey(cli.getPublicKeyMovil())));
                    // Aprobechamos y tenemos tambien el hmackey
                    cli.setHmacKey(plugin.getE2ee().getHmacKey(cli.getShareKey()));
                }
                // Si es de este tipo procesamo los datos
                case "Communication" -> {
                    mPro.process(msgRecibido, con);
                }
                default -> {}
            }

        } catch (Exception e ){
            plugin.getLogger().info(e.getMessage());
        }


    }

    @Override
    public void onError(WebSocket con, Exception e) {
        closeConection(con);
    }

    @Override
    public void onStart() {

    }

    public void closeConection(WebSocket con){
        con.close();
        clients.remove(con);
    }

    public Map<WebSocket, ClientWebSocket> getClients() {
        return clients;
    }
}
