package modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class GestorLibros {

    private SessionFactory sessionFactory;

    public GestorLibros(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Método para insertar un libro
    public boolean insertarLibro(Libro libro) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(libro); // Hibernate maneja la inserción automáticamente
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Libro> obtenerTodosLosLibros() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Libro";
            Query<Libro> query = session.createQuery(hql, Libro.class);
            List<Libro> libros = query.getResultList();

            if (libros == null) {  // Prevención de null
                System.out.println("La consulta no devolvió ningún libro.");
                return List.of(); // Devolver una lista vacía en lugar de null
            }

            System.out.println("Se encontraron " + libros.size() + " libros.");
            return libros;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // En caso de error, devolver una lista vacía en lugar de null
        }
    }


    // Método para actualizar un libro
    public boolean actualizarLibro(Libro libro) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(libro); // Hibernate maneja la actualización automáticamente
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar un libro
    public boolean eliminarLibro(int id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Libro libro = session.get(Libro.class, id); // Carga el libro a eliminar
            if (libro != null) {
                session.delete(libro); // Elimina el objeto
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para buscar un libro por ID
    public Libro buscarLibroPorId(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Libro.class, id); // Hibernate obtiene el libro de la base de datos
        }
    }

    // Método para buscar libros por título y autor
    public List<Libro> buscarLibrosPorTituloYAutor(String titulo, String autor) {
        try (Session session = sessionFactory.openSession()) {
            // Construir la consulta HQL con filtros dinámicos
            String hql = "FROM Libro WHERE LOWER(titulo) LIKE :titulo AND LOWER(autor) LIKE :autor";
            Query<Libro> query = session.createQuery(hql, Libro.class);
            query.setParameter("titulo", "%" + titulo.toLowerCase() + "%");
            query.setParameter("autor", "%" + autor.toLowerCase() + "%");

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
