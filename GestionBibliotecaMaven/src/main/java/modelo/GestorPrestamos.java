package modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class GestorPrestamos {
    private SessionFactory sessionFactory;

    // Constructor que recibe el SessionFactory
    public GestorPrestamos(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Método para insertar un préstamo
    public boolean insertarPrestamo(Prestamo prestamo) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate utilizando el SessionFactory proporcionado
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Inserta el préstamo
            session.save(prestamo);

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.getTransaction().rollback(); // Revertir si ocurre un error
            }
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
    }

    // Método para obtener todos los préstamos
    public List<Prestamo> obtenerTodosLosPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        Session session = null;
        try {
            // Abre una sesión de Hibernate utilizando el SessionFactory proporcionado
            session = sessionFactory.openSession();

            // HQL para obtener todos los préstamos
            String hql = "FROM Prestamo";
            Query<Prestamo> query = session.createQuery(hql, Prestamo.class);

            // Ejecuta la consulta y obtiene el resultado
            prestamos = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
        return prestamos;
    }

    // Método para actualizar un préstamo
    public boolean actualizarPrestamo(Prestamo prestamo) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate utilizando el SessionFactory proporcionado
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Actualiza el préstamo
            session.update(prestamo);

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.getTransaction().rollback(); // Revertir si ocurre un error
            }
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
    }

    // Método para eliminar un préstamo
    public boolean eliminarPrestamo(int id) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate utilizando el SessionFactory proporcionado
            session = sessionFactory.openSession();
            session.beginTransaction();

            // HQL para eliminar el préstamo
            String hql = "DELETE FROM Prestamo WHERE id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", id);

            // Ejecuta la eliminación
            int result = query.executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.getTransaction().rollback(); // Revertir si ocurre un error
            }
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
    }

    // Método para buscar un préstamo por ID
    public Prestamo buscarPrestamoPorId(int id) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate utilizando el SessionFactory proporcionado
            session = sessionFactory.openSession();

            // HQL para buscar un préstamo por ID
            String hql = "FROM Prestamo WHERE id = :id";
            Query<Prestamo> query = session.createQuery(hql, Prestamo.class);
            query.setParameter("id", id);

            // Ejecuta la consulta y devuelve el resultado
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
        return null;
    }
}
