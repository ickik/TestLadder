package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;

public class XMLTestFileTableModel extends AbstractLadderTableModel {

	private BiMap<String, Long> xmlTestFileMap;
	private final DBConnection connection;
	
	public XMLTestFileTableModel(DBConnection connection) {
		this.connection = connection;
		xmlTestFileMap = connection.getXMLTestFiles();
	}
	
	public boolean addXMLTestFile(String xmlFile) {
		if (xmlTestFileMap.containsKey(xmlFile)) {
			return false;
		}
		long id = connection.insertXMLTestFile(xmlFile);
		xmlTestFileMap.put(xmlFile, id);
		fireTableRowsInserted(xmlTestFileMap.size() - 1, xmlTestFileMap.size());
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "XML Files";
	}

	@Override
	public int getRowCount() {
		return xmlTestFileMap.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<String> list = new ArrayList<String>();
		list.addAll(xmlTestFileMap.keySet());
		return list.get(row);
	}

	@Override
	public BiMap<String, Long> getMap() {
		return xmlTestFileMap;
	}

	public void update() {
		xmlTestFileMap = connection.getXMLTestFiles();
	}
}
