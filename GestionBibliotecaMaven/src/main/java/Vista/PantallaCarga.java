package Vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import modelo.GestorLogin;

public class PantallaCarga extends JFrame {

	private JLabel loadingLabel;

    public PantallaCarga() {
        setTitle("Cargando...");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // No cerrar al hacer clic en la X
        
        // Layout
        setLayout(new BorderLayout());
        
        loadingLabel = new JLabel("Cargando...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(loadingLabel, BorderLayout.CENTER);
        
        // Hacer visible el frame
        setVisible(true);
    }

    public void executeLoginTask(String email, String password, VistaLogin vista, GestorLogin gestor) {
        // Crear un SwingWorker para ejecutar la validación de las credenciales
        SwingWorker<Boolean, Void> loginTask = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                // Aquí llamamos al método validateCredentials que puede tomar tiempo
                return gestor.validateCredentials(email, password, vista);
            }

            @Override
            protected void done() {
                try {
                    // Verifica si la tarea fue exitosa
                    if (get()) {
                        // Si fue exitosa, se cierra el frame de carga
                        dispose();
                    } else {
                        // Si no, muestra un mensaje de error (esto depende de la lógica)
                        JOptionPane.showMessageDialog(null, "Error al iniciar sesión.");
                        dispose();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al realizar la operación.");
                    dispose();
                }
            }
        };
        
        // Iniciar la tarea en segundo plano
        loginTask.execute();
    }
}