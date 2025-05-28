package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ManagerDBT {

    private final ShadowWarden plugin;

    public ManagerDBT(ShadowWarden plugin) {
        this.plugin = plugin;
    }

    // Método principal: se conecta y asegura las tablas necesarias
    public Connection connect() {
        try {
            Connection conn = connectDBT();
            createTablesIfNotExists(conn);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    // Conecta o crea el archivo de BD del día actual
    private Connection connectDBT() throws SQLException {
        File folder = new File(plugin.getDataFolder(), "logs");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("No se pudo crear la carpeta logs");
        }

        String today = LocalDate.now().toString();
        File dbFile = new File(folder, today + ".db");

        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    }

    // Crea todas las tablas necesarias si no existen
    private void createTablesIfNotExists(Connection conn) throws SQLException {
        createChatTable(conn);
        // Aquí puedes añadir más tablas cuando las necesites
        // createUsersTable(conn);
        // createPermissionsTable(conn);
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

    // Ejemplo: insertar mensaje de chat
    public void onChat(String uuid, String name, String msg) {
        String sql = "INSERT INTO chat(hour, uuid, name, message) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, LocalTime.now().toString());
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);
            pstmt.setString(4, msg);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar mensaje de chat", e);
        }
    }

    // Método auxiliar para ejecutar SQL simple (opcional)
    private void executeSQL(String sql, Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}