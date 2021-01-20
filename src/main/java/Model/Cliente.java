package Model;

import java.time.LocalDate;
import java.util.Arrays;

public class Cliente {
    //Variables de cliente
    private int idCliente;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String comercialPrincipal;
    private int idEmpresa;
    private LocalDate[] visitas;

    //Método constructor con id que utilizo para mostrar clientes
    public Cliente(int idCliente, String nombre, String apellido1, String apellido2, String comercialPrincipal, int idEmpresa, LocalDate[] visitas) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.comercialPrincipal = comercialPrincipal;
        this.idEmpresa = idEmpresa;
        this.visitas = visitas;
    }

    //Método constructor sin id, que utilizo para insertar clientes ya que el id_cliente de PostgreSQL es autoincrement
    public Cliente(String nombre, String apellido1, String apellido2, String comercialPrincipal, int idEmpresa, LocalDate[] visitas) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.comercialPrincipal = comercialPrincipal;
        this.idEmpresa = idEmpresa;
        this.visitas = visitas;
    }

    //Getter y Setter
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getComercialPrincipal() {
        return comercialPrincipal;
    }

    public void setComercialPrincipal(String comercialPrincipal) {
        this.comercialPrincipal = comercialPrincipal;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public LocalDate[] getVisitas() {
        return visitas;
    }

    public void setVisitas(LocalDate[] visitas) {
        this.visitas = visitas;
    }

    //Método toString() para imprimir por pantalla.
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Cliente: ");
        sb.append("id=").append(idCliente);
        sb.append(", Nombre='").append(nombre).append('\'');
        sb.append(", 1º Apellido='").append(apellido1).append('\'');
        sb.append(", 2º Apellido='").append(apellido2).append('\'');
        sb.append(", Comercial Principal='").append(comercialPrincipal).append('\'');
        sb.append(", id Empresa=").append(idEmpresa);
        sb.append(", Visitas=").append(visitas == null ? "null" : Arrays.asList(visitas).toString());
        return sb.toString();
    }
}
