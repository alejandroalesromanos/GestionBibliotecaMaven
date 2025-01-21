package Vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class VistaReportes extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public VistaReportes(boolean isAdmin, String currentUser, String emailUser) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the frame.
	 */
	public VistaReportes() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

	

}
