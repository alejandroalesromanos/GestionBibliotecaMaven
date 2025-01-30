package modelo;

import java.sql.Date;

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
        Fantasia,
        Ciencia_Ficcion,
        Terror,
        Misterio,
        Ficción,
        Aventura,
        Historico,
        Biografia,
        Poesía,
        Drama,
        Novela,
        Realismo,
        Distopía
    }

    @Id 
    @Column(name = "id") 
    private int id;

    @Column(name = "titulo") 
    private String titulo;

    @Column(name = "autor") 
    private String autor;

    @Enumerated(EnumType.STRING) 
    @Column(name = "genero") 
    private Generos genero;

    @Column(name = "disponibilidad") 
    private boolean disponibilidad;

    @Column(name = "Fecha_Publicacion") 
    private Date Fecha_Publicacion; // Cambio aquí a java.sql.Date

    public Libro() {
    }

    public Libro(int id, String titulo, String autor, Generos genero, boolean disponibilidad, Date Fecha_Publicacion) {
        super();
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.disponibilidad = disponibilidad;
        this.Fecha_Publicacion = Fecha_Publicacion;
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
        return Fecha_Publicacion;
    }

    public void setFechaDePublicacion(Date Fecha_Publicacion) {
        this.Fecha_Publicacion = Fecha_Publicacion;
    }
}
