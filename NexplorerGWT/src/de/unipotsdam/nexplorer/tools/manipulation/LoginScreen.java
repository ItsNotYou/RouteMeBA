package de.unipotsdam.nexplorer.tools.manipulation;

import static layout.TableLayoutConstants.FILL;
import static layout.TableLayoutConstants.PREFERRED;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import layout.TableLayout;

public class LoginScreen extends JPanel implements ActionListener {

	private static final long serialVersionUID = 7792154810037436490L;
	private static final double SPACE = 8;
	private SettingsManipulator loginCallback;
	private JTextField hostBox;

	public LoginScreen(SettingsManipulator settingsManipulator) {
		this.loginCallback = settingsManipulator;

		double[][] layout = new double[][] { { FILL, PREFERRED, SPACE, 300, SPACE, PREFERRED, FILL }, { FILL, PREFERRED, FILL } };
		setLayout(new TableLayout(layout));

		JButton login = new JButton("Login");
		login.addActionListener(this);
		add(login, "5, 1");

		hostBox = new JTextField();
		add(hostBox, "3, 1");

		JLabel label = new JLabel("Host:");
		add(label, "1, 1");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String host = hostBox.getText();
		loginCallback.login(host);
	}
}
