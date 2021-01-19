package View;

import Controller.ClienteDAO;
import Model.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InteraccionUsuario {
    //Variables con las opciones del menu principal
    private String info = "Elige una de las siguientes opciones:";
    private String opcion1 = "1 - Mostrar todos los clientes.";
    private String opcion2 = "2 - Insertar un nuevo cliente.";
    private String opcion3 = "3 - Eliminar un cliente.";
    private String opcion4 = "4 - Insertar una nueva visita de un cliente.";
    private String opcion5 = "5 - Buscar un cliente.";
    private String opcion6 = "6 - Modificar un cliente.";
    private String salir = "0 - SALIR.";

    private int opcionElegida;

    //Contructor sin parámetros para instanciar la clase
    public InteraccionUsuario() {
    }

    //Método que muestra las opciones y llama a un método en función de la opción elegida
    public void mostrarOpciones() {
        System.out.println(info + "\n" +
                opcion1 + "\n" +
                opcion2 + "\n" +
                opcion3 + "\n" +
                opcion4 + "\n" +
                opcion5 + "\n" +
                opcion6 + "\n\n" +
                salir + "\n");

        Scanner in = new Scanner(System.in);
        System.out.println("Pulsa el número de la opción que desea:");

        opcionElegida = comprobarEntrada();

        //Llamamos al método en función de la opción elegida
        switch (opcionElegida) {
            case 1:
                mostrarClientes();
                break;
            case 2:
                insertarCliente();
                break;
            case 3:
                eliminarCliente();
                break;
            case 4:
                insertarVisita();
                break;
            case 5:
                buscarCliente();
                break;
            case 6:
                modificarCliente();
                break;
            case 0:
                System.exit(0);
                break;
            //Si el entero no corresponde a ninguna opción volvemos a mostrar opciones
            default:
                System.out.println("No ha insertado ninguna de las opciones propuestas.\n");
                mostrarOpciones();
        }
    }

    //Método que muestra todos los clientes de la base de datos
    public void mostrarClientes() {
        System.out.println("Has elegido mostrar clientes.");
        //Instanciamos la clase DAO
        ClienteDAO clienteDAO = new ClienteDAO();
        //Llamamos al método que muestra los clientes
        clienteDAO.mostrarClientes();
    }

    //Método para insertar un cliente
    public void insertarCliente() {
        System.out.println("Has elegido insertar cliente.");
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente;
        Scanner in = new Scanner(System.in);

        //Pedimos por teclado los datos del cliente
        System.out.println("Inserta el nombre del cliente:");
        String nombre = in.nextLine();

        System.out.println("Inserta el primer apellido del cliente:");
        String apellido1 = in.nextLine();

        System.out.println("Inserta el segundo apellido del cliente:");
        String apellido2 = in.nextLine();

        System.out.println("Inserta el nombre del comercial principal del cliente:");
        String comercialPrincipal = in.nextLine();

        System.out.println("Inserta el id de la empresa:");
        int idEmpresa = comprobarEntrada();

        System.out.println("Introduce las fechas de visita al cliente:");
        //ArrayList para almacenar las visitas, este puede ser dinámico sin tener que saber exactamente
        // cuantas fechas introducirá el usuario
        ArrayList<LocalDate> fechasVisita = new ArrayList<>();
        LocalDate fecha;
        String otraVisita = "";
        //Bucle para poder insertar más de una fecha
        do {
            fecha = comprobarFechas();
            fechasVisita.add(fecha);
            System.out.println("¿Quieres añadir otra fecha de visita? Y/N");
            otraVisita = in.nextLine();
        } while (otraVisita.equalsIgnoreCase("y")); //Mientras el usuario introduzca en otraVisita, y o Y se ejecuta el bucle

        //Ahora creamos el array de LocalDate de la longitud con las fechas que se introducen por teclado
        LocalDate[] visitas = new LocalDate[fechasVisita.size()];
        //Lo recorremos y le introducimos las fechas del ArrayList
        for (int i=0; i<fechasVisita.size(); i++) {
            visitas[i] = fechasVisita.get(i);
        }

        //Instanciamos cliente y le introducimos los datos
        cliente = new Cliente(nombre, apellido1, apellido2, comercialPrincipal, idEmpresa, visitas);
        //Llamamos al método que inserta cliente
        clienteDAO.insertarCliente(cliente);
    }

    //Método para eliminar cliente
    public void eliminarCliente() {
        System.out.println("Has elegido eliminar cliente.");
        ClienteDAO clienteDAO = new ClienteDAO();
        Scanner in = new Scanner(System.in);

        //Mostramos clientes para que el usuario compruebe el id del que quiere eliminar
        clienteDAO.mostrarClientes();

        System.out.println("Introduce el 'id' del cliente que desea eliminar:");
        //Llamamos al método que comprueba entrada de enteros y almacenamos valor
        int id = comprobarEntrada();
        //Llamamos al método que elimina cliente de la clase con el CRUD
        clienteDAO.eliminarCliente(id);
    }

    //Método para insertar una visita a un determinado cliente
    public void insertarVisita() {
        System.out.println("Has elegido insertar nueva visita a cliente.");
        ClienteDAO clienteDAO = new ClienteDAO();
        Scanner in = new Scanner(System.in);

        //Muestro los clientes para que el usuario compruebe el id donde quiere insertar visita
        clienteDAO.mostrarClientes();

        String otraVisita = "";

        //Bucle para que el cliente elija si quiere añadir más visitas
        do {
            System.out.println("Introduce el 'id' del cliente que le quiere añadir visita:");
            //Llamamos al método que comprueba entrada de enteros y almacenamos en variable
            int id = comprobarEntrada();
            System.out.println("Introduce la fecha de visita que quiere añadir:");
            //Llamamos al método que comprueba la fecha y la almacenamos en su variable
            LocalDate visita = comprobarFechas();
            //Llamamos al metodo que inserta visita de la clase con el CRUD
            clienteDAO.insertarVisitas(id, visita);

            System.out.println("¿Quieres añadir otra visita a un cliente? Y/N");
            otraVisita = in.nextLine(); //Preguntamos si quiere añadir otra visita para comprobarlo en el while
        } while (otraVisita.equalsIgnoreCase("y")); //Mientras el usuario introduzca en otraVisita, y o Y se ejecuta el bucle
    }

    //Método para buscar un cliente, por el dato que decida el usuario
    public void buscarCliente() {
        System.out.println("Has elegido buscar cliente.");
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente;
        Scanner in = new Scanner(System.in);

        System.out.println("Pulsa el número de la opción que quieres buscar:\n" +
                "1 - Buscar por id de cliente.\n" +
                "2 - Buscar por nombre de cliente\n" +
                "3 - Buscar por primer apellido de cliente\n" +
                "4 - Buscar por segundo apellido de cliente\n" +
                "5 - Buscar por comercial principal de cliente\n" +
                "6 - Buscar por id de empresa del cliente\n" +
                "7 - Buscar por fecha de visita de cliente\n\n" +
                "0 - SALIR.");

        int opcionElegida = comprobarEntrada(); //Comprobamos la entrada de entero y almacenamos en su variable

        //Comprobamos la opción elegida
        switch (opcionElegida) {
            case 1: //Si elige la opción 1 preguntamos el id por el que quiere buscar
                System.out.println("Has elegido búsqueda por id de cliente.\n" +
                        "Introduce un id de cliente:");
                int idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                //Instanciamos un cliente introduciendole el valor del id solo, los demás nulos
                cliente = new Cliente(idCliente, null, null, null, null, -1, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 2: //Si elige la opción 2 preguntamos por el nombre del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por nombre de cliente.\n" +
                        "Introduce un nombre de cliente:");
                String nombre = in.nextLine(); //Introduce nombre por teclado
                //Instanciamos un cliente introduciendole el valor del nombre solo, los demás nulos
                cliente = new Cliente(-1, nombre, null, null, null, -1, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 3: //Si elige la opción 3 preguntamos por el primer apellido del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por primer apellido de cliente.\n" +
                        "Introduce un apellido de cliente:");
                String apellido1 = in.nextLine(); //Introduce apellido por teclado
                //Instanciamos un cliente introduciendole el valor del primer apellido solo, los demás nulos
                cliente = new Cliente(-1, null, apellido1, null, null, -1, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 4: //Si elige la opción 4 preguntamos por el segundo apellido del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por segundo apellido de cliente.\n" +
                        "Introduce un apellido de cliente:");
                String apellido2 = in.nextLine(); //Introduce apellido por teclado
                //Instanciamos un cliente introduciéndole el valor del segundo apellido solo, los demás nulos
                cliente = new Cliente(-1, null, null, apellido2, null, -1, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 5: //Si elige la opción 5 preguntamos por el nombre del comercial del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por comercial principal de cliente.\n" +
                        "Introduce comercial del cliente:");
                String comercialPrincipal = in.nextLine(); //Introduce nombre del comercial por teclado
                //Instanciamos un cliente introduciéndole el valor del comercial principal solo, los demás nulos
                cliente = new Cliente(-1, null, null, null, comercialPrincipal, -1, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 6: //Si elige la opción 6 preguntamos por el id de empresa del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por id de empresa de cliente.\n" +
                        "Introduce un id de empresa de cliente:");
                int idEmpresa = comprobarEntrada(); //Comprobamos la entrada de número entero
                //Instanciamos un cliente introduciéndole el valor del id de empresa solo, los demás nulos
                cliente = new Cliente(-1, null, null, null, null, idEmpresa, null);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 7: //Si elige la opción 7 preguntamos por fecha de visita del cliente que quiere buscar
                System.out.println("Has elegido búsqueda por fecha de visita de cliente.\n" +
                        "Introduce una fecha de visita de cliente (Entre 1980-01-01 y fecha actual):");
                LocalDate[] fechaVisita = new LocalDate[]{comprobarFechas()}; //Comprobamos la fecha con su método
                //Instanciamos un cliente introduciéndole el valor de la fecha solo, los demás nulos
                cliente = new Cliente(-1, null, null, null, null, -1, fechaVisita);
                clienteDAO.buscarCliente(cliente); //Llamamos al método buscar cliente de la clase con el CRUD
                break;
            case 0:
                System.exit(0);
                break;
            default:
                System.out.println("No ha insertado ninguna de las opciones propuestas.");
                buscarCliente(); //Llamamos de nuevo al método.
        }
    }

    public void modificarCliente() {
        System.out.println("Has elegido modificar un cliente.");
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente;
        Scanner in = new Scanner(System.in);

        //Mostramos clientes para que el usuario compruebe id del cliente que quiere modificar
        clienteDAO.mostrarClientes();

        System.out.println("\nPulsa el número de la opción modificar:\n" +
                "1 - Modificar nombre de un cliente.\n" +
                "2 - Modificar primer apellido de un cliente.\n" +
                "3 - Modificar segundo apellido de un cliente.\n" +
                "4 - Modificar comercial principal de un cliente.\n" +
                "5 - Modificar id de empresa de un cliente.\n" +
                "6 - Modificar fecha de visita a un cliente.\n\n" +
                "0 - SALIR.");

        int opcionElegida = comprobarEntrada(); //Comprobamos la entrada de entero y almacenamos en su variable
        int idCliente;
        //Comprobamos la opción elegida
        switch (opcionElegida) {
            case 1: //Si elige la opción 1 preguntamos el id y nombre que quiere modificar
                System.out.println("Has elegido modificar nombre de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Que nombre quieres ponerle al cliente:");
                String nombre = in.nextLine(); //Preguntamos el nombre que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id y nombre solo, los demás nulos
                cliente = new Cliente(idCliente, nombre, null, null, null, -1, null);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 2: //Si elige la opción 2 preguntamos por el id y primer apellido que quiere modificar
                System.out.println("Has elegido modificar primer apellido de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Que apellido quieres ponerle al cliente:");
                String apellido1 = in.nextLine(); //Preguntamos el primer apellido que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id y primer apellido solo, los demás nulos
                cliente = new Cliente(idCliente, null, apellido1, null, null, -1, null);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 3: //Si elige la opción 3 preguntamos id y segundo apellido que quiere modificar
                System.out.println("Has elegido modificar segundo apellido de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Que apellido quieres ponerle al cliente:");
                String apellido2 = in.nextLine(); //Preguntamos el segundo apellido que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id y segundo apellido solo, los demás nulos
                cliente = new Cliente(idCliente, null, null, apellido2, null, -1, null);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 4: //Si elige la opción 4 preguntamos id y comercial principal que quiere modificar
                System.out.println("Has elegido modificar comercial principal de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Que comercial principal quieres ponerle al cliente:");
                String comercialPrincipal = in.nextLine(); //Preguntamos el comercial principal que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id y comercial principal solo, los demás nulos
                cliente = new Cliente(idCliente, null, null, null, comercialPrincipal, -1, null);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 5: //Si elige la opción 5 preguntamos id de cliente e id de empresa
                System.out.println("Has elegido modificar id de empresa de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Que id de empresa quieres ponerle al cliente:");
                int idEmpresa = comprobarEntrada(); //Preguntamos el id de empresa que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id de cliente e id de empresa solo, los demás nulos
                cliente = new Cliente(idCliente, null, null, null, null, idEmpresa, null);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 6: //Si elige la opción 6 preguntamos id de cliente y fecha de visita
                System.out.println("Has elegido modificar fecha de visita de un cliente.\n" +
                        "Introduce el id del cliente:");
                idCliente = comprobarEntrada(); //Comprobamos la entrada del número entero
                System.out.println("Qué fecha de visita quieres modificar al cliente:");
                LocalDate localDateM = comprobarFechas();
                System.out.println("Qué fecha de visita quieres ponerle al cliente:");
                LocalDate[] localDate = new LocalDate[] {localDateM, comprobarFechas()}; //Preguntamos la fecha de visita que quiere ponerle al cliente
                //Instanciamos un cliente introduciéndole el valor del id de cliente e id de empresa solo, los demás nulos
                cliente = new Cliente(idCliente, null, null, null, null, -1, localDate);
                clienteDAO.modificarCliente(cliente); //Llamamos al método modificar cliente de la clase con el CRUD
                break;
            case 0:
                System.exit(0);
                break;
            default:
                System.out.println("No ha insertado ninguna de las opciones propuestas.");
                modificarCliente(); //Llamamos de nuevo al método.
        }
    }

    //Método para insertar fechas
    public LocalDate comprobarFechas() {
        Scanner in = new Scanner(System.in);

        System.out.println("Introduce el año:");
        int anio = comprobarEntrada(); //Comprobamos que introduce un entero
        System.out.println("Introduce el mes:");
        int mes = comprobarEntrada(); //Comprobamos que introduce un entero
        System.out.println("Introduce el día:");
        int dia = comprobarEntrada(); //Comprobamos que introduce un entero
        //Comprobamos que la fecha este en el rango de 1990-01-01 yla fecha actual si no volvemos a preguntar la fecha
        while ((anio<1990 || anio>LocalDate.now().getYear()) || (anio==LocalDate.now().getYear() && mes>LocalDate.now().getMonthValue())
                || (anio==LocalDate.now().getYear() && mes==LocalDate.now().getMonthValue() && dia>LocalDate.now().getDayOfMonth())) {
            System.out.println("Fecha incorrecta, vuelva a introducir la fecha:");
            System.out.println("Introduce el año:");
            anio = comprobarEntrada(); //Comprobamos que introduce un entero
            System.out.println("Introduce el mes:");
            mes = comprobarEntrada(); //Comprobamos que introduce un entero
            System.out.println("Introduce el día:");
            dia = comprobarEntrada(); //Comprobamos que introduce un entero
        }
        LocalDate fechaCorrecta = LocalDate.of(anio, mes, dia); //Almacenamos fecha en variable LocalDate
       return fechaCorrecta; //Retornamos la fecha
    }

    //Método para capturar errores en la entrada de datos de tipo entero y repetir si no se introduce un dato correcto
    public int comprobarEntrada() {
        Scanner in = new Scanner(System.in);
        int entrada = 0; //Inicializamos variable
        //Variable boleana para indicar cuando termina el bucle
        boolean repetir = true;
        //Bucle para repetir si hay excepción en la entrada del entero
        do {
            //try-catch para capturar excepción si no se introduce un entero
            try {
                entrada = in.nextInt(); //Pedimos entrada de entero por teclado
                repetir = false; //Cambiamos a false, no hay error y salimos del do-while
            } catch (InputMismatchException e) {
                in.nextLine(); //Para que nos deje volver a introducir valor y no se vuelva bucle infinito
                System.out.println("Tienes que introducir un número entero:");
            }
        } while (repetir);//Repetimos mientras 'repetir' sea true
        return entrada; //Retornamos el número entero introducido
    }
}
