package dev.didelfo.shadowWarden.manager.database;


import dev.didelfo.shadowWarden.ShadowWarden;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLManager {

    private ShadowWarden plugin;
    private Connection connection;

    // Constructor: Conecta a la base de datos
    public MySQLManager(String url, String usuario, String pass, String name, ShadowWarden pl) {
        this.plugin = pl;
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver"); // Para MySQL
            Class.forName("org.mariadb.jdbc.Driver"); // Para MariaDB

            Connection admin = DriverManager.getConnection(url, usuario, pass);

            // Creamos la base de datos si no existe
            Statement st = admin.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
            st.close();
            admin.close();

            connection = DriverManager.getConnection(url+name, usuario, pass);
            System.out.println("Conexión a MySQL/MariaDB establecida.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

/*
    // Método para conectar a la BD
    public void conectar() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
        }
    }

 */
    // Método para desconectar
    public void desconectar()  {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Consultar con condiciones
    public List<Map<String, Object>> consultarConCondiciones(String tabla, Map<String, Object> condiciones) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tabla);
        List<Object> parametros = new ArrayList<>();

        if (condiciones != null && !condiciones.isEmpty()) {
            sql.append(" WHERE ");
            boolean primero = true;
            for (Map.Entry<String, Object> entry : condiciones.entrySet()) {
                if (!primero) {
                    sql.append(" AND ");
                }
                sql.append(entry.getKey()).append(" = ?");
                parametros.add(entry.getValue());
                primero = false;
            }
        }

        return ejecutarConsulta(sql.toString(), parametros);
    }

    // Consultar todos los registros
    public List<Map<String, Object>> consultarTodo(String tabla)  {
        String sql = "SELECT * FROM " + tabla;
        return ejecutarConsulta(sql, null);
    }

    // Consultar con límite
    public List<Map<String, Object>> consultarLimitado(String tabla, int limite)  {
        String sql = "SELECT * FROM " + tabla + " LIMIT ?";
        List<Object> parametros = new ArrayList<>();
        parametros.add(limite);
        return ejecutarConsulta(sql, parametros);
    }

    // Consultar ordenado
    public List<Map<String, Object>> consultarOrdenado(String tabla, String campoOrden, boolean ascendente) {
        String orden = ascendente ? "ASC" : "DESC";
        String sql = "SELECT * FROM " + tabla + " ORDER BY " + campoOrden + " " + orden;
        return ejecutarConsulta(sql, null);
    }

    // Consultar limitado y ordenado
    public List<Map<String, Object>> consultarLimitadoOrdenado(String tabla, int limite, String campoOrden, boolean ascendente) {
        String orden = ascendente ? "ASC" : "DESC";
        String sql = "SELECT * FROM " + tabla + " ORDER BY " + campoOrden + " " + orden + " LIMIT ?";
        List<Object> parametros = new ArrayList<>();
        parametros.add(limite);
        return ejecutarConsulta(sql, parametros);
    }

    // Insertar registro
    public int insertar(String tabla, Map<String, Object> datos) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tabla + " (");
        StringBuilder valores = new StringBuilder(" VALUES (");
        List<Object> parametros = new ArrayList<>();

        boolean primero = true;
        for (Map.Entry<String, Object> entry : datos.entrySet()) {
            if (!primero) {
                sql.append(", ");
                valores.append(", ");
            }
            sql.append(entry.getKey());
            valores.append("?");
            parametros.add(entry.getValue());
            primero = false;
        }

        sql.append(")").append(valores).append(")");
        return ejecutarActualizacion(sql.toString(), parametros);
    }

    // Actualizar registros
    public int actualizar(String tabla, Map<String, Object> datos, Map<String, Object> condiciones) {
        StringBuilder sql = new StringBuilder("UPDATE " + tabla + " SET ");
        List<Object> parametros = new ArrayList<>();

        boolean primero = true;
        for (Map.Entry<String, Object> entry : datos.entrySet()) {
            if (!primero) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            parametros.add(entry.getValue());
            primero = false;
        }

        if (condiciones != null && !condiciones.isEmpty()) {
            sql.append(" WHERE ");
            primero = true;
            for (Map.Entry<String, Object> entry : condiciones.entrySet()) {
                if (!primero) {
                    sql.append(" AND ");
                }
                sql.append(entry.getKey()).append(" = ?");
                parametros.add(entry.getValue());
                primero = false;
            }
        }

        return ejecutarActualizacion(sql.toString(), parametros);
    }

    // Borrar registros
    public int borrar(String tabla, Map<String, Object> condiciones) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + tabla);
        List<Object> parametros = new ArrayList<>();

        if (condiciones != null && !condiciones.isEmpty()) {
            sql.append(" WHERE ");
            boolean primero = true;
            for (Map.Entry<String, Object> entry : condiciones.entrySet()) {
                if (!primero) {
                    sql.append(" AND ");
                }
                sql.append(entry.getKey()).append(" = ?");
                parametros.add(entry.getValue());
                primero = false;
            }
        }

        return ejecutarActualizacion(sql.toString(), parametros);
    }

    // Crear tabla
    public void crearTabla(String nombreTabla, Map<String, String> columnas, String clavePrimaria) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + nombreTabla + " (");

        boolean primero = true;
        for (Map.Entry<String, String> entry : columnas.entrySet()) {
            if (!primero) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" ").append(entry.getValue());
            primero = false;
        }

        if (clavePrimaria != null && !clavePrimaria.isEmpty()) {
            sql.append(", PRIMARY KEY (").append(clavePrimaria).append(")");
        }

        sql.append(")");

        ejecutarSQL(sql.toString());
    }

    // Borrar tabla
    public void borrarTabla(String nombreTabla) {
        String sql = "DROP TABLE IF EXISTS " + nombreTabla;
        ejecutarSQL(sql);
    }

    // Ejecutar SQL directo
    public void ejecutarSQL(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método privado para ejecutar consultas
    private List<Map<String, Object>> ejecutarConsulta(String sql, List<Object> parametros) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (parametros != null) {
                for (int i = 0; i < parametros.size(); i++) {
                    pstmt.setObject(i + 1, parametros.get(i));
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String nombreColumna = metaData.getColumnName(i);
                        Object valor = rs.getObject(i);
                        fila.put(nombreColumna, valor);
                    }
                    resultados.add(fila);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return resultados;
    }

    // Método privado para ejecutar actualizaciones
    private int ejecutarActualizacion(String sql, List<Object> parametros)  {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (parametros != null) {
                for (int i = 0; i < parametros.size(); i++) {
                    pstmt.setObject(i + 1, parametros.get(i));
                }
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}



