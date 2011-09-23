package com.ickik.ladder.model;

import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.utils.TestLadderEncoding;
import com.ickik.ladder.values.User;

/**
 * User model which add and display all users.
 * @author Patrick Allgeyer
 * @version 0.1.000, 14/03/11
 */
@SuppressWarnings("serial")
public final class UserModel extends AbstractLadderTableModel {

	private final DBConnection connection;
	private final List<User> usersList;
	private final BiMap<String, Long> userMap;
	
	public UserModel(DBConnection connection) {
		this.connection = connection;
		usersList = connection.getUsers();
		userMap = connection.getUsersMap();
	}
	
	public boolean addUser(String login, String password, String rights) throws LadderException {
		if (connection.checkUser(login)) {
			return false;
		}
		connection.createUser(login, password, rights);
		usersList.add(connection.checkUser(login, password));
		fireTableRowsInserted(usersList.size() - 1, usersList.size());
		return true;
	}
	
	public void updateUser(String password, String rights, int index) throws LadderException {
		User user = usersList.get(index);
		if (password != null) {
			user.setPassword(TestLadderEncoding.encryptionPassword(password));
			//usersList.get(index).setPassword(TestLadderEncoding.encryptionPassword(login, rights));
		}
		user.setRights(rights);
		connection.updateUser(user);
	}
	
	public boolean removeUser(int index) throws LadderException {
		User user = usersList.get(index);
		connection.removeUser(user.getId());
		fireTableRowsDeleted(0, usersList.size());
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int arg0) {
		switch(arg0) {
		case 0:
			return "Login";
		case 1:
			return "Password";
		case 2:
			return "Rights";
		}
		return "";
	}

	@Override
	public int getRowCount() {
		return usersList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch(column) {
		case 0:
			return usersList.get(row).getLogin();
		case 1:
			return usersList.get(row).getPassword();
		case 2:
			return usersList.get(row).getRights();
		}
		return null;
	}
	
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	public User getUser(int index) {
		return usersList.get(index);
	}

	@Override
	public BiMap<String, Long> getMap() {
		return userMap;
	}

}
