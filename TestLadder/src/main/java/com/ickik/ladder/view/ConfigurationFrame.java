package com.ickik.ladder.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ickik.ladder.db.Database;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.utils.LadderProperties;
import com.ickik.ladder.utils.TestLadderEncoding;
import com.ickik.ladder.utils.TestLadderProperties;

/**
 * Configuration frame to connect to the database. The user must complete
 * every fields to connect to the database.
 * @author Patrick Allgeyer
 * @version 0.1.000, 08/03/11
 */
public class ConfigurationFrame extends AbstractTestLadderView {

	private final JFrame configurationFrame;
	private JComboBox dbComboBox;
	private JTextField serverTextField;
	private JTextField portTextField;
	private JTextField dbTextField;
	private JTextField loginDbTextField;
	private JPasswordField pwdTextField;
	
	public ConfigurationFrame(ResourceBundle resourceBundle) {
		this(resourceBundle, false);
	}
	
	public ConfigurationFrame(ResourceBundle resourceBundle, boolean loadFrame) {
		super(resourceBundle);
		configurationFrame = new JFrame(LadderFrame.SOFTWARE + " " + LadderFrame.VERSION);
		try {
			if (!TestLadderProperties.getInstance().propertiesExists() || loadFrame) {
				createConfigurationFrame();
			} else {
				new ConnectionFrame(resourceBundle/*, new DBConnection(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_NAME.toString()), Integer.parseInt(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PORT.toString())), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE.toString()), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()), TestLadderEncoding.decryptionPassword(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PASSWORD.toString())), Database.valueOf(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE_TYPE.toString())))*/);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			//displayErrorMessage("");
		} catch (LadderException e) {
			displayErrorMessage(configurationFrame, e.getMessage());
		}
	}
	
	private void createConfigurationFrame() {
		try {
			configurationFrame.add(createDatabasePanel(), BorderLayout.NORTH);
			configurationFrame.add(createConfigurationPanel(), BorderLayout.CENTER);
			configurationFrame.add(createButtonPanel(), BorderLayout.SOUTH);
			configurationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			configurationFrame.pack();
			configurationFrame.setVisible(true);
			centeredFrameInScreen(configurationFrame);
		} catch (LadderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JPanel createDatabasePanel() throws LadderException {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		dbComboBox = new JComboBox();
		for (Database db : Database.values()) {
			dbComboBox.addItem(db.toString());
		}
		if (TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE_TYPE.toString()) != null) {
			dbComboBox.setSelectedItem(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE_TYPE.toString()));
		}
		panel.add(new JLabel(getResourceBundle().getString("configuration.combobox.databases")));
		panel.add(dbComboBox);
		return panel;
	}
	
	private JPanel createConfigurationPanel() throws LadderException {
		JPanel panel = new JPanel(new GridLayout(5, 2));
		JLabel serverLbl = new JLabel(getResourceBundle().getString("configuration.server.address"));
		serverTextField = new JTextField(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_NAME.toString()));
		JLabel portLbl = new JLabel(getResourceBundle().getString("configuration.server.port_number"));
		portTextField = new JTextField(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PORT.toString()));
		portTextField.addKeyListener(getNumberKeyListener());
		JLabel dbLbl = new JLabel(getResourceBundle().getString("configuration.server.database_name"));
		dbTextField = new JTextField(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE.toString()));
		JLabel loginDbLbl = new JLabel(getResourceBundle().getString("configuration.server.login"));
		loginDbTextField = new JTextField(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()));
		JLabel pwdLbl = new JLabel(getResourceBundle().getString("configuration.server.password"));
		pwdTextField = new JPasswordField();
		if (!TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PASSWORD.toString()).isEmpty()) {
			pwdTextField.setText(TestLadderEncoding.decryptionPassword(loginDbTextField.getText(), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PASSWORD.toString())));
		}
		panel.add(serverLbl);
		panel.add(serverTextField);
		panel.add(portLbl);
		panel.add(portTextField);
		panel.add(dbLbl);
		panel.add(dbTextField);
		panel.add(loginDbLbl);
		panel.add(loginDbTextField);
		panel.add(pwdLbl);
		panel.add(pwdTextField);
		return panel;
	}
	
	private KeyListener getNumberKeyListener() {
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() < KeyEvent.VK_0 || arg0.getKeyCode() > KeyEvent.VK_9) {
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		};
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		JButton okButton = new JButton(getResourceBundle().getString("configuration.button.ok"));
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = dbComboBox.getSelectedIndex();
				boolean error = false;
				List<String> errorList = new ArrayList<String>();
				if ("".equals(serverTextField.getText())) {
					error = true;
					errorList.add("configuration.exist.server.address");
				}
				if ("".equals(portTextField.getText())) {
					error = true;
					errorList.add("configuration.exist.server.port_number");
				}
				if ("".equals(dbTextField.getText())) {
					error = true;
					errorList.add("configuration.exist.server.database");
				}
				if ("".equals(loginDbTextField.getText())) {
					error = true;
					errorList.add("configuration.exist.server.login");
				}
				if (error) {
					displayErrorMessage(configurationFrame, "configuration.exist", errorList.toArray(new String[errorList.size()]));
					return ;
				}
				try {
					InetAddress.getByName(serverTextField.getText());
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.SERVER_NAME.toString(), serverTextField.getText());
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.SERVER_PORT.toString(), portTextField.getText());
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.SERVER_LOGIN.toString(), loginDbTextField.getText());
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.DATABASE.toString(), dbTextField.getText());
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.SERVER_PASSWORD.toString(), TestLadderEncoding.encryptionPassword(loginDbTextField.getText(), new String(pwdTextField.getPassword())));
					TestLadderProperties.getInstance().getProperties().put(LadderProperties.DATABASE_TYPE.toString(), Database.values()[index].toString());
					TestLadderProperties.getInstance().saveProperties();
					new ConnectionFrame(getResourceBundle());
					configurationFrame.dispose();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LadderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					displayErrorMessage(configurationFrame, "connection.connect.server.error");
				}
			}
		});
		JButton cancelButton = new JButton(getResourceBundle().getString("configuration.button.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				configurationFrame.dispose();
				System.exit(0);
			}
		});
		panel.add(okButton);
		panel.add(cancelButton);
		return panel;
	}
}
