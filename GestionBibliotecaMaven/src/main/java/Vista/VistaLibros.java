package Vista;

import modelo.GestorLibros;
import modelo.Libro;
import modelo.Libro.Generos;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VistaLibros extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private GestorLibros gestorLibros;
    private Session session;

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

        // Iniciar la sesión de Hibernate
        SessionFactory factory = new Configuration().configure().addAnnotatedClass(Libro.class).buildSessionFactory();
        session = factory.getCurrentSession();
        gestorLibros = new GestorLibros(session);

        // Cargar libros al inicio
        loadBooks();
    }

    private void loadBooks() {
        try {
            session.beginTransaction();
            List<Libro> libros = gestorLibros.obtenerTodosLosLibros();
            tableModel.setRowCount(0); // Limpiar la tabla

            for (Libro libro : libros) {
                tableModel.addRow(new Object[]{
                    libro.getId(), libro.getTitulo(), libro.getAutor(), libro.getGenero() != null ? libro.getGenero() : "N/A",
                    libro.isDisponibilidad() ? "Disponible" : "No Disponible", libro.getFechaDePublicacion() != null ? libro.getFechaDePublicacion().toString() : "N/A"
                });
            }

            session.getTransaction().commit();
        } catch (Exception e) {
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
            java.util.Date fechaPublicacion = fechaPublicacionChooser.getDate();

            if (titulo.isEmpty() || autor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título y Autor son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Libro nuevoLibro = new Libro();
            nuevoLibro.setTitulo(titulo);
            nuevoLibro.setAutor(autor);
            nuevoLibro.setGenero(genero.isEmpty() ? null : Generos.valueOf(genero.toUpperCase()));
            nuevoLibro.setDisponibilidad(disponibilidad);
            nuevoLibro.setFechaDePublicacion(fechaPublicacion);

            if (gestorLibros.insertarLibro(nuevoLibro)) {
                loadBooks();
                JOptionPane.showMessageDialog(this, "Libro añadido con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al añadir libro.", "Error", JOptionPane.ERROR_MESSAGE);
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
        if (gestorLibros.eliminarLibro(bookId)) {
            loadBooks();
            JOptionPane.showMessageDialog(this, "Libro eliminado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al eliminar libro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeAvailability() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para cambiar la disponibilidad.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        Libro libro = gestorLibros.buscarLibroPorId(bookId);
        if (libro != null) {
            libro.setDisponibilidad(!libro.isDisponibilidad()); // Cambiar disponibilidad
            if (gestorLibros.actualizarLibro(libro)) {
                loadBooks();
                JOptionPane.showMessageDialog(this, "Disponibilidad cambiada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar disponibilidad.", "Error", JOptionPane.ERROR_MESSAGE);
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
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(52, 152, 219));
                }
            });
        }
    }
}
