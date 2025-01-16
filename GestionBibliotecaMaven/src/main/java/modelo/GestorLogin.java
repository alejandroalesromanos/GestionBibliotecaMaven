package modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import javax.swing.*;
import Vista.VistaLogin;
import modelo.Usuario.Rol;
import Vista.MenuPrincipal;

public class GestorLogin {

    private SessionFactory sessionFactory; // Para manejar la creación de sesiones

    // Constructor que recibe el SessionFactory
    public GestorLogin(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean validateCredentials(String email, String password, VistaLogin vista) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();

            // Utiliza HQL para buscar el usuario por email y contraseña
            String hql = "FROM Usuario WHERE email = :email AND password = :password";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            // Ejecuta la consulta
            Usuario usuario = query.uniqueResult();

            if (usuario != null) {
                // Si se encuentra el usuario, se obtiene la información
                Rol role = usuario.getRol();
                String emailUser = usuario.getEmail();
                String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidos();

                // Cierra la ventana de login y abre el menú principal según el rol
                vista.dispose();
                openMainMenu(role.equals("Administrador"), nombreCompleto, emailUser);
                return true;
            } else {
                // Si no se encuentra el usuario, muestra un mensaje de error
                vista.showErrorMessage("Credenciales incorrectas.");
                return false;
            }
        } catch (Exception e) {
            // Si ocurre un error al conectar a la base de datos, muestra un mensaje de error
            vista.showErrorMessage("Error al conectarse a la base de datos: " + e.getMessage());
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }

        // Código viejo con JDBC (comentado para referencia futura)
        /*
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT Nombre, Apellidos, Email, Rol FROM usuarios WHERE Email = ? AND password = ?")) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("Rol");
                    String emailUser = rs.getString("Email");
                    String nombreCompleto = rs.getString("Nombre") + " " + rs.getString("Apellidos");
                    
                    // Close the login window and open the main menu based on role
                    vista.dispose();
                    openMainMenu(role.equals("Administrador"), nombreCompleto, emailUser);
                    return true;
                } else {
                    vista.showErrorMessage("Credenciales incorrectas.");
                    return false;
                }
            }
        } catch (Exception e) {
            vista.showErrorMessage("Error al conectarse a la base de datos: " + e.getMessage());
            return false;
        }
        */
    }

    private void openMainMenu(boolean isAdmin, String nombreCompleto, String emailUser) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal(isAdmin, nombreCompleto, emailUser).setVisible(true));
    }
}
