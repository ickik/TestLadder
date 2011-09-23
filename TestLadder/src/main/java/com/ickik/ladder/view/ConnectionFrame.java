package com.ickik.ladder.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.db.Database;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.utils.LadderProperties;
import com.ickik.ladder.utils.TestLadderEncoding;
import com.ickik.ladder.utils.TestLadderProperties;
import com.ickik.ladder.values.User;

/**
 * Frame to connect to the database and access on test cases
 * and test executed.
 * @author Patrick Allgeyer
 * @version 0.1.000, 14/03/11
 */
public class ConnectionFrame extends AbstractTestLadderView {

	private final JFrame connectionFrame;
	
	public ConnectionFrame(ResourceBundle resourceBundle) {
		super(resourceBundle);
		this.connectionFrame = new JFrame(LadderFrame.SOFTWARE + " " + LadderFrame.VERSION);
		try {
			createConnectionFrame();
		} catch (LadderException e) {
			displayErrorMessage(null, e.getMessage());
		}
	}
	
	private void createConnectionFrame() throws LadderException {
		connectionFrame.add(createLoginPanel(), BorderLayout.CENTER);
		connectionFrame.add(createConfigurationPanel(), BorderLayout.SOUTH);
		connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectionFrame.setResizable(false);
		connectionFrame.pack();
		connectionFrame.setVisible(true);
		centeredFrameInScreen(connectionFrame);
	}
	
	private JPanel createConfigurationPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		JButton configurationButton = new JButton(getResourceBundle().getString("connection.button.configuration"));
		configurationButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connectionFrame.dispose();
				new ConfigurationFrame(getResourceBundle(), true);
			}
		});
		panel.add(configurationButton);
		return panel;
	}
	
	private JPanel createLoginPanel() throws LadderException {
		JPanel panel = new JPanel(new GridLayout(3, 2));
		JLabel loginLbl = new JLabel(getResourceBundle().getString("connection.login"));
		final JTextField login = new JTextField(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.LOGIN.toString()));
		JLabel passwordLbl = new JLabel(getResourceBundle().getString("connection.password"));
		final JPasswordField password = new JPasswordField();
		
		JButton connectButton = new JButton(getResourceBundle().getString("connection.button.ok"));
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!checkParameters(login.getText(), new String(password.getPassword()))) {
					return;
				}
				try {
					DBConnection connection = new DBConnection(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_NAME.toString()), Integer.parseInt(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PORT.toString())), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE.toString()), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()), TestLadderEncoding.decryptionPassword(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PASSWORD.toString())), Database.valueOf(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE_TYPE.toString())));
					connection.connection();
					User user = connection.checkUser(login.getText(), new String(password.getPassword()));
					if (user != null) {
						connectionFrame.dispose();
						TestLadderProperties.getInstance().getProperties().put(LadderProperties.LOGIN.toString(), login.getText());
						TestLadderProperties.getInstance().saveProperties();
						new LadderFrame(getResourceBundle(), user, connection);
					} else {
						displayErrorMessage(connectionFrame, "connection.login_password.failed");
					}
				} catch (LadderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		JButton cancelButton = createCancelButton();
		
		panel.add(loginLbl);
		panel.add(login);
		panel.add(passwordLbl);
		panel.add(password);
		panel.add(connectButton);
		panel.add(cancelButton);
		return panel;
	}
	
	private boolean checkParameters(String login, String password) {
		if (login.isEmpty() && password.isEmpty()) {
			displayErrorMessage(connectionFrame, "connection.login_password.empty");
			return false;
		}
		if (login.isEmpty()) {
			displayErrorMessage(connectionFrame, "connection.login.empty");
			return false;
		}
		if (password.isEmpty()) {
			displayErrorMessage(connectionFrame, "connection.password.empty");
			return false;
		}
		return true;
	}
	
	private JButton createCancelButton() {
		JButton cancelButton = new JButton(getResourceBundle().getString("connection.button.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connectionFrame.dispose();
				System.exit(0);
			}
		});
		return cancelButton;
	}
}
