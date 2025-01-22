package Vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    public MenuPrincipal(boolean isAdmin, String currentUser, String emailUser) {
        setTitle("Menú Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null); 

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
        fondoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        fondoPanel.setLayout(new GridBagLayout());
        setContentPane(fondoPanel);

        // Configuración del layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiqueta de bienvenida
        JLabel welcomeLabel = new JLabel("Bienvenido, " + currentUser, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial Black", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        fondoPanel.add(welcomeLabel, gbc);

        // Botón para gestión de usuarios (solo para administradores)
        if (isAdmin) {
            JButton userManagementButton = createStyledButton("Gestión de Usuarios");
            userManagementButton.addActionListener(e -> {
                new VistaUsuarios(isAdmin, currentUser, emailUser).setVisible(true);
                dispose();
            });
            gbc.gridy = 1;
            fondoPanel.add(userManagementButton, gbc);
        }

        // Botón para vista de libros
        JButton bookManagementButton = createStyledButton("Vista de Libros");
        bookManagementButton.addActionListener(e -> {
            VistaLibros vistaLibros = new VistaLibros(isAdmin, currentUser, emailUser);
            vistaLibros.setVisible(true);
            dispose();
        });
        gbc.gridy = 2;
        fondoPanel.add(bookManagementButton, gbc);

        // Botón para préstamos y devoluciones
        JButton loansAndReturnsButton = createStyledButton("Préstamos y Devoluciones");
        loansAndReturnsButton.addActionListener(e -> {
            new VistaPrestamos(isAdmin, currentUser, emailUser).setVisible(true);
            dispose();
        });
        gbc.gridy = 3;
        fondoPanel.add(loansAndReturnsButton, gbc);

        // Botón para notificaciones
        JButton notificationsButton = createStyledButton("Notificaciones");
        notificationsButton.addActionListener(e -> {
            new VistaNotificaciones(isAdmin, currentUser, emailUser).setVisible(true);
            dispose();
        });
        gbc.gridy = 4;
        fondoPanel.add(notificationsButton, gbc);

        // Botón para reportes (solo para administradores)
        if (isAdmin) {
            JButton reportesButton = createStyledButton("Reportes");
            reportesButton.addActionListener(e -> {
                new VistaReportes(isAdmin, currentUser, emailUser).setVisible(true);
                dispose();
            });
            gbc.gridy = 5; // Nueva posición para el botón
            fondoPanel.add(reportesButton, gbc);
        }

        // Botón para cerrar sesión
        JButton logoutButton = createStyledButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> {
            VistaLogin loginView = new VistaLogin();
            loginView.setSize(700, 500);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (int) (screenSize.getWidth() - loginView.getWidth()) / 2;
            int y = (int) (screenSize.getHeight() - loginView.getHeight()) / 2;
            loginView.setLocation(x, y);
            loginView.setVisible(true);
            dispose();
        });
        gbc.gridy = 6;
        fondoPanel.add(logoutButton, gbc);
    }

    private JButton createStyledButton(String text) {
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
}
