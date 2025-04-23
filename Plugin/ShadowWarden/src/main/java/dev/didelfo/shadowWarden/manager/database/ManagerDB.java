package dev.didelfo.shadowWarden.manager.database;

import dev.didelfo.shadowWarden.ShadowWarden;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerDB {

    private ShadowWarden plugin;
    private String url;

    private MySQLManager dbm;

    public ManagerDB(ShadowWarden pl){
        this.plugin = pl;
        this.url =
                "jdbc:mariadb://" + pl.getConfig().getString("db.url") + "/";
        this.dbm = new MySQLManager(
                url,
                plugin.getConfig().getString("db.user"),
                plugin.getConfig().getString("db.pass"),
                plugin.getConfig().getString("db.name"),
                plugin
        );
    }


    public synchronized void secuenciaInicioTablas(){
        /*
        * - users ->
        * - ip_users ->
        * - user_admin ->
        * - rol ->
        * - permission_rol ->
        * - permission ->
        * - additional_permission ->
        */

        // Tabla Users
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS users (
                  uuid VARCHAR(50) PRIMARY KEY,
                  nick VARCHAR(32)
                );
                """);

        // Tabla ip_users
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS ip_users (
                  uuid VARCHAR(50),
                  ip VARCHAR(15),
                  FOREIGN KEY (uuid) REFERENCES users(uuid)
                );
                """);

        // Tabla KDR
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS KDR (
                  uuid VARCHAR(50) PRIMARY KEY,
                  kills INT,
                  deaths INT,
                  FOREIGN KEY (uuid) REFERENCES users(uuid)
                );
                """);

        // Tabla tiempoJugador
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS time_play (
                  uuid VARCHAR(50),
                  start DATETIME,
                  stop DATETIME,
                  total TIME,
                  FOREIGN KEY (uuid) REFERENCES users(uuid)
                );
                """);

        // Talba rol
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS rol (
                  id INT PRIMARY KEY,
                  name VARCHAR(32) UNIQUE
                );
                """);

        // Tabla user_admoin
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS user_admin (
                  uuid VARCHAR(50) PRIMARY KEY,
                  token TEXT NULL,
                  id_rol INT NULL,
                  FOREIGN KEY (uuid) REFERENCES users(uuid),
                  FOREIGN KEY (id_rol) REFERENCES rol(id)
                );
                """);

        // Tabla permisos
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS permission (
                  id INT PRIMARY KEY,
                  name VARCHAR(32)
                );
                """);

        // Tabla permisos adiconales
        dbm.ejecutarSQL("""
                CREATE TABLE IF NOT EXISTS additional_permission (
                  uuid VARCHAR(50),
                  id_permission INT,
                  FOREIGN KEY (uuid) REFERENCES user_admin(uuid),
                  FOREIGN KEY (id_permission) REFERENCES permission(id)
                );
                """);
    }

    public synchronized void onJoinUser(String uuid, String nick ){

        Map<String, Object> datos = new HashMap<>();
        datos.put("uuid", uuid);
        List respuesta = dbm.consultarConCondiciones(
                "users",
                datos
        );

        if (respuesta.size() == 0){
            addUserOnJoin(uuid, nick);
        } else {
            editUserOnJoin(uuid, nick);
        }

    }

    private synchronized  void addUserOnJoin(String uuid, String nick){
        Map<String, Object> datos = new HashMap<>();
        datos.put("uuid", uuid);
        datos.put("nick", nick);
        dbm.insertar("users", datos);
    }

    private synchronized void editUserOnJoin(String uuid, String nick){
        Map<String, Object> datos = new HashMap<>();
        datos.put("nick", nick);

        Map<String, Object> con = new HashMap<>();
        con.put("uuid", uuid);

        dbm.actualizar("users", datos, con);
    }






}
