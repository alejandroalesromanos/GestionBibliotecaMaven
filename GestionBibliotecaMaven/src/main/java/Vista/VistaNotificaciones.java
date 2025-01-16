package Vista;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Db;

public class VistaNotificaciones extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable notificationsTable;
    private DefaultTableModel tableModel;

    public VistaNotificaciones(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Vista de Notificaciones");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo personalizado con gradiente
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
        JLabel titleLabel = new JLabel("Lista de Notificaciones", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(false);
        fondoPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de notificaciones
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Mensaje", "Fecha"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        notificationsTable = new JTable(tableModel);
        notificationsTable.setRowHeight(25);
        notificationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        notificationsTable.getTableHeader().setBackground(new Color(41, 128, 185));
        notificationsTable.getTableHeader().setForeground(Color.WHITE);
        notificationsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Fondo de la tabla
        notificationsTable.setBackground(Color.WHITE);
        notificationsTable.setOpaque(true);
        notificationsTable.setFillsViewportHeight(true);
        notificationsTable.setGridColor(new Color(200, 200, 200));
        notificationsTable.setShowHorizontalLines(true);
        notificationsTable.setShowVerticalLines(true);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fondoPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton allNotificationsButton = createStyledButton("Mostrar Todas las Notificaciones", e -> loadNotifications(currentUser));
        buttonPanel.add(allNotificationsButton);

        JButton unreadNotificationsButton = createStyledButton("Mostrar Notificaciones No Leídas", e -> loadUnreadNotifications(currentUser));
        buttonPanel.add(unreadNotificationsButton);



        JButton backButton = createStyledButton("Volver al Menú Principal", e -> {
            dispose();
            new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
        });
        buttonPanel.add(backButton);

        fondoPanel.add(buttonPanel, BorderLayout.EAST);

        // Cargar notificaciones al inicio
        loadNotifications(currentUser);
        sendNotifications(currentUser);
    }

    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(250, 40));
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        button.addActionListener(action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
                button.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
            }
        });
        return button;
    }

    private void loadNotifications(String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT ID, Mensaje, Fecha_Notificacion FROM notificaciones WHERE id_usuario = ?")) {
            ps.setString(1, currentUser);
            ResultSet resultSet = ps.executeQuery();

            tableModel.setRowCount(0); // Limpiar la tabla
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String mensaje = resultSet.getString("Mensaje");
                String fecha = resultSet.getString("Fecha_Notificacion");
                tableModel.addRow(new Object[]{id, mensaje, fecha});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar notificaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUnreadNotifications(String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT ID, Mensaje, Fecha_Notificacion FROM notificaciones WHERE id_usuario = ?")) {
            ps.setString(1, currentUser);
            ResultSet resultSet = ps.executeQuery();

            tableModel.setRowCount(0); // Limpiar la tabla
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String mensaje = resultSet.getString("Mensaje");
                String fecha = resultSet.getString("Fecha_Notificacion");
                tableModel.addRow(new Object[]{id, mensaje, fecha});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar notificaciones no leídas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markNotificationAsRead() {
        int selectedRow = notificationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una notificación de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

      
    }

    private void sendNotifications(String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT libros.ID, libros.Titulo, prestamos.Fecha_Devolucion " +
                     "FROM prestamos " +
                     "JOIN libros ON prestamos.id_libro = libros.ID " +
                     "WHERE prestamos.id_usuario = ?")) {
            ps.setString(1, currentUser);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int libroId = resultSet.getInt("ID");
                String titulo = resultSet.getString("Titulo");
                LocalDate fechaDevolucion = resultSet.getDate("Fecha_Devolucion").toLocalDate();
                LocalDate hoy = LocalDate.now();

                long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaDevolucion);

                if (diasRestantes == 7) {
                    addNotification("Quedan 7 días para devolver el libro: " + titulo, hoy.toString(), currentUser);
                } else if (diasRestantes < 0) {
                    addNotification("Multa: libro " + titulo + " retrasado por " + Math.abs(diasRestantes) + " días.", hoy.toString(), currentUser);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al enviar notificaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNotification(String mensaje, String fecha, String currentUser) {
        try (Connection connection = new Db().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO notificaciones (Mensaje, Fecha_Notificacion, id_usuario) VALUES (?, ?, ?)")) {
            ps.setString(1, mensaje);
            ps.setString(2, fecha);
            ps.setString(3, currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al añadir notificación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

