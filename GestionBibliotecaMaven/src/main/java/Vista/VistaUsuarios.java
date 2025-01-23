package Vista;

import modelo.Db;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class VistaUsuarios extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public VistaUsuarios(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Gestión de Usuarios");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        fondoPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabla de usuarios
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"DNI", "Nombre", "Apellidos", "Email", "Teléfono", "Rol"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(new Color(41, 128, 185));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(userTable);
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

      

        if (isAdmin) {
            JButton addUserButton = new StyledButton("Añadir Usuario");
            addUserButton.addActionListener(e -> showAddUserForm());
            buttonPanel.add(addUserButton);

            JButton editUserButton = new StyledButton("Editar Usuario");
            editUserButton.addActionListener(e -> editUser());
            buttonPanel.add(editUserButton);

            JButton deleteUserButton = new StyledButton("Eliminar Usuario");
            deleteUserButton.addActionListener(e -> deleteUser());
            buttonPanel.add(deleteUserButton);
        }

        JButton backButton = new StyledButton("Volver al Menú Principal");
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
        });
        buttonPanel.add(backButton);

        fondoPanel.add(buttonPanel, BorderLayout.EAST);

        // Cargar usuarios al inicio
        loadUsers();
    }

    private void loadUsers() {
        try (Connection connection = new Db().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT ID, DNI, Nombre, Apellidos, Email, Telefono, Rol FROM usuarios")) {

            tableModel.setRowCount(0); // Limpiar la tabla antes de cargar

            while (resultSet.next()) {
                tableModel.addRow(new Object[] {
                        resultSet.getString("DNI"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellidos"),
                        resultSet.getString("Email"),
                        resultSet.getString("Telefono"),
                        resultSet.getString("Rol")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField dniField = new JTextField(20);
        JTextField nombreField = new JTextField(20);
        JTextField apellidosField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField telefonoField = new JTextField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] { "Administrador", "Usuario_estándar" });
        JPasswordField passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        panel.add(dniField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        panel.add(apellidosField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        panel.add(telefonoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Añadir Usuario", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dni = dniField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellidos = apellidosField.getText().trim();
            String email = emailField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String rol = (String) roleComboBox.getSelectedItem();
            String password = new String(passwordField.getPassword());

            if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || telefono.isEmpty()
                    || rol == null || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "INSERT INTO usuarios (DNI, Nombre, Apellidos, Email, Telefono, Rol, Password) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, dni);
                ps.setString(2, nombre);
                ps.setString(3, apellidos);
                ps.setString(4, email);
                ps.setString(5, telefono);
                ps.setString(6, rol);
                ps.setString(7, password);
                ps.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "Usuario añadido con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al añadir usuario: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirmOption = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmOption == JOptionPane.YES_OPTION) {
            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM usuarios WHERE ID = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String dni = (String) tableModel.getValueAt(selectedRow, 1);
        String nombre = (String) tableModel.getValueAt(selectedRow, 2);
        String apellidos = (String) tableModel.getValueAt(selectedRow, 3);
        String email = (String) tableModel.getValueAt(selectedRow, 4);
        String telefono = (String) tableModel.getValueAt(selectedRow, 5);
        String rol = (String) tableModel.getValueAt(selectedRow, 6);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField dniField = new JTextField(dni, 20);
        JTextField nombreField = new JTextField(nombre, 20);
        JTextField apellidosField = new JTextField(apellidos, 20);
        JTextField emailField = new JTextField(email, 20);
        JTextField telefonoField = new JTextField(telefono, 20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Administrador", "Usuario_estándar"});
        roleComboBox.setSelectedItem(rol);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        panel.add(dniField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        panel.add(apellidosField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        panel.add(telefonoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = new Db().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "UPDATE usuarios SET DNI = ?, Nombre = ?, Apellidos = ?, Email = ?, Telefono = ?, Rol = ? WHERE ID = ?")) {
                ps.setString(1, dniField.getText());
                ps.setString(2, nombreField.getText());
                ps.setString(3, apellidosField.getText());
                ps.setString(4, emailField.getText());
                ps.setString(5, telefonoField.getText());
                ps.setString(6, (String) roleComboBox.getSelectedItem());
                ps.setInt(7, userId);
                ps.executeUpdate();
                loadUsers();
                JOptionPane.showMessageDialog(this, "Usuario editado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al editar usuario: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}

