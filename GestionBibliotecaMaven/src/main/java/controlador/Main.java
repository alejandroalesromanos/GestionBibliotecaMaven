package controlador;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

                    // Ejecutar la actualización de multas cada 24 horas (86400 segundos)
                    scheduler.scheduleAtFixedRate(() -> {
                        actualizarMultas();  // Llama al método de actualización de multas
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
}
