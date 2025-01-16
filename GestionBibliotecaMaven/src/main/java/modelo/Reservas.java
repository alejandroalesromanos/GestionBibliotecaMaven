package modelo;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "reservas")
public class Reservas {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "id_libro")
    private int idLibro;

    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(name = "fecha_reserva")
    private Date fechaReserva;

    @Column(name = "disponible")
    private boolean disponible;

    // Constructor por defecto, necesario para Hibernate
    public Reservas() {
    }

    // Constructor con parámetros
    public Reservas(int id, int idLibro, int idUsuario, Date fechaReserva, boolean disponible) {
        this.id = id;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.fechaReserva = fechaReserva;
        this.disponible = disponible;
    }

    // Métodos getter y setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
