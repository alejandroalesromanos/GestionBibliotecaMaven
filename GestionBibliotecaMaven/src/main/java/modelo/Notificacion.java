package modelo;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "id_libro")
    private int idLibro;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "leida")
    private boolean leida;

    // Constructor por defecto, necesario para Hibernate
    public Notificacion() {
    }

    // Constructor con parámetros
    public Notificacion(int id, String mensaje, int idUsuario, int idLibro, Date fecha, boolean leida) {
        this.id = id;
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
        this.idLibro = idLibro;
        this.fecha = fecha;
        this.leida = leida;
    }

    // Métodos getter y setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}
