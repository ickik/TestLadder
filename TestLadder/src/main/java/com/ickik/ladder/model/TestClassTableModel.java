package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;

@SuppressWarnings("serial")
public class TestClassTableModel extends AbstractLadderTableModel {

	private BiMap<String, Long> testClassMap;
	private final DBConnection connection;
	
	public TestClassTableModel(DBConnection connection) {
		this.connection = connection;
		testClassMap = connection.getTestClasses();
	}
	
	public boolean addTestClass(String testClass) {
		if (testClassMap.containsKey(testClass)) {
			return false;
		}
		long id = connection.insertTestClass(testClass);
		testClassMap.put(testClass, id);
		fireTableRowsInserted(testClassMap.size() - 1, testClassMap.size());
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "Classes";
	}

	@Override
	public int getRowCount() {
		return testClassMap.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<String> list = new ArrayList<String>();
		list.addAll(testClassMap.keySet());
		return list.get(row);
	}

	@Override
	public BiMap<String, Long> getMap() {
		return testClassMap;
	}

	public void update() {
		testClassMap = connection.getTestClasses();
	}
}
