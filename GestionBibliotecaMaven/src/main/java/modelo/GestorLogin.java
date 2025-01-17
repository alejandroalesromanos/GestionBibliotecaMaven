package modelo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import javax.swing.*;
import Vista.VistaLogin;
import modelo.Usuario.Rol;
import Vista.MenuPrincipal;

public class GestorLogin {

    private SessionFactory sessionFactory; // Para manejar la creación de sesiones

    // Constructor que inicializa el SessionFactory
    public GestorLogin() {
        try {
            // Configura y construye el SessionFactory
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Error al configurar Hibernate: " + e.getMessage(), e);
        }
    }

    // Método para validar credenciales usando un SwingWorker para no bloquear la UI
    public void validateCredentialsAsync(String email, String password, VistaLogin vista) {
        // Creamos un SwingWorker para manejar la validación de credenciales en segundo plano
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return validateCredentials(email, password, vista);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        // Si las credenciales son correctas, ya se realizó la transición en el hilo de fondo
                    } else {
                        // Si las credenciales son incorrectas, ya se mostró el mensaje de error
                    }
                } catch (Exception e) {
                    vista.showErrorMessage("Error inesperado.");
                }
            }
        };
        worker.execute();  // Ejecuta el SwingWorker
    }

    // Método de validación de credenciales
    public boolean validateCredentials(String email, String password, VistaLogin vista) {
        Session session = null;
        try {
            // Crear una sesión a partir del SessionFactory
            session = sessionFactory.openSession();

            // Utiliza HQL para buscar el usuario por email y contraseña
            String hql = "FROM Usuario u WHERE u.email = :email AND u.password = :password";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            // Ejecuta la consulta
            Usuario usuario = query.uniqueResult();

            if (usuario != null) {
                // Si se encuentra el usuario, se obtiene la información
                Rol role = usuario.getRol(); // Suponiendo que getRol() retorna un enum o cadena
                String emailUser = usuario.getEmail();
                String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidos();

                // Cierra la ventana de login y abre el menú principal según el rol
                SwingUtilities.invokeLater(() -> {
                    vista.dispose();
                    openMainMenu(role.name().equalsIgnoreCase("Administrador"), nombreCompleto, emailUser);
                });
                return true;
            } else {
                // Si no se encuentra el usuario, muestra un mensaje de error
                SwingUtilities.invokeLater(() -> vista.showErrorMessage("Credenciales incorrectas."));
                return false;
            }
        } catch (Exception e) {
            // Si ocurre un error al conectar a la base de datos, muestra un mensaje de error
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> vista.showErrorMessage("Error al conectarse a la base de datos: " + e.getMessage()));
            return false;
        } finally {
            if (session != null) {
                session.close(); // Cierra la sesión de Hibernate
            }
        }
    }

    // Método para abrir el menú principal
    public void openMainMenu(boolean isAdmin, String nombreCompleto, String emailUser) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal(isAdmin, nombreCompleto, emailUser).setVisible(true));
    }

    // Método para cerrar el SessionFactory al finalizar la aplicación
    public void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
