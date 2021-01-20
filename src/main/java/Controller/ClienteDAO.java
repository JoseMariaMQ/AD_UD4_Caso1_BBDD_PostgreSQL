package Controller;

import Model.Cliente;
import java.sql.*;
import java.time.LocalDate;

public class ClienteDAO {
    private PreparedStatement ps;
    private PreparedStatement ps2;
    private Statement st;
    private ResultSet rs;
    AccesoBD accesoBD = new AccesoBD();

    //Método para insertar cliente
    public void insertarCliente(Cliente cliente) {
        //Sentencia SQL para el PreparedStatement
        String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, comercial_principal, id_empresa, visitas) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            //Objeto de tipo PreparedStatement para enviar sentencias SQL
            ps = accesoBD.conectar().prepareStatement(sql);

            //Se establece los parámetros designados en los valores de la consulta
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido1());
            ps.setString(3, cliente.getApellido2());
            ps.setString(4, cliente.getComercialPrincipal());
            ps.setInt(5, cliente.getIdEmpresa());

            //Convertimos en Array el LocalDate[]
            Array array = accesoBD.conectar().createArrayOf("DATE", cliente.getVisitas());
            ps.setArray(6, array);

            //Ejecuta instrucción SQL
            int returnPS = ps.executeUpdate();

            //Comprobamos si ha insertado alguna fila. Si el PreparedStatement retorna 0, no ha insertado nada.
            if (returnPS == 0) {
                System.out.println("No se ha insertado cliente.");
            } else {
                System.out.println("Insertando cliente...");
                System.out.println("Cliente insertado correctamente!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally { //Cerrar conexiones
            try {
                if (ps != null) ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            accesoBD.cerrar();
            System.out.println("Conexión con BBDD cerrada.");
        }
    }

    //Método para mostrar clientes
    public void mostrarClientes() {
        //Sentencia SQL para el ResultSet (Ordenamos por el id de cliente)
        String sql = "SELECT * FROM clientes ORDER BY id_cliente;";

        try {
            st = accesoBD.conectar().createStatement(); //Objeto que utilizamos para ejecutar declaración SQL estática
            rs = st.executeQuery(sql); //ResultSet para almacenar datos de consulta que devuelve statement

            System.out.println("Mostrando clientes...");

            //Creamos objeto cliente
            Cliente cliente = null;
            //Recorremos los datos del ResultSet
            while (rs.next()) {
                //Almacenamos el tipo de datos de las visitas en un array
                Array visitas = rs.getArray(7);
                //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                Date[] visitasDate = (Date[])visitas.getArray();
                //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                // de la longitud del de la base de datos
                LocalDate[] localDate = new LocalDate[visitasDate.length];
                //Lo recorremos con un for y lo almacenamos en localDate
                for(int i=0; i<visitasDate.length; i++) {
                    localDate[i] = visitasDate[i].toLocalDate();
                }
                //Instanciamos un cliente y le introducimos los datos de la consulta
                cliente = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                //Imprimimos por pantalla los datos de los clientes
                System.out.println(cliente);
            }
            //Si no hay datos de clientes, mostramos mensaje de no hay clientes...
            if (cliente == null) {
                System.out.println("No hay datos de clientes.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally { //Cerramos para liberar recursos
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            accesoBD.cerrar(); //Método que cierra conexión
        }
    }

    //Método para borrar cliente
    public void eliminarCliente(int id) {
        //Sentencia SQL para el PreparedStatement
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try {
            //Objeto de tipo PreparedStatement para enviar sentencias SQL
            ps = accesoBD.conectar().prepareStatement(sql);

            //Establecemos parámetro designado en el valor de la consulta
            ps.setInt(1, id);

            //Ejecutamos la sentencia SQL y almacenamos el valor que devuelve en la variable
            int returnPS = ps.executeUpdate();
            //Comprobamos si ha borrado alguna fila. Si el PreparedStatement retorna 0
            // no ha borrado nada por lo que no hay ninguna fila con ese 'id'
            if (returnPS == 0) {
                System.out.println("No hay ningún cliente con ese 'id'.");
            } else {
                System.out.println("Borrando cliente...");
                System.out.println("Cliente borrado correctamente!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally { //Cerramos conexiones
            try {
                if (ps != null) ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            accesoBD.cerrar();
        }
    }

    //Método para actualizar visitas
    public void insertarVisitas(int id, LocalDate localDate) {
        //Sentencias SQL para los PreparedStatement, una para seleccionar el cliente
        // y comprobar el array de fechas de visita y otro para la actualización
        String sql = "UPDATE clientes SET visitas[?] = ? WHERE id_cliente = ?;";
        String sqlVisitas = "SELECT visitas FROM clientes WHERE id_cliente = ?;";

        Array visitas = null;
        Date[] visitasDate;

        try {
            //Objeto de tipo PreparedStatement para enviar sentencias SQL
            ps2 = accesoBD.conectar().prepareStatement(sqlVisitas);
            //Establecemos parámetro designado en el valor de la consulta
            ps2.setInt(1, id);
            //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
            rs = ps2.executeQuery();

            //Recorremos el ResultSet para calcular las fechas de visita que tiene ese determinado cliente
            while (rs.next()) {
                visitas = rs.getArray(1); //Almacenamos en un array
            }
            visitasDate = (Date[]) visitas.getArray(); //Convertimos en Date para poder calcular la longitud

            //Objeto de tipo PreparedStatement para enviar sentencias SQL
            ps = accesoBD.conectar().prepareStatement(sql);

            //Establecemos los parámetros designados en los valores de la consulta
            ps.setInt(1, visitasDate.length+1); //Lo almacenamos calculando la longitud+1 (SQL empieza en 1)
            ps.setDate(2, Date.valueOf(localDate)); //Introducimos fecha convertida en Date
            ps.setInt(3, id); //Le asignamos a la sentencia el mismo 'id'

            int returnPS = ps.executeUpdate(); //Ejecutamos sentencia

            //Comprobamos si ha actualizado alguna fecha. Si el PreparedStatement retorna 0
            // no ha actualizado nada por lo que no hay ninguna fila con ese 'id'
            if (returnPS == 0) {
                System.out.println("No hay ningún cliente con ese 'id'.");
            } else {
                System.out.println("Actualizando visitas...");
                System.out.println("Fechas de visitas actualizada correctamente!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally { //Cerramos conexiones
            try {
                if (ps != null) ps.close();
                if (ps2 != null) ps2.close();
                if (rs != null) rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            accesoBD.cerrar();
        }
    }

    //Método para buscar cliente
    public void buscarCliente(Cliente cliente) {
        //Comprobamos con if-else if cual es el dato que introduzco el usuario
        if (cliente.getIdCliente() != -1) { //Si introduzco búsqueda por 'id'
            //Sentencia para buscar por 'id'
            String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setInt(1, cliente.getIdCliente());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'id' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'id'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getNombre() != null) { //Si el usuario introduce búsqueda por nombre
            //Sentencia para buscar por nombre
            String sql = "SELECT * FROM clientes WHERE nombre LIKE ?";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getNombre());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto cliente
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Instanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'nombre' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'nombre'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getApellido1() != null) { //Si el usuario introduce búsqueda por primer apellido
            //Sentencia para buscar por primer apellido
            String sql = "SELECT * FROM clientes WHERE apellido1 LIKE ?";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getApellido1());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'apellido' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'apellido'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getApellido2() != null) { //Si el usuario introduce búsqueda por segundo apellido
            //Sentencia para buscar por segundo apellido
            String sql = "SELECT * FROM clientes WHERE apellido2 LIKE ?";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getApellido2());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;
                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'apellido' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'apellido'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getComercialPrincipal() != null) { //Si el usuario introduce búsqueda por comercial principal
            //Sentencia para buscar por comercial principal
            String sql = "SELECT * FROM clientes WHERE comercial_principal LIKE ?";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                // Establecemos los parámetros designados en los valores de la consulta, utilizamos '%'
                // para establecer que contenga la cadena de caracteres que le introducimos asi si solo
                // introduce el nombre buscará igualmente aunque no introduzca el apellido del comercial y viceversa
                ps.setString(1, "%"+cliente.getComercialPrincipal()+"%");
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'comercial principal' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'comercial principal'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdEmpresa() != -1) { //Si el usuario introduce búsqueda por id de empresa
            //Sentencia para buscar por id de empresa
            String sql = "SELECT * FROM clientes WHERE id_empresa = ?";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setInt(1, cliente.getIdEmpresa());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con ese 'id de empresa' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con ese 'id de empresa'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getVisitas() != null) { //Si el usuario introduce búsqueda por fecha de visita
            //Sentencia para buscar por fecha de visita
            String sql = "SELECT * FROM clientes WHERE ? = ANY (visitas)";

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setDate(1, Date.valueOf(cliente.getVisitas()[0]));
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps.executeQuery();

                //Creamos objeto clientes
                Cliente clientes = null;

                //Recorremos el ResultSet
                while (rs.next()) {
                    //Almacenamos el tipo de datos de las visitas en un array
                    Array visitas = rs.getArray(7);
                    //Lo convertimos en un array del tipo de datos de la columna de la base de datos
                    Date[] visitasDate = (Date[])visitas.getArray();
                    //Creamos un array del tipo de datos de las fechas que tiene la clase clientes
                    // de la longitud del de la base de datos
                    LocalDate[] localDate = new LocalDate[visitasDate.length];
                    //Lo recorremos con un for y lo almacenamos en localDate
                    for(int i=0; i<visitasDate.length; i++) {
                        localDate[i] = visitasDate[i].toLocalDate();
                    }

                    //Intanciamos un cliente y le introducimos los valores que devuelve la consulta
                    clientes = new Cliente(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6), localDate);

                    //Imprimimos por pantalla los datos de los clientes
                    System.out.println(clientes);
                }
                //Si no hay datos con es 'fecha de visita' mostramos mensaje de no hay clientes...
                if (clientes == null) {
                    System.out.println("No hay clientes con esa 'fecha de visita'.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        //Cerramos conexiones
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        accesoBD.cerrar();
    }

    //Método para modificar cliente
    public void modificarCliente(Cliente cliente) {
        //Comprobamos con if-else if cual es el dato que introduzco el usuario
        if (cliente.getIdCliente() != -1 && cliente.getNombre() != null) { //Si usuario introduce modificar nombre
            //Sentencia para actualizar nombre de un 'id' determinado
            String sql = "UPDATE clientes SET nombre = ? WHERE id_cliente = ?;";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getNombre());
                ps.setInt(2, cliente.getIdCliente());
                //Ejecutamos la consulta SQL
                int returnPS = ps.executeUpdate();

                //Comprobamos si ha modificado nombre. Si el PreparedStatement retorna 0
                // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                if (returnPS == 0) {
                    System.out.println("No hay ningún cliente con ese 'id'.");
                } else {
                    System.out.println("Modificando nombre...");
                    System.out.println("Nombre modificado correctamente!");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdCliente() != -1 && cliente.getApellido1() != null) { //Si el usuario introduce modificar primer apellido
            //Sentencia para actualizar primer apellido de un 'id' determinado
            String sql = "UPDATE clientes SET apellido1 = ? WHERE id_cliente = ?;";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getApellido1());
                ps.setInt(2, cliente.getIdCliente());
                //Ejecutamos la consulta SQL
                int returnPS = ps.executeUpdate();

                //Comprobamos si ha modificado primer apellido. Si el PreparedStatement retorna 0
                // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                if (returnPS == 0) {
                    System.out.println("No hay ningún cliente con ese 'id'.");
                } else {
                    System.out.println("Modificando primer apellido...");
                    System.out.println("Primer apellido modificado correctamente!");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdCliente() != -1 && cliente.getApellido2() != null) { //Si el usuario introduce modificar segundo apellido
            //Sentencia para actualizar segundo apellido de un 'id' determinado
            String sql = "UPDATE clientes SET apellido2 = ? WHERE id_cliente = ?;";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getApellido2());
                ps.setInt(2, cliente.getIdCliente());
                //Ejecutamos la consulta SQL
                int returnPS = ps.executeUpdate();

                //Comprobamos si ha modificado segundo apellido. Si el PreparedStatement retorna 0
                // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                if (returnPS == 0) {
                    System.out.println("No hay ningún cliente con ese 'id'.");
                } else {
                    System.out.println("Modificando segundo apellido...");
                    System.out.println("Segundo apellido modificado correctamente!");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdCliente() != -1 && cliente.getComercialPrincipal() != null) { //Si el usuario introduce modificar comercial principal
            //Sentencia para actualizar comercial principal de un 'id' determinado
            String sql = "UPDATE clientes SET comercial_principal = ? WHERE id_cliente = ?;";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setString(1, cliente.getComercialPrincipal());
                ps.setInt(2, cliente.getIdCliente());
                //Ejecutamos la consulta SQL
                int returnPS = ps.executeUpdate();

                //Comprobamos si ha modificado comercial principal. Si el PreparedStatement retorna 0
                // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                if (returnPS == 0) {
                    System.out.println("No hay ningún cliente con ese 'id'.");
                } else {
                    System.out.println("Modificando comercial principal...");
                    System.out.println("Comercial principal modificado correctamente!");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdCliente() != -1 && cliente.getIdEmpresa() != -1) { //Si el usuario introduce modificar id empresa
            //Sentencia para actualizar id de empresa de un 'id' determinado
            String sql = "UPDATE clientes SET id_empresa = ? WHERE id_cliente = ?;";
            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps = accesoBD.conectar().prepareStatement(sql);
                //Establecemos los parámetros designados en los valores de la consulta
                ps.setInt(1, cliente.getIdEmpresa());
                ps.setInt(2, cliente.getIdCliente());
                //Ejecutamos la consulta SQL
                int returnPS = ps.executeUpdate();

                //Comprobamos si ha modificado id de empresa. Si el PreparedStatement retorna 0
                // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                if (returnPS == 0) {
                    System.out.println("No hay ningún cliente con ese 'id'.");
                } else {
                    System.out.println("Modificando id de empresa...");
                    System.out.println("Id de empresa modificado correctamente!");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (cliente.getIdCliente() != -1 && cliente.getVisitas() != null) { //Si el usuario introduce modificar fecha de visita
            //Sentencia para actualizar id de empresa de un 'id' determinado
            String sql = "UPDATE clientes SET visitas[?] = ? WHERE id_cliente = ?;";
            //Sentencia para seleccionar visitas del cliente con ese 'id'
            String sqlVisitas = "SELECT visitas FROM clientes WHERE id_cliente = ?;";

            Array visitas = null;
            Date[] visitasDate;

            try {
                //Objeto de tipo PreparedStatement para enviar sentencias SQL
                ps2 = accesoBD.conectar().prepareStatement(sqlVisitas);
                //Establecemos parámetro designado en el valor de la consulta
                ps2.setInt(1, cliente.getIdCliente());
                //Almacenamos en un ResultSet el resultado de la consulta SQL que devuelve el PreparedStatement
                rs = ps2.executeQuery();

                //Recorremos el ResultSet para calcular las fechas de visita que tiene ese determinado cliente
                while (rs.next()) {
                    visitas = rs.getArray(1); //Almacenamos en un array
                }
                visitasDate = (Date[]) visitas.getArray(); //Convertimos en Date
                //Creamos array de LocalDate para recorrer visitasDate y convertirlo en LocalDate
                LocalDate[] visitasLocalDate = new LocalDate[visitasDate.length];
                for(int i=0; i<visitasDate.length; i++) {
                    visitasLocalDate[i] = visitasDate[i].toLocalDate();
                }

                int pos = -1;
                //Calculamos el índice con un for
                for (int i=0; i<visitasLocalDate.length; i++) {
                    if (visitasLocalDate[i].isEqual(cliente.getVisitas()[0])){
                        pos = i;
                    }
                }
                //Comprobamos que existe la fecha
                if (pos==-1) {
                    System.out.println("No existe esa fecha de visita para el cliente.");
                } else { //Si existe ejecutamos la sentencia SQL para modificar fecha de visita
                    //Objeto de tipo PreparedStatement para enviar sentencias SQL
                    ps = accesoBD.conectar().prepareStatement(sql);
                    //Establecemos los parámetros designados en los valores de la consulta
                    ps.setInt(1, pos+1); //Le sumamos 1 ya que los índices en SQL empiezan en 1
                    ps.setDate(2, Date.valueOf(cliente.getVisitas()[1]));
                    ps.setInt(3, cliente.getIdCliente());
                    //Ejecutamos la consulta SQL
                    int returnPS = ps.executeUpdate();

                    //Comprobamos si ha modificado fecha visita a cliente. Si el PreparedStatement retorna 0
                    // no ha modificado nada por lo que no hay ninguna fila con ese 'id'
                    if (returnPS == 0) {
                        System.out.println("No hay ningún cliente con ese 'id'.");
                    } else {
                        System.out.println("Modificando fecha de visita...");
                        System.out.println("Fecha de visita modificada correctamente!");
                    }
                    //Cerramos conexiones
                    try {
                        if (ps != null) ps.close();
                        if (rs != null) rs.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        //Cerramos conexiones
        try {
            if (ps != null) ps.close();
            if (ps2 != null) ps2.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        accesoBD.cerrar();
    }
}
