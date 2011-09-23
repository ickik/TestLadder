package com.ickik.ladder.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ickik.ladder.db.Database;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.model.RightsTab;
import com.ickik.ladder.model.UserModel;

/**
 * Frame which displays the information about a selected user.
 * @author Patrick Allgeyer
 * @version 0.1.000, 17/03/11
 */
public final class RightsTestLadderFrame extends AbstractTestLadderView {

	private final JFrame ladderFrame;
	private final JFrame rightsFrame;
	private final int userNumber;
	private final UserModel userModel;
	private final Map<Integer, JCheckBox> rightCheckBoxMap = new HashMap<Integer, JCheckBox>();
	private JTextField login;
	private JPasswordField password;
	
	/**
	 * Constructor called when an user wants to be created.
	 * @param ladderFrame the calling frame.
	 * @param resourceBundle the bundle to display the rights label to the
	 * user.
	 * @param model the userModel which handles all users and allows to
	 * insert or update them in database.
	 */
	public RightsTestLadderFrame(JFrame ladderFrame, ResourceBundle resourceBundle, UserModel model) {
		this(ladderFrame, resourceBundle, -1, model);
	}
	
	/**
	 * Constructor called when an user wants to be created.
	 * @param ladderFrame the calling frame.
	 * @param resourceBundle the bundle to display the rights label to the
	 * user.
	 * @userNumber the id of the user to modify.
	 * @param model the userModel which handles all users and allows to
	 * insert or update them in database.
	 */
	public RightsTestLadderFrame(JFrame ladderFrame, ResourceBundle resourceBundle, int userNumber, UserModel model) {
		super(resourceBundle);
		this.userNumber = userNumber;
		this.userModel = model;
		this.ladderFrame = ladderFrame;
		this.rightsFrame = new JFrame(SOFTWARE + " " + VERSION);
		this.ladderFrame.setEnabled(false);
		createRightsFrame();
	}
	
	private void createRightsFrame() {
		rightsFrame.add(createIdPanel(), BorderLayout.NORTH);
		rightsFrame.add(createRightsPanel(), BorderLayout.CENTER);
		rightsFrame.add(createButtonPanel(), BorderLayout.SOUTH);
		rightsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		rightsFrame.pack();
		rightsFrame.setVisible(true);
		centeredFrame(rightsFrame);
	}
	
	private JPanel createIdPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 2));
		login = new JTextField();
		password = new JPasswordField();
		if (userNumber != -1) {
			//try {
				password.setText(userModel.getUser(userNumber).getPassword());
			/*} catch (LadderException e) {
				displayErrorMessage(null, e.getMessage());
			}*/
			login.setText(userModel.getUser(userNumber).getLogin());
			login.setEnabled(false);
		}
		panel.add(new JLabel(getResourceBundle().getString("rightsframe.login")));
		panel.add(login);
		panel.add(new JLabel(getResourceBundle().getString("rightsframe.password")));
		panel.add(password);
		return panel;
	}
	
	private JScrollPane createRightsPanel() {
		JPanel rights = new JPanel();
		BoxLayout box = new BoxLayout(rights, BoxLayout.Y_AXIS);
		rights.setLayout(box);
		String r = getRights();
		System.out.println("right:" + r);
		int len = r.length();
		for (RightsTab tab : RightsTab.values()) {
			if (tab.getValue() < len) {
				final JCheckBox menu = new JCheckBox(tab.name());
				if (r.charAt(tab.getValue()) == '1') {
					menu.setSelected(true);
				} else {
					menu.setSelected(false);
				}
				rightCheckBoxMap.put(tab.getValue(), menu);
				int size = tab.getTabDetails().size();
				final JCheckBox[] items = new JCheckBox[size];
				JPanel underMenu = new JPanel(new GridLayout((size < 5?size:5), (size / 5) + 1));
				for (int i = 0; i < size; i++) {
					JCheckBox cb = new JCheckBox(tab.getTabDetails().get(i).name());
					if (r.charAt(tab.getTabDetails().get(i).getValue()) == '1') {
						cb.setSelected(true);
					} else {
						cb.setSelected(false);
					}
					cb.setEnabled(menu.isSelected());
					underMenu.add(cb);
					items[i] = cb;
					rightCheckBoxMap.put(tab.getTabDetails().get(i).getValue(), cb);
				}
				menu.addChangeListener(new ChangeListener() {
					
					@Override
					public void stateChanged(ChangeEvent arg0) {
						for (JCheckBox cb : items) {
							cb.setEnabled(menu.isSelected());
						}
					}
				});
				rights.add(menu);
				rights.add(underMenu);
			}
		}
		JScrollPane scroll = new JScrollPane(rights, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return scroll;
	}
	
	private String getRights() {
		if (userNumber != -1) {
			String s = userModel.getUser(userNumber).getRights();//TestLadderEncoding.decryptionPassword(userModel.getUser(userNumber).getLogin(), userModel.getUser(userNumber).getRights());
			return s;
		}
		return Database.DB2.defaultRights();
	}
	
	private String getBinaryRights() {
		StringBuilder str = new StringBuilder(Database.DB2.defaultRights());
		for (Integer i : rightCheckBoxMap.keySet()) {
			if (rightCheckBoxMap.get(i).isSelected()) {
				str.setCharAt(i, '1');
			} else {
				str.setCharAt(i, '0');
			}
		}
		return str.toString();
	}
	
	private JPanel createButtonPanel() {
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
		JButton okButton;
		if (userNumber == -1) {
			okButton = createAddUserButton();
		} else {
			okButton = createModifyUserButton();
		}
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ladderFrame.setEnabled(true);
				rightsFrame.dispose();
			}
		});
		
		JButton selectAll = new JButton("Select all");
		selectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (JCheckBox cb : rightCheckBoxMap.values()) {
					cb.setSelected(true);
				}
			}
		});
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(selectAll);
		if (userNumber == -1) {
			JButton clearButton = new JButton("Sel");
			buttonsPanel.add(clearButton);
		}
		return buttonsPanel;
	}
	
	private JButton createModifyUserButton() {
		JButton okButton = new JButton();
		okButton.setText(getResourceBundle().getString("rightsframe.button.modify"));
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String binaryRights = getBinaryRights();
				try {
					System.out.println("right before modif:");
					String passwd = new String(password.getPassword());
					if (passwd.isEmpty() || userModel.getUser(userNumber).getPassword().equals(passwd)) {
						passwd = null;
					}
					String rights = binaryRights;//User.getConvertBinaryStringToString(binaryRights);
					userModel.updateUser(passwd, rights, userNumber);
					ladderFrame.setEnabled(true);
					rightsFrame.dispose();
				} catch (LadderException e) {
					displayErrorMessage(rightsFrame, e.getMessage());
				}
			}
		});
		return okButton;
	}
	
	private JButton createAddUserButton() {
		JButton okButton = new JButton();
		okButton.setText(getResourceBundle().getString("rightsframe.button.add"));
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ("".equals(login.getText())) {
					displayErrorMessage(rightsFrame, "");
					return;
				}
				String binaryRights = getBinaryRights();
				System.out.println("rights before add:" + binaryRights);
				String rights = binaryRights;//User.getConvertBinaryStringToString(binaryRights);
				try {
					userModel.addUser(login.getText(), new String(password.getPassword()), rights);
				} catch (LadderException e) {
					displayErrorMessage(rightsFrame, e.getMessage());
				}
				ladderFrame.setEnabled(true);
				rightsFrame.dispose();
			}
		});
		return okButton;
	}
}
