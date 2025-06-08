package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;
import dev.didelfo.shadowWarden.models.Sanction;
import dev.didelfo.shadowWarden.models.User;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ManagerDB {

    private ShadowWarden plugin;
    private Connection connection;
    private String URL;

    public ManagerDB(ShadowWarden pl) {
        this.plugin = pl;
        connect();
    }


    private void connect() {

        boolean existe = false;
        switch (plugin.getConfig().getString("dataBase.type").toLowerCase()) {
            case "none" -> {
                plugin.getLogger().info("Base Desactivada");
            }
            case "sqlite" -> {
                existe = new File(plugin.getDataFolder(), "db.db").exists();
                this.URL = new File(plugin.getDataFolder(), "db.db").getAbsolutePath();

                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("No se encontró el driver JDBC de SQLCipher", e);
                }

                String url = String.format("jdbc:sqlite:%s", URL);

                try {
                    connection = DriverManager.getConnection(url);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Si no existia el archi nos conectamos para crearlo y luego creamos la tablas
                if (!existe) {

                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute("""
                                    CREATE TABLE IF NOT EXISTS users (
                                        uuid TEXT PRIMARY KEY,
                                        name TEXT,
                                        ip TEXT
                                    );
                                """);
                        stmt.execute("""
                                    CREATE TABLE IF NOT EXISTS sanction (
                                        uuid_user TEXT,
                                        type TEXT,
                                        start TEXT,
                                        expire TEXT,
                                        reason TEXT,
                                        nameStaff TEXT,
                                        ip TEXT,
                                        FOREIGN KEY (uuid_user) REFERENCES users(uuid) ON DELETE CASCADE
                                    );
                                """);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case "mysql" -> {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                String url = "jdbc:mysql://" + plugin.getConfig().getString("dataBase.ip") + ":" + plugin.getConfig().getInt("dataBase.port") + "/";
                String urlDB = url + plugin.getConfig().getString("dataBase.name");

                // Nos conectamos primero para comprobar si existe esta base de datos

                try {

                    // Ahora conectar a la BD real
                    connection = DriverManager.getConnection(
                            urlDB,
                            plugin.getConfig().getString("dataBase.user"),
                            plugin.getConfig().getString("dataBase.pass")
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Creamos las tablas en la base de datos

                try (Statement stmtt = connection.createStatement()) {

                    stmtt.execute("""
                            CREATE TABLE IF NOT EXISTS users (
                                    uuid VARCHAR(50) PRIMARY KEY,
                                    name VARCHAR(40) NOT NULL,
                                    ip VARCHAR(60) NOT NULL
                                );
                            """);
                    stmtt.execute("""
                            CREATE TABLE IF NOT EXISTS sanction (
                                    uuid_user VARCHAR(50),
                                    type VARCHAR(20) NOT NULL,
                                    start VARCHAR(70),
                                    expire VARCHAR(70),
                                    reason VARCHAR(120),
                                    nameStaff VARCHAR(50),
                                    ip VARCHAR(60),
                                    FOREIGN KEY (uuid_user) REFERENCES users(uuid) ON DELETE CASCADE
                                );
                            """);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> {
            }
        }
    }


    //Añade o actualiza un usuario en la base de datos
    public void addOrUpdateUser(String uuid, String name, String ip) {
        // Consulta para ver si ya existe un usuario con este UUID
        String checkSql = "SELECT * FROM users WHERE uuid = ? LIMIT 1";

        // Sentencia para actualizar los datos
        String updateSql = "UPDATE users SET name = ?, ip = ? WHERE uuid = ?";

        // Sentencia para insertar nuevos datos
        String insertSql = "INSERT INTO users (uuid, name, ip) VALUES (?, ?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, uuid);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // El usuario ya existe → actualizamos
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, name);
                    updateStmt.setString(2, ip);
                    updateStmt.setString(3, uuid);
                    updateStmt.executeUpdate();
                }
            } else {
                // El usuario no existe → insertamos
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, uuid);
                    insertStmt.setString(2, name);
                    insertStmt.setString(3, ip);
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
        }
    }


    // Añadir o Actualizar sancion
    public void addOrUpdateSanction(String uuid, String type, String reason, String Expire, String nameStaff, String ip) {
        String checkSql = "SELECT * FROM sanction WHERE uuid_user = ? AND type = ? ORDER BY start DESC LIMIT 1";
        String updateSql = "UPDATE sanction SET start = ?, expire = ?, reason = ?, nameStaff = ?, ip = ? WHERE uuid_user = ? AND type = ?";
        String insertSql = "INSERT INTO sanction (uuid_user, type, start, expire, reason, nameStaff, ip) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, uuid);
            ResultSet rs = checkStmt.executeQuery();

            String fechaAhora = getCurrentDateString();

            if (rs.next()) {
                // Ya existe un registro
                String storedExpire = rs.getString("expire");


                // Si expira y aunestá vigente, actualizamos, si es permanente o es anterior no lo actualizamos
                if (!(storedExpire.equals("never") && isDateBefore(fechaAhora, storedExpire))) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, fechaAhora);
                        updateStmt.setString(2, Expire);
                        updateStmt.setString(3, reason);
                        updateStmt.setString(4, nameStaff);
                        updateStmt.setString(5, ip);
                        updateStmt.setString(6, uuid);
                        updateStmt.setString(7, type);
                        updateStmt.executeUpdate();
                    }
                }
                // Si ya expiró, podrías insertar uno nuevo (según tu lógica)
            } else {
                // No hay registros → insertamos nuevo
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, uuid);
                    insertStmt.setString(2, type);
                    insertStmt.setString(3, fechaAhora);
                    insertStmt.setString(4, Expire);
                    insertStmt.setString(5, reason);
                    insertStmt.setString(6, nameStaff);
                    insertStmt.setString(7, ip);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getCurrentDateString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    // Devuelve true → d1 es después de d2
    private boolean isDateAfter(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }

    // Devuelve true → d1 es antes de d2
    private boolean isDateBefore(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }


    // Comprobar si un jugador tiene uan sancion
    public boolean isPlayerSancionado(String uuid, String type, String ip) {
        String sql = """
                    SELECT * FROM sanction
                    WHERE type = ? AND (uuid_user = ? OR ip = ?)
                    ORDER BY start DESC LIMIT 1
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            stmt.setString(2, uuid);
            stmt.setString(3, ip);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String expire = rs.getString("expire");
                // Si es "never" → está permanentemente baneado
                if ("never".equalsIgnoreCase(expire)) {
                    return true;
                } else {
                    // Compara la fecha actual con la vencimiento
                    String now = getCurrentDateString();
                    return isDateBefore(now, expire); // true si ahora es antes de expire → sigue vigente el ban
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error al comprobar si el jugador está baneado");
            e.printStackTrace();
        }
        return false;
    }

    // Obtenemos la informacion de la sancion
    public Sanction getInfoSanction(String uuid, String type, String ip) {
        String sql = "SELECT * FROM sanction WHERE type = ? AND (uuid_user = ? OR ip = ?) ORDER BY start DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            stmt.setString(2, uuid);
            stmt.setString(3, ip);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Sanction(
                        rs.getString("expire"),
                        rs.getString("reason"),
                        rs.getString("nameStaff")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Obtenemos la informacion del jugador
    public User getInfoUser(String name) {
        String sql = "SELECT * FROM users WHERE name = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("uuid"),
                        rs.getString("name"),
                        rs.getString("ip")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Eliminamos la sancion
    public void deleteSancion(String uuid, String type) {
        String checkSql = "SELECT * FROM sanction WHERE uuid_user = ? AND type = ? ORDER BY start DESC LIMIT 1";
        String deleteSql = "DELETE FROM sanction WHERE uuid_user = ? AND type = ? AND start = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, uuid);
            checkStmt.setString(2, type);
            ResultSet rs = checkStmt.executeQuery();

            String fechaAhora = getCurrentDateString();

            if (rs.next()) {
                // Ya existe un registro
                String startt = rs.getString("start");
                String storedExpire = rs.getString("expire");


                // Si expira y aunestá vigente, borramos el registro
                if (isDateBefore(fechaAhora, storedExpire) || (storedExpire.equals("never"))) {
                    try (PreparedStatement deleteStm = connection.prepareStatement(deleteSql)) {
                        deleteStm.setString(1, uuid);
                        deleteStm.setString(2, type);
                        deleteStm.setString(3, startt);
                        deleteStm.execute();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
