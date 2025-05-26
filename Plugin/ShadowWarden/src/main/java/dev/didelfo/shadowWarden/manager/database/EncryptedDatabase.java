package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.manager.inventory.componets.Permissions;
import dev.didelfo.shadowWarden.manager.inventory.componets.User;
import dev.didelfo.shadowWarden.utils.ToolManager;
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
    public void connect() {
        try {
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

        // Verificar si la tabla 'user' existe
        boolean tableExists = false;
        try (ResultSet rs = connection.getMetaData().getTables(null, null, "user", null)) {
            tableExists = rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Statement stmt = connection.createStatement()) {
            // Establecer clave de cifrado siempre (no afecta si ya estaba establecida)
            stmt.execute("PRAGMA key = '" + encryptionKey + "'");

            if (!tableExists) {

                // Solo ejecutamos el esquema inicial si la BD no existía antes
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS user (
                                uuidmojan TEXT PRIMARY KEY,
                                uuidServer TEXT UNIQUE,
                                name TEXT,
                                token TEXT,
                                head TEXT,
                                idRol INTEGER DEFAULT 1,
                                FOREIGN KEY(idRol) REFERENCES rol(id) ON DELETE SET NULL
                            );
                        """);

                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS rol (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL
                            );
                        """);

                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS permissions (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL
                                description TEXT NOT NULL
                            );
                        """);

                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS permsRol (
                                idRol INTEGER NOT NULL,
                                idPermission INTEGER NOT NULL,
                                PRIMARY KEY (idRol, idPermission),
                                FOREIGN KEY (idRol) REFERENCES rol(id) ON DELETE CASCADE,
                                FOREIGN KEY (idPermission) REFERENCES permissions(id) ON DELETE CASCADE
                            );
                        """);

                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS aditionalPerms (
                                uuidmojan TEXT NOT NULL,
                                idPermission INTEGER NOT NULL,
                                PRIMARY KEY (uuidmojan, idPermission),
                                FOREIGN KEY (uuidmojan) REFERENCES user(uuidmojan) ON DELETE CASCADE,
                                FOREIGN KEY (idPermission) REFERENCES permissions(id) ON DELETE CASCADE
                            );
                        """);

                // Rolles Iniciales
                stmt.execute("INSERT INTO rol (name) VALUES ('Sin Rol'), ('Helper'), ('Moderador'), ('Administrador'), ('Owner')");

                // Permisos
//                stmt.execute("INSERT INTO permissions (name) VALUES ('kick'), ('ban'), ('viewlogs')");


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// ==========================================
//                  Inserts
// ==========================================

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



// ==========================================
//                  Gets
// ==========================================

    public List<Permissions> getAllPermissions() {
        List<Permissions> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");


                Permissions permission = new Permissions(id, name, description);
                permissions.add(permission);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener permisos: " + e.getMessage(), e);
        }
        return permissions;
    }

    public List<User> getAllUser() {
        List<User> usuarios = new ArrayList<>();
        String sql = "SELECT uuidmojan, name, cabeza, idRol FROM user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ToolManager t = new ToolManager(plugin);

            while (rs.next()) {
                String uuidmojan = rs.getString("uuidmojan");
                String name = rs.getString("name");
                String cabeza = rs.getString("cabeza");
                int idRol = rs.getInt("idRol");

                User user = new User(uuidmojan, name, t.stringToItem(cabeza), idRol);
                usuarios.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener permisos: " + e.getMessage(), e);
        }
        return usuarios;
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

// ==========================================
//                  Delete
// ==========================================

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