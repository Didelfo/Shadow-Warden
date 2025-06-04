package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class ManagerDB {

    private ShadowWarden plugin;
    private Connection connection;
    private String URL;

    public ManagerDB(ShadowWarden pl) {
        this.plugin = pl;
    }


    private void connect() {

        boolean existe = false;
        switch (plugin.getConfig().getString("dataBase.type").toLowerCase()) {
            case "none" -> {
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
                                        name TEXT
                                    );
                                """);
                        stmt.execute("""
                                    CREATE TABLE IF NOT EXISTS ip_users (
                                        uuid_user TEXT PRIMARY KEY,
                                        ip TEXT,
                                        FOREIGN KEY (uuid_user) REFERENCES users(uuid) ON DELETE CASCADE
                                    );
                                """);
                        stmt.execute("""
                                    CREATE TABLE IF NOT EXISTS sanction (
                                        uuid_user TEXT PRIMARY KEY,
                                        type TEXT,
                                        start TEXT,
                                        expire TEXT,
                                        reason TEXT,
                                        FOREIGN KEY (uuid_user) REFERENCES users(uuid) ON DELETE CASCADE
                                    );
                                """);
                    } catch (Exception e) {}
                }
            }
            case "mysql" -> {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                String url = "jdbc:mysql://" + plugin.getConfig().getString("dataBase.") + ":" + 3306 + "/miapp";




            }
            default -> {
            }
        }


    }


}
