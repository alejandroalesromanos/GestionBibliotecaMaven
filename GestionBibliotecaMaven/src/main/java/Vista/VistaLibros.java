package Vista;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import modelo.Db;

public class VistaLibros extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public VistaLibros(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Vista de Libros");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("Lista de Libros", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        fondoPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de libros
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Título", "Autor", "Género", "Disponibilidad", "Fecha de Publicación"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookTable.getTableHeader().setBackground(new Color(41, 128, 185));
        bookTable.getTableHeader().setForeground(Color.WHITE);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(bookTable);
        fondoPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (isAdmin) {
            JButton addBookButton = new StyledButton("Añadir Libro");
            addBookButton.addActionListener(e -> addBook());
            buttonPanel.add(addBookButton);

            JButton deleteBookButton = new StyledButton("Eliminar Libro");
            deleteBookButton.addActionListener(e -> deleteBook());
            buttonPanel.add(deleteBookButton);

            JButton changeAvailabilityButton = new StyledButton("Cambiar Disponibilidad");
            changeAvailabilityButton.addActionListener(e -> changeAvailability());
            buttonPanel.add(changeAvailabilityButton);
        }

        JButton backButton = new StyledButton("Volver al Menú Principal");
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
        });
        buttonPanel.add(backButton);

        fondoPanel.add(buttonPanel, BorderLayout.EAST);

        // Cargar libros al inicio
        loadBooks();
    }

    private void loadBooks() {
        try (Connection connection = new Db().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, Titulo, Autor, Genero, Disponibilidad, Fecha_Publicacion FROM libros")) {

            tableModel.setRowCount(0); // Limpiar la tabla
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String titulo = resultSet.getString("Titulo");
                String autor = resultSet.getString("Autor");
                String genero = resultSet.getString("Genero");
                boolean disponibilidad = resultSet.getBoolean("Disponibilidad");
                Date fechaPublicacion = resultSet.getDate("Fecha_Publicacion");

                tableModel.addRow(new Object[]{
                    id, titulo, autor, genero != null ? genero : "N/A",
                    disponibilidad ? "Disponible" : "No Disponible",
                    fechaPublicacion != null ? fechaPublicacion.toString() : "N/A"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField tituloField = new JTextField();
        JTextField autorField = new JTextField();
        JTextField generoField = new JTextField();
        JCheckBox disponibilidadBox = new JCheckBox("Disponible");
        JDateChooser fechaPublicacionChooser = new JDateChooser();

        panel.add(new JLabel("Título:"));
        panel.add(tituloField);
        panel.add(new JLabel("Autor:"));
        panel.add(autorField);
        panel.add(new JLabel("Género:"));
        panel.add(generoField);
        panel.add(new JLabel("Disponibilidad:"));
        panel.add(disponibilidadBox);
        panel.add(new JLabel("Fecha de Publicación:"));
        panel.add(fechaPublicacionChooser);

        int result = JOptionPane.showConfirmDialog(this, panel, "Añadir Libro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String titulo = tituloField.getText().trim();
            String autor = autorField.getText().trim();
            String genero = generoField.getText().trim();
            boolean disponibilidad = disponibilidadBox.isSelected();
            Date fechaPublicacion = null;

            if (fechaPublicacionChooser.getDate() != null) {
                fechaPublicacion = new java.sql.Date(fechaPublicacionChooser.getDate().getTime());
            }

            if (titulo.isEmpty() || autor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título y Autor son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "INSERT INTO libros (Titulo, Autor, Genero, Disponibilidad, Fecha_Publicacion) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, titulo);
                ps.setString(2, autor);
                ps.setString(3, genero.isEmpty() ? null : genero);
                ps.setBoolean(4, disponibilidad);
                ps.setDate(5, fechaPublicacion);
                ps.executeUpdate();

                loadBooks();
                JOptionPane.showMessageDialog(this, "Libro añadido con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al añadir libro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmOption = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este libro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmOption == JOptionPane.YES_OPTION) {
            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM libros WHERE ID = ?")) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
                loadBooks();
                JOptionPane.showMessageDialog(this, "Libro eliminado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar libro: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeAvailability() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para cambiar la disponibilidad.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean currentAvailability = "Disponible".equals(tableModel.getValueAt(selectedRow, 4));

        int confirmOption = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cambiar la disponibilidad de este libro?",
                "Confirmar Cambio",
                JOptionPane.YES_NO_OPTION);

        if (confirmOption == JOptionPane.YES_OPTION) {
            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE libros SET Disponibilidad = ? WHERE ID = ?")) {
                ps.setBoolean(1, !currentAvailability);
                ps.setInt(2, bookId);
                ps.executeUpdate();

                loadBooks();
                JOptionPane.showMessageDialog(this, "Disponibilidad cambiada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cambiar disponibilidad: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
}
