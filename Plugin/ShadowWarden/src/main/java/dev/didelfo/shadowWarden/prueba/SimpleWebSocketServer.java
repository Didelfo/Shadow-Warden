package dev.didelfo.shadowWarden.prueba;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class SimpleWebSocketServer extends WebSocketServer {


    private List<WebSocket> connections = new ArrayList<>(); // Lista para mantener las conexiones activas

    public SimpleWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Nueva conexión: " + conn.getRemoteSocketAddress());
        connections.add(conn); // Agregar la conexión a la lista
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Conexión cerrada: " + conn.getRemoteSocketAddress());
        connections.remove(conn); // Eliminar la conexión de la lista
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Mensaje recibido: " + message);
        conn.send("Respuesta del servidor: " + message); // Enviar respuesta al cliente que envió el mensaje

        // Enviar un mensaje a todos los clientes conectados
        for (WebSocket socket : connections) {
            if (socket != conn) { // No enviar el mensaje al mismo cliente que lo envió
                socket.send("El servidor dice: " + message);
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado");
    }



}
