package Vista;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VistaReportes extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel reportPanel;

    public VistaReportes(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Generador de Reportes Jasper");
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

        JButton generateButton = new StyledButton("Generar Reporte");
        generateButton.addActionListener(e -> generateReport());
        buttonPanel.add(generateButton);

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

    private void generateReport() {
        // Construir la ruta al escritorio y la carpeta "Reportes"
        String userHome = System.getProperty("user.home");
        String reportsFolder = userHome + File.separator + "Desktop" + File.separator + "Reportes";
        String desktopPath = reportsFolder + File.separator + "reporte.pdf";
        String reportPath = "C:\\Users\\ikasle\\Documents\\reportes\\reporte.jrxml"; // Cambiar a la ruta real

        try {
            // Verificar o crear la carpeta "Reportes"
            File folder = new File(reportsFolder);
            if (!folder.exists()) {
                folder.mkdirs(); // Crear la carpeta si no existe
            }

            // Verificar si el archivo JRXML existe o está vacío, y crearlo con contenido básico si es necesario
            File reportFile = new File(reportPath);
            if (!reportFile.exists() || reportFile.length() == 0) {
                createDefaultJRXML(reportPath); // Crear el archivo JRXML con contenido básico
            }

            // Cargar el archivo JRXML
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Datos ficticios de ejemplo para el reporte
            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("name", "Juan Pérez");
            row.put("age", 30);
            data.add(row);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Rellenar el reporte con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            // Exportar el reporte a PDF en la ruta definida
            JasperExportManager.exportReportToPdfFile(jasperPrint, desktopPath);

            // Mostrar la vista previa en el panel
            showReportPreview(jasperPrint);

            // Notificar al usuario
            JOptionPane.showMessageDialog(this, "Reporte generado exitosamente en:\n" + desktopPath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createDefaultJRXML(String reportPath) {
        String defaultJRXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"" +
                "http://jasperreports.sourceforge.net/jasperreports " +
                "http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" " +
                "name=\"reporte\" pageWidth=\"595\" pageHeight=\"842\" columnWidth=\"515\" leftMargin=\"40\" " +
                "rightMargin=\"40\" topMargin=\"50\" bottomMargin=\"50\">\n" +
                "   <queryString language=\"SQL\">\n" +
                "      <![CDATA[SELECT * FROM table_name]]>\n" +
                "   </queryString>\n" +
                "   <field name=\"name\" class=\"java.lang.String\"/>\n" +
                "   <field name=\"age\" class=\"java.lang.Integer\"/>\n" +
                "   <title>\n" +
                "      <band height=\"50\">\n" +
                "         <staticText>\n" +
                "            <reportElement x=\"0\" y=\"0\" width=\"100\" height=\"20\"/>\n" +
                "            <textElement/>\n" +
                "            <text><![CDATA[Reporte de Usuarios]]></text>\n" +
                "         </staticText>\n" +
                "      </band>\n" +
                "   </title>\n" +
                "   <detail>\n" +
                "      <band height=\"20\">\n" +
                "         <textField>\n" +
                "            <reportElement x=\"0\" y=\"0\" width=\"100\" height=\"20\"/>\n" +
                "            <textElement/>\n" +
                "            <textFieldExpression><![CDATA[$F{name}]]></textFieldExpression>\n" +
                "         </textField>\n" +
                "         <textField>\n" +
                "            <reportElement x=\"120\" y=\"0\" width=\"100\" height=\"20\"/>\n" +
                "            <textElement/>\n" +
                "            <textFieldExpression><![CDATA[$F{age}]]></textFieldExpression>\n" +
                "         </textField>\n" +
                "      </band>\n" +
                "   </detail>\n" +
                "</jasperReport>";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportPath))) {
            writer.write(defaultJRXML);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el archivo JRXML: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReportPreview(JasperPrint jasperPrint) {
        reportPanel.removeAll();

        JRViewer viewer = new JRViewer(jasperPrint);
        reportPanel.add(viewer, BorderLayout.CENTER);

        reportPanel.revalidate();
        reportPanel.repaint();
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
