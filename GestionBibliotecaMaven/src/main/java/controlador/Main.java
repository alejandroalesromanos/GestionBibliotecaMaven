package controlador;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import Vista.VistaLogin;
import modelo.Db;

public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Configurar la ventana de inicio de sesión
                    VistaLogin frame = new VistaLogin();
                    frame.setSize(700, 500);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int x = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
                    int y = (int) (screenSize.getHeight() - frame.getHeight()) / 2;
                    frame.setLocation(x, y);

                    frame.setVisible(true);

                    // Ejecutar la actualización de multas en un hilo programado
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                    // Ejecutar la actualización de multas y el envío de notificaciones cada 24 horas (86400 segundos)
                    scheduler.scheduleAtFixedRate(() -> {
                        actualizarMultas();  // Llama al método de actualización de multas
                        sendDailyNotifications("currentUser");  // Método para enviar notificaciones diarias sobre multas y días restantes
                    }, 0, 24, TimeUnit.HOURS);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Método para actualizar las multas de todos los préstamos
    public static void actualizarMultas() {
        try (Connection connection = new Db().getConnection()) {
            // Actualizar las multas para los préstamos no devueltos
            String query = "UPDATE prestamos SET Multa_Generada = " +
                           "(CASE " +
                           "  WHEN DATEDIFF(CURDATE(), Fecha_Prestamo) > 15 THEN (DATEDIFF(CURDATE(), Fecha_Prestamo) - 15) * 2 " +
                           "  ELSE 0 " +
                           "END) " +
                           "WHERE Fecha_Devolucion IS NULL";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int rowsUpdated = statement.executeUpdate();
                System.out.println("Se han actualizado " + rowsUpdated + " préstamos con multas.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar las multas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para enviar notificaciones diarias sobre los días restantes antes de la multa
    public static void sendDailyNotifications(String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT libros.ID, libros.Titulo, prestamos.Fecha_Devolucion, prestamos.Fecha_Prestamo "
                     + "FROM prestamos "
                     + "JOIN libros ON prestamos.id_libro = libros.ID "
                     + "WHERE prestamos.id_usuario = ?")) {
            ps.setString(1, currentUser);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int libroId = resultSet.getInt("ID");
                String titulo = resultSet.getString("Titulo");
                LocalDate fechaDevolucion = resultSet.getDate("Fecha_Devolucion") != null 
                    ? resultSet.getDate("Fecha_Devolucion").toLocalDate() 
                    : null;
                LocalDate fechaPrestamo = resultSet.getDate("Fecha_Prestamo").toLocalDate();
                LocalDate hoy = LocalDate.now();

                // Verificar si el libro está disponible para la cola de espera
                if (libroEstaDisponibleParaCola(libroId)) {
                    addNotification("El libro " + titulo + " ya está disponible. Puede retirarlo.", hoy.toString(), currentUser);
                }

                // Notificación sobre días restantes para la devolución
                if (fechaDevolucion == null) {
                    long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaPrestamo.plusDays(15)); // Los 15 días de préstamo

                    if (diasRestantes > 0) {
                        addNotification("Te quedan " + diasRestantes + " días para devolver el libro: " + titulo, hoy.toString(), currentUser);
                    } else {
                        // Notificación de multa después del límite
                        long diasMulta = Math.abs(diasRestantes);
                        addNotification("¡ALERTA! Tu libro '" + titulo + "' está retrasado por " + diasMulta + " días. La multa es de " 
                                + (diasMulta * 2) + " unidades.", hoy.toString(), currentUser);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al enviar notificaciones diarias: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Función para verificar si el libro está disponible en la cola de espera
    private static boolean libroEstaDisponibleParaCola(int libroId) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT COUNT(*) FROM cola_reserva WHERE id_libro = ? AND estado = 'pendiente'")) {
            ps.setInt(1, libroId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                // Si la cola está vacía, el libro está disponible
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para agregar notificaciones a la base de datos
    private static void addNotification(String mensaje, String fecha, String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO notificaciones (Mensaje, Fecha_Notificacion, id_usuario) VALUES (?, ?, ?)")) {
            ps.setString(1, mensaje);
            ps.setString(2, fecha);
            ps.setString(3, currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al añadir notificación: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
