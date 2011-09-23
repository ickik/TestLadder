package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.exception.LadderException;

/**
 * Table Model that handle the categories. A category
 * is a representation of a classification of the test.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
@SuppressWarnings("serial")
public class CategoryTableModel extends AbstractLadderTableModel {

	private final DBConnection connection;
	private BiMap<String, Long> categoryMap;
	
	/**
	 * Constructor of the model. It needs a DBConnection
	 * object which is the connection to the database.
	 * @param connection the connection to the database.
	 */
	public CategoryTableModel(DBConnection connection) {
		this.connection = connection;
		categoryMap = connection.getCategories();
	}
	
	/**
	 * Add a category in the database and in the JTable.
	 * @param category the category to add.
	 * @return true if it was possible to insert the new
	 * category in the Table and in the database;
	 * false if the category already exists.
	 * @throws LadderException is throw if it was not possible
	 * to insert the category in the database.
	 */
	public boolean addCategory(String category) throws LadderException {
		if (categoryMap.containsKey(category)) {
			return false;
		}
		long id = connection.insertCategory(category);
		categoryMap.put(category, id);
		fireTableRowsInserted(categoryMap.size() - 1, categoryMap.size());
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "Categories";
	}

	@Override
	public int getRowCount() {
		return categoryMap.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<String> list = new ArrayList<String>();
		list.addAll(categoryMap.keySet());
		return list.get(row);
	}

	@Override
	public BiMap<String, Long> getMap() {
		return categoryMap;
	}
}
