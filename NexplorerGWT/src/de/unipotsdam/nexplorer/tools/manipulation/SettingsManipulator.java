package de.unipotsdam.nexplorer.tools.manipulation;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SettingsManipulator extends JFrame {

	private static final long serialVersionUID = -6005359365370130751L;

	public static void main(String[] args) {
		new SettingsManipulator();
	}

	private JPanel contentPanel;
	private ClientFactory model;

	private SettingsManipulator() {
		model = new ClientFactory();

		contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(new LoginScreen(this), BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);

		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void login(String host) {
		UpdateClient client = model.login(host);
		if (client == null) {
			JOptionPane.showMessageDialog(this, "Verbindung zum Host fehlgeschlagen");
		} else {
			// Update frame content
			contentPanel.removeAll();
			contentPanel.add(new ManipulationScreen(client), BorderLayout.CENTER);
			invalidate();
		}
	}
}
