package Vista;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import modelo.Db;

public class VistaPrestamos extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTable prestamosTable;
	private DefaultTableModel tableModel;

	public VistaPrestamos(boolean isAdmin, String currentUser, String emailUser) {
		setTitle("Vista de Préstamos");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
		setResizable(false);

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

		JLabel titleLabel = new JLabel("Lista de Préstamos", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
		titleLabel.setForeground(Color.WHITE);
		fondoPanel.add(titleLabel, BorderLayout.NORTH);

		tableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "Libro", "Usuario", "Fecha Préstamo", "Fecha Devolución", "Multa", "Estado" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		prestamosTable = new JTable(tableModel);
		prestamosTable.setRowHeight(25);
		prestamosTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		prestamosTable.getTableHeader().setBackground(new Color(41, 128, 185));
		prestamosTable.getTableHeader().setForeground(Color.WHITE);
		prestamosTable.setFont(new Font("Arial", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(prestamosTable);
		fondoPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JButton allLoansButton = createStyledButton("Mostrar Todos los Préstamos",
				e -> loadPrestamos("ALL", isAdmin, emailUser));
		buttonPanel.add(allLoansButton);

		JButton returnedLoansButton = createStyledButton("Mostrar Préstamos Devueltos",
				e -> loadPrestamos("RETURNED", isAdmin, emailUser));
		buttonPanel.add(returnedLoansButton);

		JButton notReturnedLoansButton = createStyledButton("Mostrar Préstamos No Devueltos",
				e -> loadPrestamos("NOT_RETURNED", isAdmin, emailUser));
		buttonPanel.add(notReturnedLoansButton);

		if (isAdmin) {
			JButton addLoanButton = createStyledButton("Añadir Préstamo",
					e -> addPrestamo(isAdmin, currentUser, emailUser));
			buttonPanel.add(addLoanButton);

			JButton markAsReturnedButton = createStyledButton("Marcar como Devuelto", e -> updateReturnStatus(true));
			buttonPanel.add(markAsReturnedButton);

			JButton markAsNotReturnedButton = createStyledButton("Marcar como No Devuelto",
					e -> updateReturnStatus(false));
			buttonPanel.add(markAsNotReturnedButton);
		} else {
			JButton reserveButton = createStyledButton("Reservar Libro",
					e -> addPrestamo(isAdmin, currentUser, emailUser));
			buttonPanel.add(reserveButton);
		}

		JButton backButton = createStyledButton("Volver al Menú Principal", e -> {
			dispose();
			new MenuPrincipal(isAdmin, currentUser, emailUser).setVisible(true);
		});
		buttonPanel.add(backButton);

		fondoPanel.add(buttonPanel, BorderLayout.EAST);
		loadPrestamos("ALL", isAdmin, emailUser);
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

	private void loadPrestamos(String filter, boolean isAdmin, String emailUser) {
		try (Connection connection = new Db().getConnection()) {
			String query = "SELECT l.Titulo, u.Email AS Usuario, p.Fecha_Prestamo, p.Fecha_Devolucion, p.Multa_Generada, p.ID "
					+ "FROM prestamos p " + "JOIN libros l ON p.ID_Libro = l.ID "
					+ "JOIN usuarios u ON p.ID_Usuario = u.ID";

			if (!isAdmin) {
				query += " WHERE u.Email = ?";
			}

			if ("NOT_RETURNED".equals(filter)) {
				query += isAdmin ? " WHERE p.Fecha_Devolucion IS NULL" : " AND p.Fecha_Devolucion IS NULL";
			} else if ("RETURNED".equals(filter)) {
				query += isAdmin ? " WHERE p.Fecha_Devolucion IS NOT NULL" : " AND p.Fecha_Devolucion IS NOT NULL";
			}

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				if (!isAdmin) {
					statement.setString(1, emailUser);
				}

				try (ResultSet resultSet = statement.executeQuery()) {
					tableModel.setRowCount(0);
					while (resultSet.next()) {
						String estado = resultSet.getDate("Fecha_Devolucion") != null ? "Devuelto" : "No Devuelto";
						Date fechaPrestamo = resultSet.getDate("Fecha_Prestamo");
						Date fechaDevolucion = resultSet.getDate("Fecha_Devolucion");
						long multa = 0;

						if (fechaDevolucion == null) {
							// Calcular días transcurridos desde la fecha de préstamo
							long diasTranscurridos = (System.currentTimeMillis() - fechaPrestamo.getTime())
									/ (1000 * 60 * 60 * 24);
							if (diasTranscurridos > 15) {
								multa = (diasTranscurridos - 15) * 2; // Ejemplo: 2 unidades monetarias por día
							}
						}

						tableModel.addRow(new Object[] { resultSet.getString("Titulo"), resultSet.getString("Usuario"),
								fechaPrestamo, fechaDevolucion,
								multa > 0 ? multa : resultSet.getFloat("Multa_Generada"), estado,
								resultSet.getInt("ID") });
					}
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar los préstamos: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addPrestamo(boolean isAdmin, String currentUser, String emailUser) {
	    JDialog dialog = new JDialog(this, "Reservar Libro", true);
	    dialog.setLayout(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    dialog.setSize(400, 250); // Tamaño del cuadro de diálogo
	    dialog.setLocationRelativeTo(this);

	    JLabel libroLabel = new JLabel("Selecciona un libro:");
	    JComboBox<String> libroComboBox = new JComboBox<>();
	    libroComboBox.setPreferredSize(new Dimension(200, 25)); // Ajuste del tamaño
	    Map<String, Integer> libroMap = new HashMap<>();

	    try (Connection connection = new Db().getConnection()) {
	        String query = "SELECT ID, Titulo FROM libros WHERE Disponibilidad = 1";
	        try (PreparedStatement statement = connection.prepareStatement(query);
	             ResultSet resultSet = statement.executeQuery()) {

	            while (resultSet.next()) {
	                String titulo = resultSet.getString("Titulo");
	                int id = resultSet.getInt("ID");
	                libroComboBox.addItem(titulo);
	                libroMap.put(titulo, id);
	            }
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(dialog, "Error al cargar los libros disponibles: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    JLabel fechaLabel = new JLabel("Fecha Préstamo:");
	    JDateChooser fechaChooser = new JDateChooser();
	    fechaChooser.setDateFormatString("yyyy-MM-dd");
	    fechaChooser.setPreferredSize(new Dimension(200, 25));

	    JButton addButton = new JButton("Reservar");
	    addButton.setPreferredSize(new Dimension(100, 30));
	    addButton.addActionListener(e -> {
	        String selectedLibro = (String) libroComboBox.getSelectedItem();
	        if (selectedLibro == null) {
	            JOptionPane.showMessageDialog(dialog, "Debe seleccionar un libro.", "Advertencia", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        int libroId = libroMap.get(selectedLibro);
	        try (Connection connection = new Db().getConnection()) {
	            String query = "INSERT INTO prestamos (ID_Libro, ID_Usuario, Fecha_Prestamo) VALUES (?, ?, ?)";
	            try (PreparedStatement statement = connection.prepareStatement(query)) {
	                statement.setInt(1, libroId);
	                statement.setInt(2, getUserIdByEmail(emailUser, connection)); // Corrección aquí
	                statement.setDate(3, new java.sql.Date(fechaChooser.getDate().getTime()));

	                statement.executeUpdate();

	                // Actualizar la disponibilidad del libro
	                String updateQuery = "UPDATE libros SET Disponibilidad = 0 WHERE ID = ?";
	                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
	                    updateStatement.setInt(1, libroId);
	                    updateStatement.executeUpdate();
	                }

	                JOptionPane.showMessageDialog(dialog, "Préstamo añadido con éxito.");
	                dialog.dispose();
	                loadPrestamos("ALL", isAdmin, emailUser);
	            }
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(dialog, "Error al añadir el préstamo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.insets = new Insets(5, 5, 5, 5);
	    dialog.add(libroLabel, gbc);

	    gbc.gridx = 1;
	    dialog.add(libroComboBox, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    dialog.add(fechaLabel, gbc);

	    gbc.gridx = 1;
	    dialog.add(fechaChooser, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2;
	    dialog.add(addButton, gbc);

	    dialog.setVisible(true);
	}



	private void updateReturnStatus(boolean isReturned) {
		int selectedRow = prestamosTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.", "Advertencia",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int prestamoId = (int) tableModel.getValueAt(selectedRow, 6);
		try (Connection connection = new Db().getConnection()) {
			String query = "UPDATE prestamos SET Fecha_Devolucion = ? WHERE ID = ?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				if (isReturned) {
					statement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
				} else {
					statement.setNull(1, Types.DATE);
				}
				statement.setInt(2, prestamoId);

				statement.executeUpdate();
				JOptionPane.showMessageDialog(this, "Estado de devolución actualizado con éxito.");
				loadPrestamos("ALL", true, null);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al actualizar el estado de devolución: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private int getUserIdByEmail(String email, Connection connection) throws SQLException {
		String query = "SELECT ID FROM usuarios WHERE Email = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("ID");
				}
			}
		}
		throw new SQLException("Usuario no encontrado.");
	}
}
