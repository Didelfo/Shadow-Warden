package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class ManagerDBT {

    private ShadowWarden plugin;

    public ManagerDBT(ShadowWarden pl){this.plugin = pl;}


// =================================
//
// =================================

    public void onChat(String uuid, String name, String msg){
        String sql =
                """
                CREATE TABLE IF NOT EXISTS chat(
                    hour TEXT,
                    uuid TEXT,
                    name TEXT,
                    message TEXT
                );
                """;
        String sql2 = "INSERT INTO chat (hour, uuid, name, message) VALUES (?, ?, ?, ?)";

        executeSQL(sql);

        try (
                PreparedStatement pre = connectDBT().prepareStatement(sql2);
                ){
            pre.setString(1, LocalTime.now().toString());
            pre.setString(2, uuid);
            pre.setString(3, name);
            pre.setString(4, msg);
            pre.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }




// =================================
//
// =================================

    private Connection connectDBT() throws SQLException{
        // Sino existe la base de datos la crea.
        // Usamos la fecha del momento para saber si cambio de dia o no

        // Creamos la carpeta sino existe
        File folder = new File(plugin.getDataFolder(), "logs");
        if (!folder.exists()) folder.mkdir();

        // Guardamos la BD en la carpeta o nos conectamos a ella
        String today = LocalDate.now().toString();
        File db = new File(folder, today + ".db");

        return DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
    }

    private void createTables(){
        String sql =
                """
                CREATE TABLE IF NOT EXISTS chat(
                    hour TEXT,
                    uuid TEXT,
                    message TEXT
                );
                """;

        executeSQL(sql);
    }

    private void executeSQL(String sql){
        try (
                Statement sta = connectDBT().createStatement();
            ){
            sta.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
