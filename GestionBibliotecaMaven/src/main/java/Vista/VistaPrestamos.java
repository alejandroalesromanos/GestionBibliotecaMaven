package Vista;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import modelo.Db;

public class VistaPrestamos extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTable prestamosTable;
	private DefaultTableModel tableModel;
	private static Map<Integer, Queue<String>> listaEspera = new HashMap<>();

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
							long diasTranscurridos = (System.currentTimeMillis() - fechaPrestamo.getTime())
									/ (1000 * 60 * 60 * 24);
							if (diasTranscurridos > 15) {
								multa = (diasTranscurridos - 15) * 2;
							}
						}

						tableModel.addRow(new Object[] { resultSet.getString("Titulo"), resultSet.getString("Usuario"),
								fechaPrestamo, fechaDevolucion,
								multa > 0 ? multa : resultSet.getFloat("Multa_Generada"), estado });
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al cargar los préstamos: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateReturnStatus(boolean returned) {
		int selectedRow = prestamosTable.getSelectedRow();
		if (selectedRow != -1) {
			try (Connection connection = new Db().getConnection()) {
				String titulo = (String) prestamosTable.getValueAt(selectedRow, 0);
				String usuario = (String) prestamosTable.getValueAt(selectedRow, 1);

				// Obtener las fechas necesarias
				String query = "SELECT p.fecha_prestamo, p.fecha_devolucion " + "FROM prestamos p "
						+ "JOIN libros l ON p.ID_Libro = l.ID " + "JOIN usuarios u ON p.ID_Usuario = u.ID "
						+ "WHERE l.Titulo = ? AND u.email = ?";

				try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setString(1, titulo);
					statement.setString(2, usuario);

					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							Date fechaPrestamo = resultSet.getDate("fecha_prestamo");
							Date fechaDevolucion = resultSet.getDate("fecha_devolucion");

							// Si el libro ya fue devuelto, no hacer nada
							if (fechaDevolucion != null) {
								JOptionPane.showMessageDialog(this, "Este libro ya ha sido devuelto.", "Información",
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}

							// Calcular multa (ejemplo: 1.5 unidades monetarias por día de retraso)
							long diferenciaDias = (new Date(System.currentTimeMillis()).getTime()
									- fechaPrestamo.getTime()) / (1000 * 60 * 60 * 24);
							double multa = 0;

							// Supongamos que el período permitido es fijo, por ejemplo, 15 días
							int diasPermitidos = 15;
							if (diferenciaDias > diasPermitidos) {
								multa = (diferenciaDias - diasPermitidos) * 1.5;
							}

							// Actualizar el préstamo con la fecha de devolución y la multa
							String updatePrestamoQuery = "UPDATE prestamos SET fecha_devolucion = ?, multa = ? "
									+ "WHERE ID_Libro = (SELECT ID FROM libros WHERE Titulo = ?) "
									+ "AND ID_Usuario = (SELECT ID FROM usuarios WHERE email = ?)";

							try (PreparedStatement updateStatement = connection.prepareStatement(updatePrestamoQuery)) {
								updateStatement.setDate(1, new Date(System.currentTimeMillis()));
								updateStatement.setDouble(2, multa);
								updateStatement.setString(3, titulo);
								updateStatement.setString(4, usuario);

								int updatedRows = updateStatement.executeUpdate();
								if (updatedRows > 0) {
									JOptionPane.showMessageDialog(this,
											"Estado de devolución y multa actualizados con éxito.", "Éxito",
											JOptionPane.INFORMATION_MESSAGE);

									// Si el libro ha sido devuelto, revisar la lista de espera
									if (returned) {
										String listaEsperaQuery = "SELECT ID_Usuario FROM lista_espera le "
												+ "JOIN libros l ON le.ID_Libro = l.ID "
												+ "WHERE l.Titulo = ? ORDER BY le.Fecha_Solicitud ASC LIMIT 1";

										try (PreparedStatement waitlistStatement = connection
												.prepareStatement(listaEsperaQuery)) {
											waitlistStatement.setString(1, titulo);

											try (ResultSet waitlistResult = waitlistStatement.executeQuery()) {
												if (waitlistResult.next()) {
													int siguienteUsuarioId = waitlistResult.getInt("ID_Usuario");

													// Asignar el préstamo al siguiente usuario
													String prestamoQuery = "INSERT INTO prestamos (ID_Libro, ID_Usuario, fecha_prestamo) "
															+ "VALUES ((SELECT ID FROM libros WHERE Titulo = ?), ?, ?)";

													try (PreparedStatement prestamoStatement = connection
															.prepareStatement(prestamoQuery)) {
														prestamoStatement.setString(1, titulo);
														prestamoStatement.setInt(2, siguienteUsuarioId);
														prestamoStatement.setDate(3,
																new Date(System.currentTimeMillis()));

														prestamoStatement.executeUpdate();

														// Eliminar al usuario de la lista de espera
														String eliminarEsperaQuery = "DELETE FROM lista_espera "
																+ "WHERE ID_Libro = (SELECT ID FROM libros WHERE Titulo = ?) "
																+ "AND ID_Usuario = ?";

														try (PreparedStatement eliminarEsperaStatement = connection
																.prepareStatement(eliminarEsperaQuery)) {
															eliminarEsperaStatement.setString(1, titulo);
															eliminarEsperaStatement.setInt(2, siguienteUsuarioId);

															eliminarEsperaStatement.executeUpdate();
														}

														JOptionPane.showMessageDialog(this,
																"El libro ha sido asignado al siguiente usuario en la lista de espera.",
																"Éxito", JOptionPane.INFORMATION_MESSAGE);
													}
												} else {
													// Si no hay usuarios en la lista de espera
													JOptionPane.showMessageDialog(this,
															"El libro ha sido devuelto y está disponible nuevamente.",
															"Éxito", JOptionPane.INFORMATION_MESSAGE);
												}
											}
										}
									}

									loadPrestamos("ALL", true, null);
								} else {
									JOptionPane.showMessageDialog(this, "No se encontró el préstamo para actualizar.",
											"Advertencia", JOptionPane.WARNING_MESSAGE);
								}
							}
						}
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error al actualizar el estado: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Por favor, seleccione un préstamo para actualizar.", "Advertencia",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void addPrestamo(boolean isAdmin, String currentUser, String emailUser) {
		JDialog dialog = new JDialog(this, "Nuevo Préstamo", true);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Labels
		JLabel libroLabel = new JLabel("Título del Libro:");
		JLabel usuarioLabel = new JLabel("Email del Usuario:");
		JLabel fechaPrestamoLabel = new JLabel("Fecha del Préstamo:");

		// ComboBox para seleccionar libro
		JComboBox<String> libroComboBox = new JComboBox<>();
		try (Connection connection = new Db().getConnection()) {
			String query = "SELECT Titulo FROM libros";
			try (PreparedStatement statement = connection.prepareStatement(query);
					ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					libroComboBox.addItem(resultSet.getString("Titulo"));
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		// Email del usuario
		JTextField usuarioField = new JTextField(isAdmin ? "" : emailUser);
		usuarioField.setEditable(!isAdmin);

		// Fecha de préstamo
		JDateChooser fechaPrestamoChooser = new JDateChooser();
		fechaPrestamoChooser.setDateFormatString("yyyy-MM-dd");

		// Bloquear días anteriores a hoy
		java.util.Date currentDate = new java.util.Date();
		fechaPrestamoChooser.setSelectableDateRange(currentDate, null);

		// Botón Añadir
		JButton addButton = new JButton("Añadir");
		addButton.setPreferredSize(new Dimension(100, 40));
		addButton.addActionListener(e -> {
			String titulo = (String) libroComboBox.getSelectedItem();
			String usuario = usuarioField.getText().trim();
			java.util.Date fechaPrestamo = fechaPrestamoChooser.getDate();

			if (titulo.isEmpty() || usuario.isEmpty() || fechaPrestamo == null) {
				JOptionPane.showMessageDialog(dialog, "Por favor, complete todos los campos.", "Advertencia",
						JOptionPane.WARNING_MESSAGE);
			} else {
				try (Connection connection = new Db().getConnection()) {
					// Verificar disponibilidad del libro
					String disponibilidadQuery = "SELECT COUNT(*) AS PrestamosActivos FROM prestamos p "
							+ "JOIN libros l ON p.ID_Libro = l.ID "
							+ "WHERE l.Titulo = ? AND p.Fecha_Devolucion IS NULL";

					try (PreparedStatement checkStatement = connection.prepareStatement(disponibilidadQuery)) {
						checkStatement.setString(1, titulo);

						try (ResultSet resultSet = checkStatement.executeQuery()) {
							if (resultSet.next() && resultSet.getInt("PrestamosActivos") > 0) {
								// Libro no disponible, agregar a la lista de espera
								String listaEsperaQuery = "INSERT INTO lista_espera (ID_Libro, ID_Usuario, Fecha_Solicitud) "
										+ "VALUES ((SELECT ID FROM libros WHERE Titulo = ?), "
										+ "(SELECT ID FROM usuarios WHERE Email = ?), ?)";

								try (PreparedStatement waitlistStatement = connection.prepareStatement(listaEsperaQuery,
										Statement.RETURN_GENERATED_KEYS)) {
									waitlistStatement.setString(1, titulo);
									waitlistStatement.setString(2, usuario);
									waitlistStatement.setDate(3, new Date(fechaPrestamo.getTime()));

									waitlistStatement.executeUpdate();

									// Obtener la posición en la lista de espera
									String posicionQuery = "SELECT COUNT(*) AS Posicion FROM lista_espera le "
											+ "JOIN libros l ON le.ID_Libro = l.ID " + "WHERE l.Titulo = ?";

									try (PreparedStatement posicionStatement = connection
											.prepareStatement(posicionQuery)) {
										posicionStatement.setString(1, titulo);

										try (ResultSet posicionResult = posicionStatement.executeQuery()) {
											if (posicionResult.next()) {
												int posicion = posicionResult.getInt("Posicion");
												JOptionPane.showMessageDialog(dialog,
														"El libro no está disponible actualmente. "
																+ "Se ha agregado a la lista de espera en la posición: "
																+ posicion,
														"Lista de Espera", JOptionPane.INFORMATION_MESSAGE);
											}
										}
									}
								}
							} else {
								// Libro disponible, registrar el préstamo
								String prestamoQuery = "INSERT INTO prestamos (ID_Libro, ID_Usuario, Fecha_Prestamo) "
										+ "VALUES ((SELECT ID FROM libros WHERE Titulo = ?), "
										+ "(SELECT ID FROM usuarios WHERE Email = ?), ?)";

								try (PreparedStatement prestamoStatement = connection.prepareStatement(prestamoQuery)) {
									prestamoStatement.setString(1, titulo);
									prestamoStatement.setString(2, usuario);
									prestamoStatement.setDate(3, new Date(fechaPrestamo.getTime()));

									int rowsInserted = prestamoStatement.executeUpdate();
									if (rowsInserted > 0) {
										JOptionPane.showMessageDialog(dialog, "Préstamo añadido con éxito.", "Éxito",
												JOptionPane.INFORMATION_MESSAGE);
										loadPrestamos("ALL", isAdmin, emailUser);
										dialog.dispose();
									}
								}
							}
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(dialog, "Error al procesar el préstamo: " + ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Botón Cancelar
		JButton cancelButton = new JButton("Cancelar");
		cancelButton.setPreferredSize(new Dimension(100, 40));
		cancelButton.addActionListener(e -> dialog.dispose());

		// Añadir componentes al panel con GridBagLayout
		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(libroLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		dialog.add(libroComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		dialog.add(usuarioLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		dialog.add(usuarioField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		dialog.add(fechaPrestamoLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		dialog.add(fechaPrestamoChooser, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		dialog.add(addButton, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		dialog.add(cancelButton, gbc);

		dialog.setVisible(true);
	}

}
