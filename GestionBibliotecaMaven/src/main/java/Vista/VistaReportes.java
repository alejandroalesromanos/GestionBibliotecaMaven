package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class VistaReportes extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel reportPanel;

    public VistaReportes(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Generador de Reportes BIRT");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Fondo personalizado
        JPanel fondoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(0, 0, new Color(41, 128, 185), 0, height,
                        new Color(109, 213, 250));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
            }
        };
        fondoPanel.setLayout(new BorderLayout());
        fondoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(fondoPanel);

        // Panel de botones a la derecha
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Botones para los reportes
        String[] reportFiles = {"informe_libros_mas_prestados.rptdesign", "informemax.rptdesign", "informeprueba.rptdesign", "informe4.rptdesign"};
        for (int i = 0; i < reportFiles.length; i++) {
            String reportFile = reportFiles[i];
            final int reportIndex = i; // Crear una copia de 'i'
            JButton reportButton = new StyledButton("Generar Reporte " + (reportIndex + 1));
            reportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateReport(reportFile, "Reporte " + (reportIndex + 1));
                }
            });
            buttonPanel.add(reportButton);
        }

        JButton backButton = new StyledButton("Volver al Menú Principal");
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
        });
        buttonPanel.add(backButton);

        fondoPanel.add(buttonPanel, BorderLayout.EAST);

        // Panel central para la vista previa del reporte
        reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        fondoPanel.add(reportPanel, BorderLayout.CENTER);
    }

    private void generateReport(String reportFileName, String reportTitle) {
        String userHome = System.getProperty("user.home");
        String reportsFolder = userHome + File.separator + "Desktop" + File.separator + "Reportes";
        String outputPath = reportsFolder + File.separator + reportFileName.replace(".rptdesign", ".pdf");

        // Obtener la ruta del recurso dentro de src/main/resources
        URL resourceUrl = getClass().getClassLoader().getResource(reportFileName);
        if (resourceUrl == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el archivo: " + reportFileName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String reportPath;
        try {
            reportPath = Paths.get(resourceUrl.toURI()).toString();
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener la ruta del reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Crear la carpeta "Reportes" si no existe
            File folder = new File(reportsFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Ejecutar el comando de BIRT para generar el reporte
            String[] command = {
                    "cmd", "/c", "C:\\path\\to\\birt-runtime\\birt-runtime\\ReportEngine\\bin\\rptview",
                    "-f", "pdf", "-o", outputPath, "-r", reportPath
            };
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO(); // Esto hace que la salida del proceso se vea en la consola
            Process process = builder.start();
            process.waitFor();

            // Notificar al usuario
            JOptionPane.showMessageDialog(this, reportTitle + " generado exitosamente en:\n" + outputPath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this, "Error al generar " + reportTitle + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class StyledButton extends JButton {
        public StyledButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBackground(new Color(52, 152, 219));
            setBorderPainted(true);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setPreferredSize(new Dimension(250, 40));
            setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(41, 128, 185));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(52, 152, 219));
                }
            });
        }
    }
}
