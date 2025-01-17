package Vista;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import modelo.GestorLogin;

public class VistaLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel contentPanel;
    
    // Colors
    private Color primaryColor = new Color(25, 118, 210);
    private Color accentColor = new Color(66, 165, 245);
    private Color backgroundColor = new Color(236, 239, 241);
    private Color errorColor = new Color(220, 53, 69);
    private Color validColor = new Color(40, 167, 69);
    
    // Dimensions
    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 400;
    private static final Dimension MINIMUM_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

    public VistaLogin() {
        setTitle("Sistema Gestor de Biblioteca");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(MINIMUM_SIZE);
        setSize(450, 600);
        setLocationRelativeTo(null);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidateLayout();
            }
        });

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, primaryColor, 0, h, accentColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        contentPanel = createContentPanel();
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(contentPanel);
        mainPanel.add(Box.createVerticalGlue());
    }

    private void revalidateLayout() {
        // Eliminar revalidaciones y repintados innecesarios
        int width = getWidth();
        
        int titleFontSize = Math.max(24, Math.min(32, width / 15));
        int fieldFontSize = Math.max(12, Math.min(14, width / 30));
        int buttonFontSize = Math.max(12, Math.min(14, width / 30));
        
        int fieldWidth = Math.max(200, Math.min(300, width - 100));
        
        updateComponentSizes(fieldWidth, titleFontSize, fieldFontSize, buttonFontSize);
        contentPanel.revalidate();
    }

    private void updateComponentSizes(int fieldWidth, int titleFontSize, int fieldFontSize, int buttonFontSize) {
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().equals("Sistema Gestor de Biblioteca")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, titleFontSize));
                } else {
                    label.setFont(new Font("Segoe UI", Font.BOLD, fieldFontSize));
                }
            }
        }

        Dimension fieldDimension = new Dimension(fieldWidth, 50);
        emailField.setPreferredSize(fieldDimension);
        emailField.setMaximumSize(fieldDimension);
        emailField.setMinimumSize(fieldDimension);
        passwordField.setPreferredSize(fieldDimension);
        passwordField.setMaximumSize(fieldDimension);
        passwordField.setMinimumSize(fieldDimension);
        
        Dimension buttonDimension = new Dimension(Math.min(200, fieldWidth), 50);
        loginButton.setPreferredSize(buttonDimension);
        loginButton.setMaximumSize(buttonDimension);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, buttonFontSize));
    }


    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Sistema Gestor de Biblioteca");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(30));

        emailField = createStyledTextField("Ingrese su correo electrónico");
        passwordField = createPasswordField();
        
        panel.add(createFieldPanel("Correo Electrónico", emailField));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createFieldPanel("Contraseña", passwordField));
        panel.add(Box.createVerticalStrut(30));

        loginButton = createStyledButton("Iniciar Sesión");
        panel.add(loginButton);

        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(((RoundedBorder) getBorder()).getBorderShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255, 230));
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(new RoundedBorder(25, Color.WHITE));
        
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(new RoundedBorder(25, accentColor));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(new RoundedBorder(25, Color.WHITE));
                validateField(field);
            }
        });
        
        Dimension fieldSize = new Dimension(300, 50);
        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);
        field.setMinimumSize(fieldSize);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!field.getText().equals(placeholder)) {
                    validateField(field);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!field.getText().equals(placeholder)) {
                    validateField(field);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!field.getText().equals(placeholder)) {
                    validateField(field);
                }
            }
        });

        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(((RoundedBorder) getBorder()).getBorderShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(255, 255, 255, 230));
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setBorder(new RoundedBorder(25, Color.WHITE));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals("Ingrese su contraseña")) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(new RoundedBorder(25, accentColor));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText("Ingrese su contraseña");
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(new RoundedBorder(25, Color.WHITE));
            }
        });
        
        Dimension fieldSize = new Dimension(300, 50);
        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);
        field.setMinimumSize(fieldSize);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        return field;
    }

    private void validateField(JTextField field) {
        if (field == emailField && !field.getText().equals("Ingrese su correo electrónico")) {
            String email = field.getText();
            if (!email.isEmpty() && !isValidEmail(email)) {
                field.setBorder(new RoundedBorder(25, errorColor));
            } else if (!email.isEmpty()) {
                field.setBorder(new RoundedBorder(25, validColor));
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);  // Cambiado de LEFT_ALIGNMENT a CENTER_ALIGNMENT
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));  // Eliminado el padding izquierdo

        panel.add(label);
        panel.add(field);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(primaryColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(primaryColor.brighter());
                } else {
                    g2.setColor(primaryColor);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleLogin());

        Dimension buttonSize = new Dimension(200, 50);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        
        return button;
    }

    private void handleLogin() {
    	String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Validación más ligera para evitar llamadas a la base de datos
        if (email.equals("Ingrese su correo electrónico") || 
            password.equals("Ingrese su contraseña")) {
            showErrorMessage("Por favor, complete todos los campos.");
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Por favor, complete todos los campos.");
            return;
        }

        if (!isValidEmail(email)) {
            showErrorMessage("Por favor, ingrese un correo electrónico válido.");
            return;
        }

        // Crear y mostrar la ventana de carga
        PantallaCarga loadingFrame = new PantallaCarga();
        loadingFrame.setVisible(true);

        // Ejecutar la validación en segundo plano
        SwingWorker<Boolean, Void> loginTask = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                GestorLogin gestionLogin = new GestorLogin();
                return gestionLogin.validateCredentials(email, password, VistaLogin.this);
            }

            @Override
            protected void done() {
                try {
                    boolean valid = get();
                    if (valid) {
                        loadingFrame.dispose();
                        dispose();  // Cerrar la ventana de login
                    } else {
                        loadingFrame.dispose();
                        showErrorMessage("Credenciales incorrectas, por favor intente nuevamente.");
                    }
                } catch (Exception e) {
                    loadingFrame.dispose();
                    showErrorMessage("Error al realizar la operación.");
                }
            }
        };

        // Ejecutar la tarea en segundo plano
        loginTask.execute();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.draw(getBorderShape(x, y, width - 1, height - 1));
            g2d.dispose();
        }

        public Shape getBorderShape(int x, int y, int width, int height) {
            return new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
