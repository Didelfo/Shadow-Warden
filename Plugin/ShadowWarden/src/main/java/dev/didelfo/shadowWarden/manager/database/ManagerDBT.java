package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.connections.websocket.model.ChatMessage;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ManagerDBT {

    private final ShadowWarden plugin;
    private final Logger logger;
    private Connection connection;

    public ManagerDBT(ShadowWarden plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.connection = null;
    }

    // Abre o devuelve una conexión ya existente
    public Connection open()  {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connectDBT();
                createTablesIfNotExists(connection);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Cierra la conexión actual si está abierta
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.warning("Error al cerrar la conexión con la base de datos: " + e.getMessage());
        }
    }

    // Método para conectar (sin validar estado previo)
    private Connection connectDBT() throws SQLException {
        File folder = new File(plugin.getDataFolder(), "logs");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("No se pudo crear la carpeta logs");
        }

        String today = LocalDate.now().toString();
        File dbFile = new File(folder, today + ".db");

        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    // Crea las tablas necesarias
    private void createTablesIfNotExists(Connection conn) throws SQLException {
        createChatTable(conn);
        // Aquí puedes añadir más tablas cuando las necesites
    }

    // Tabla chat
    private void createChatTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS chat (
                hour TEXT,
                uuid TEXT,
                name TEXT,
                message TEXT
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Ejemplo: insertar mensaje de chat usando la conexión abierta
    public void onChat(String uuid, String name, String msg) {
        String sql = "INSERT INTO chat(hour, uuid, name, message) VALUES (?, ?, ?, ?)";
        try (Connection ignored = open(); // Usamos open() y el try cierra automáticamente
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, LocalTime.now().toString());
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);
            pstmt.setString(4, msg);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Error al guardar mensaje de chat: " + e.getMessage());
            throw new RuntimeException("Error al guardar mensaje de chat", e);
        }
    }

    // Obtener últimos 50 mensajes
    public List<ChatMessage> getLast50Messages() {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT hour, uuid, name, message FROM chat ORDER BY hour DESC LIMIT 50";

        try (Connection ignored = open();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                messages.add(new ChatMessage(
                        rs.getString("hour"),
                        rs.getString("uuid"),
                        rs.getString("name"),
                        rs.getString("message")
                ));
            }

        } catch (SQLException e) {
            logger.severe("Error al obtener los mensajes: " + e.getMessage());
            throw new RuntimeException("Error al obtener los mensajes", e);
        }

        return messages; // Más reciente primero, más antiguo al final
    }

    // Opcional: método auxiliar para ejecutar SQL simple
    private void executeSQL(String sql) throws SQLException {
        try (Connection ignored = open();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Getter para la conexión actual (opcional)
    public Connection getConnection() {
        return connection;
    }
}