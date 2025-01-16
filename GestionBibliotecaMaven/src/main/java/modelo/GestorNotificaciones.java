package modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class GestorNotificaciones {
    private SessionFactory sessionFactory; // Para manejar la creación de sesiones

    // Constructor que recibe el SessionFactory
    public GestorNotificaciones(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Mostrar notificaciones según el usuario
    public List<Notificacion> mostrarNotificaciones(int idUsuario) {
        List<Notificacion> notificaciones = new ArrayList<>();
        Session session = null;
        try {
            // Abre una sesión de Hibernate
            session = sessionFactory.openSession();

            // HQL para obtener las notificaciones del usuario
            String hql = "FROM Notificacion WHERE idUsuario = :idUsuario";
            Query<Notificacion> query = session.createQuery(hql, Notificacion.class);
            query.setParameter("idUsuario", idUsuario);

            // Ejecuta la consulta y obtiene el resultado
            notificaciones = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }

        return notificaciones;

        // Código viejo con JDBC (comentado para referencia futura)
        /*
        String sql = "SELECT * FROM Notificaciones WHERE ID_Usuario = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notificacion notificacion = new Notificacion(
                        rs.getInt("ID"),
                        rs.getString("Mensaje"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Libro"),
                        rs.getDate("Fecha"),
                        rs.getBoolean("Leido")
                    );
                    notificaciones.add(notificacion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificaciones;
        */
    }

    // Borrar notificaciones según el usuario
    public boolean borrarNotificaciones(int idUsuario) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate
            session = sessionFactory.openSession();
            session.beginTransaction();

            // HQL para eliminar las notificaciones del usuario
            String hql = "DELETE FROM Notificacion WHERE idUsuario = :idUsuario";
            Query query = session.createQuery(hql);
            query.setParameter("idUsuario", idUsuario);

            // Ejecuta la eliminación
            int result = query.executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }

        // Código viejo con JDBC (comentado para referencia futura)
        /*
        String sql = "DELETE FROM Notificaciones WHERE ID_Usuario = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        */
    }

    // Marcar notificaciones como leídas
    public boolean marcarNotificacionesLeidas(int idUsuario) {
        Session session = null;
        try {
            // Abre una sesión de Hibernate
            session = sessionFactory.openSession();
            session.beginTransaction();

            // HQL para actualizar el estado de "Leido"
            String hql = "UPDATE Notificacion SET leido = true WHERE idUsuario = :idUsuario";
            Query query = session.createQuery(hql);
            query.setParameter("idUsuario", idUsuario);

            // Ejecuta la actualización
            int result = query.executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }

        // Código viejo con JDBC (comentado para referencia futura)
        /*
        String sql = "UPDATE Notificaciones SET Leido = true WHERE ID_Usuario = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        */
    }
}
