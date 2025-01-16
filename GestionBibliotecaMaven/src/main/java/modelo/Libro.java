package modelo;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "libros") 
public class Libro {

    public enum Generos {
        FANTASIA,
        CIENCIA_FICCION,
        TERROR,
        MISTERIO,
        ROMANCE,
        AVENTURA,
        HISTORICO,
        BIOGRAFIA,
        POESIA,
        DRAMA,
    }

    @Id 
    @Column(name = "id") // Especifica el nombre de la columna en la base de datos (opcional)
    private int id;

    @Column(name = "titulo") // Asocia el atributo con la columna 'titulo' en la tabla
    private String titulo;

    @Column(name = "autor") // Asocia el atributo con la columna 'autor' en la tabla
    private String autor;

    @Enumerated(EnumType.STRING) // Especifica cómo se debe almacenar el valor del enum
    @Column(name = "genero") // Asocia el atributo con la columna 'genero' en la tabla
    private Generos genero;

    @Column(name = "disponibilidad") // Asocia el atributo con la columna 'disponibilidad' en la tabla
    private boolean disponibilidad;

    @Column(name = "fechaDePublicacion") // Asocia el atributo con la columna 'fechaDePublicacion' en la tabla
    private Date fechaDePublicacion;

    // Constructor por defecto, necesario para Hibernate
    public Libro() {
    }

    // Constructor con parámetros
    public Libro(int id, String titulo, String autor, Generos genero, boolean disponibilidad, Date fechaDePublicacion) {
        super();
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.disponibilidad = disponibilidad;
        this.fechaDePublicacion = fechaDePublicacion;
    }

    // Métodos getter y setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Generos getGenero() {
        return genero;
    }

    public void setGenero(Generos genero) {
        this.genero = genero;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Date getFechaDePublicacion() {
        return fechaDePublicacion;
    }

    public void setFechaDePublicacion(Date fechaDePublicacion) {
        this.fechaDePublicacion = fechaDePublicacion;
    }

}
