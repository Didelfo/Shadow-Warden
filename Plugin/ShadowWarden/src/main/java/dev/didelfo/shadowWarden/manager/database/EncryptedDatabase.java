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
    public void connect()  {
        try {
            // Cargar driver SQLCipher
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC de SQLCipher", e);
        }

        String url = String.format("jdbc:sqlite:%s", dbPath);

        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Statement stmt = connection.createStatement()) {
            // Establecer clave de cifrado
            stmt.execute("PRAGMA key = '" + encryptionKey + "'");

            // Crear tabla si no existe
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user (
                    uuidmojan TEXT UNIQUE,
                    uuidServer TEXT UNIQUE,
                    name TEXT,
                    token TEXT,
                    head TEXT,
                    idRol INTEGER DEFAULT 0
                );
            """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Insertar un nuevo token
    public void insertToken(String uuidMojan, Player p, String token, String head) throws SQLException {
        String sql = "INSERT INTO user(uuidmojan, uuidServer, name, token, head) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuidMojan);
            pstmt.setString(2, p.getUniqueId().toString());
            pstmt.setString(3, p.getName());
            pstmt.setString(4, token);
            pstmt.setString(5, head);
            pstmt.executeUpdate();
        }
    }

    // Obtener todos los tokens
    public List<String> getAllTokens() throws SQLException {
        List<String> tokens = new ArrayList<>();
        String sql = "SELECT name FROM user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tokens.add(rs.getString("name"));
            }
        }

        return tokens;
    }

    public String getToken(String uuid) {
        String sql = "SELECT token FROM user WHERE uuidmojan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public String getUuidServer(String uuid) {
        String sql = "SELECT uuidServer FROM user WHERE uuidmojan = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("uuidServer");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // Eliminar token por nombre
    public void deleteTokenByName(String nombre) throws SQLException {
        String sql = "DELETE FROM user WHERE name = ?";
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
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}