package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncryptedDatabase {

    private ShadowWarden plugin;
    private Connection connection;
    private final String dbPath;
    private final String encryptionKey;

    public EncryptedDatabase(ShadowWarden pl) {
        this.plugin = pl;
        this.dbPath = new File(pl.getDataFolder(), "security").getAbsolutePath() + "/token.db";
        this.encryptionKey = pl.getConfig().getString("passMaestral");
    }

    // Abrir conexión con la base de datos cifrada
    public void connect() throws SQLException {
        try {
            // Cargar driver SQLCipher
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC de SQLCipher", e);
        }

        String url = String.format("jdbc:sqlite:%s", dbPath);

        connection = DriverManager.getConnection(url);
        try (Statement stmt = connection.createStatement()) {
            // Establecer clave de cifrado
            stmt.execute("PRAGMA key = '" + encryptionKey + "'");

            // Crear tabla si no existe
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS token (
                    uuidmojan TEXT UNIQUE,
                    uuidServer TEXT UNIQUE,
                    name TEXT,
                    token TEXT
                );
            """);
        }
    }

    // Insertar un nuevo token
    public void insertToken(String uuidMojan, Player p, String token) throws SQLException {
        String sql = "INSERT INTO token(uuidmojan, uuidServer, name, token) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuidMojan);
            pstmt.setString(2, p.getUniqueId().toString());
            pstmt.setString(3, p.getName());
            pstmt.setString(4, token);
            pstmt.executeUpdate();
        }
    }

    // Obtener todos los tokens
    public List<String> getAllTokens() throws SQLException {
        List<String> tokens = new ArrayList<>();
        String sql = "SELECT name FROM token";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tokens.add(rs.getString("name"));
            }
        }

        return tokens;
    }

    public String getToken(String uuid) {
        String sql = "SELECT token FROM token WHERE uuidmojan = " + uuid;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("token");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // Eliminar token por nombre
    public void deleteTokenByName(String nombre) throws SQLException {
        String sql = "DELETE FROM token WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
        }
    }

    // Eliminar todos los tokens
    public void deleteAllTokens() throws SQLException {
        String sql = "DELETE FROM token";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // Cerrar conexión
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}