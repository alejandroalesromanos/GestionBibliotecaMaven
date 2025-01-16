package modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

public class GestorUsuarios {

    private SessionFactory sessionFactory; // Para manejar la creación de sesiones

    // Constructor que recibe el SessionFactory
    public GestorUsuarios(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Método para insertar un usuario en la base de datos
    public boolean insertarUsuario(Usuario usuario) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Guarda el usuario
            session.save(usuario);

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

    // Método para obtener todos los usuarios de la base de datos
    public List<Usuario> obtenerTodosLosUsuarios() {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();

            // Consulta HQL para obtener todos los usuarios
            String hql = "FROM Usuario";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);

            // Ejecuta la consulta y obtiene la lista de usuarios
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
        return null;
    }

    // Método para actualizar un usuario existente en la base de datos
    public boolean actualizarUsuario(Usuario usuario) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Actualiza el usuario
            session.update(usuario);

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

    // Método para eliminar un usuario por su ID
    public boolean eliminarUsuario(int id) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();
            session.beginTransaction();

            // HQL para eliminar un usuario por ID
            String hql = "DELETE FROM Usuario WHERE id = :id";
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

    // Método para buscar un usuario por su ID
    public Usuario buscarUsuarioPorId(int id) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();

            // Consulta HQL para buscar un usuario por ID
            String hql = "FROM Usuario WHERE id = :id";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
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

    // Método para autenticar un usuario por nombre y contraseña
    public boolean autenticarUsuario(String nombre, String password) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();

            // HQL para autenticar un usuario
            String hql = "SELECT COUNT(*) FROM Usuario WHERE nombre = :nombre AND password = :password";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("nombre", nombre);
            query.setParameter("password", password);

            // Ejecuta la consulta y devuelve si el usuario existe
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
        return false;
    }
}
