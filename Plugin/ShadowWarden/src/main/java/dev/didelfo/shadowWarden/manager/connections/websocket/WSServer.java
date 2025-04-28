package dev.didelfo.shadowWarden.manager.connections.websocket;

import dev.didelfo.shadowWarden.ShadowWarden;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArraySet;

public class WSServer extends WebSocketServer {

    private ShadowWarden plugin;
    private final CopyOnWriteArraySet<WebSocket> clients = new CopyOnWriteArraySet<>();

    public WSServer(ShadowWarden pl, int port){
        super(new InetSocketAddress(port));
        this.plugin = pl;
    }


    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
