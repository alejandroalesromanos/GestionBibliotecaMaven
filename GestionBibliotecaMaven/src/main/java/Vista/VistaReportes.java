package Vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;

public class VistaReportes extends JFrame {
    private JEditorPane editorPane;
    private String currentOutputFilePath; // Variable para almacenar la ruta del archivo generado

    public VistaReportes(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Visor de Informes BIRT");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo personalizado
        JPanel fondoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(0, 0, new Color(41, 128, 185), 0, height, new Color(109, 213, 250));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
            }
        };
        fondoPanel.setLayout(new BorderLayout());
        fondoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(fondoPanel);

        // Título de la ventana
        JLabel titleLabel = new JLabel("Visor de Informes BIRT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        fondoPanel.add(titleLabel, BorderLayout.NORTH);

        // Editor Pane para mostrar el informe
        editorPane = new JEditorPane();
        editorPane.setContentType("text/html"); // Asegurar que el JEditorPane soporte HTML
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        fondoPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Custom button style
        class StyledButton extends JButton {
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
                        setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        setBackground(new Color(52, 152, 219));
                        setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(new Color(31, 97, 141));
                } else {
                    g.setColor(getBackground());
                }
                g.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                super.paintComponent(g);
            }
        }

        // Botones para generar diferentes reportes
        JButton reporte1Button = new StyledButton("Reporte 1: Libros más prestados");
        reporte1Button.addActionListener(e -> generateAndLoadReport("informe_libros_mas_prestados.rptdesign"));
        buttonPanel.add(reporte1Button);

        JButton reporte2Button = new StyledButton("Reporte 2: Libros Populares");
        reporte2Button.addActionListener(e -> generateAndLoadReport("Informe_libros_populares.rptdesign"));
        buttonPanel.add(reporte2Button);

        JButton reporte3Button = new StyledButton("Reporte 3: Libros por rango de fechas");
        reporte3Button.addActionListener(e -> generateAndLoadReport("Informe_libros_rango_fechas.rptdesign"));
        buttonPanel.add(reporte3Button);

        JButton reporte4Button = new StyledButton("Reporte 4: Informe de Multas");
        reporte4Button.addActionListener(e -> generateAndLoadReport("informe_Multas.rptdesign"));
        buttonPanel.add(reporte4Button);

        // Botón para guardar el informe
        JButton saveButton = new StyledButton("Guardar Informe");
        saveButton.addActionListener(e -> saveReport());
        buttonPanel.add(saveButton);

        JButton backButton = new StyledButton("Volver al Menú Principal");
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
        });
        buttonPanel.add(backButton);

        fondoPanel.add(buttonPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void generateAndLoadReport(String reportName) {
        IReportEngine engine = null;

        try {
            // Configuración del motor BIRT
            EngineConfig config = new EngineConfig();
            Platform.startup(config);

            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = factory.createReportEngine(config);

            // Ruta del informe
            String reportPath = "C:\\Users\\aleja\\git\\GestionBibliotecaMaven\\GestionBibliotecaMaven\\src\\main\\java\\reports\\" + reportName;

            // Abre el informe
            IReportRunnable report = engine.openReportDesign(reportPath);

            // Crear tarea para ejecutar y renderizar
            IRunAndRenderTask task = engine.createRunAndRenderTask(report);

            // Generar un nombre de archivo único basado en el nombre del informe
            currentOutputFilePath = "C:\\Users\\aleja\\git\\GestionBibliotecaMaven\\GestionBibliotecaMaven\\src\\main\\java\\reports\\output_" 
                                    + reportName.replace(".rptdesign", ".html");

            // Opciones de renderización en HTML
            HTMLRenderOption options = new HTMLRenderOption();
            options.setOutputFileName(currentOutputFilePath);
            options.setOutputFormat("html");

            // Configurar las opciones en la tarea
            task.setRenderOption(options);

            // Ejecutar para crear el archivo HTML
            task.run();

            // Finalizar la tarea
            task.close();

            // Cargar el informe generado en el editor
            File htmlFile = new File(currentOutputFilePath);
            editorPane.setPage(htmlFile.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Asegurarse de liberar los recursos del motor BIRT
            if (engine != null) {
                engine.destroy();
            }
            // Apagar la plataforma BIRT
            Platform.shutdown();
        }
    }

    private void saveReport() {
        if (currentOutputFilePath == null || currentOutputFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay un informe generado para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Informe");
        fileChooser.setSelectedFile(new File("informe.html"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Files.copy(new File(currentOutputFilePath).toPath(), fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Informe guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el informe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}