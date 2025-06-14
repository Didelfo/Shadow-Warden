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
                                idRol INTEGER DEFAULT 0,
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
                                id INTEGER PRIMARY KEY,
                                name TEXT NOT NULL
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

                // Rol principal de administrador
                stmt.execute("INSERT INTO rol(name) VALUES ('Root');");

                // Ingresamos los permisos
                stmt.execute("""
                        INSERT INTO permissions(id, name) VALUES
                        (0, 'shadowwarden.app.root'),
                        (1, 'shadowwarden.app.ui.chat');
                        """);

                // Asignamos todos los permisos al root
                stmt.execute("INSERT INTO permsRol(idRol, idPermission) VALUES (1, 0)");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

// ==========================================
//                  Inserts
// ==========================================

    // Insertar un nuevo token
    public void insertToken(String uuidMojan, Player p, String token) throws SQLException {
        String sql = "INSERT INTO user(uuidmojan, uuidServer, name, token) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuidMojan);
            pstmt.setString(2, p.getUniqueId().toString());
            pstmt.setString(3, p.getName());
            pstmt.setString(4, token);
            pstmt.executeUpdate();
        }
    }

    public void insertRol(String name) {
        String sql = "INSERT INTO rol(name) VALUES(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


// ==========================================
//                  Uptdate
// ==========================================

    public void actualizarRolUser(String user, int idRol) {
        String query = "UPDATE user SET idRol = ? WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idRol);
            stmt.setString(2, user);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


// ==========================================
//                  Gets
// ==========================================

    public boolean tienePermiso(String uuidMojan, String permissionName) {
        String sql = """
        SELECT 1
        FROM user u
        JOIN rol r ON u.idRol = r.id
        JOIN permsRol pr ON r.id = pr.idRol
        JOIN permissions p ON pr.idPermission = p.id
        WHERE u.uuidMojan = ? AND p.name = ?
        
        UNION
        
        SELECT 1
        FROM aditionalPerms ap
        JOIN permissions p ON ap.idPermission = p.id
        WHERE ap.uuidMojan = ? AND p.name = ?
        LIMIT 1
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuidMojan);
            pstmt.setString(2, permissionName);
            pstmt.setString(3, uuidMojan);
            pstmt.setString(4, permissionName);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Usuarios de la tabla user
    public List<String> getAllUser() {
        List<String> user = new ArrayList<>();
        String sql = "SELECT name FROM user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                user.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public int getidRolPorNombre(String nombre) {
        String sql = "SELECT id FROM rol WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public List<String> getAllRol() {
        List<String> rols = new ArrayList<>();
        String sql = "SELECT name FROM rol";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rols.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rols;
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

    // Obtener todos los permisos
    public List<String> getAllPlayerPermissions(String uuidMojan) {
        List<String> permissions = new ArrayList<>();
        String sql = """
        SELECT DISTINCT p.name
        FROM permissions p
        LEFT JOIN permsRol pr ON p.id = pr.idPermission
        LEFT JOIN user u ON pr.idRol = u.idRol
        LEFT JOIN aditionalPerms ap ON p.id = ap.idPermission
        WHERE u.uuidmojan = ? OR ap.uuidmojan = ?
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuidMojan);
            pstmt.setString(2, uuidMojan);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener permisos del jugador", e);
        }

        return permissions;
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

    public void deleteRol(int idRol) {
        String sql = "DELETE FROM rol WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idRol);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteUser(String name) {
        String sql = "DELETE FROM user WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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