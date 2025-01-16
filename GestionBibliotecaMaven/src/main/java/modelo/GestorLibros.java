package modelo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class GestorLibros {

    private Session session;

    public GestorLibros(Session session) {
        this.session = session;
    }

    // Método para insertar un libro
    public boolean insertarLibro(Libro libro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(libro); // Hibernate maneja la inserción automáticamente
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // En caso de error, se revierte la transacción
            }
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener todos los libros
    public List<Libro> obtenerTodosLosLibros() {
        String hql = "FROM Libro"; // HQL es el lenguaje de consultas de Hibernate
        Query<Libro> query = session.createQuery(hql, Libro.class);
        return query.list();
    }

    // Método para actualizar un libro
    public boolean actualizarLibro(Libro libro) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(libro); // Hibernate maneja la actualización automáticamente
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // En caso de error, se revierte la transacción
            }
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar un libro
    public boolean eliminarLibro(int id) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Libro libro = session.get(Libro.class, id); // Carga el libro a eliminar
            if (libro != null) {
                session.delete(libro); // Elimina el objeto
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // En caso de error, se revierte la transacción
            }
            e.printStackTrace();
            return false;
        }
    }

    // Método para buscar un libro por ID
    public Libro buscarLibroPorId(int id) {
        return session.get(Libro.class, id); // Hibernate obtiene el libro de la base de datos
    }
}
