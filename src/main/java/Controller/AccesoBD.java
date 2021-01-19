package Controller;

import java.sql.*;

public class AccesoBD {
    private Connection conn;

    //Método constructor
    public AccesoBD() {
    }

    //Método para realizar la conexión con la base de datos
    public Connection conectar() {
        System.out.println("Intentando conexión con BBDD...");

        try {
            //Carga del driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            //Datos de la conexión
            String url = "jdbc:postgresql://localhost/ad_ud4_caso1";
            String usuario = "postgres";
            String contraseña = "root";

            //Objeto conexión le introducimos los datos par la conexión
            conn = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión realizada con éxito!!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al registrar el driver de PostgreSQL: " + e);
        }

        return conn; //Retornamos conexión
    }

    //Método para cerrar conexión
    public void cerrar() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
