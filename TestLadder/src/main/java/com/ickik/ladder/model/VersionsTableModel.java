package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;

/**
 * The VersionTableModel handle the version an user can insert.
 * This class extends an {@link AbstractLadderTableModel}.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
@SuppressWarnings("serial")
public class VersionsTableModel extends AbstractLadderTableModel {

	private BiMap<String, Long> versionMap;
	private final DBConnection connection;
	
	public VersionsTableModel(DBConnection connection) {
		this.connection = connection;
		versionMap = connection.getVersions();
	}
	
	/**
	 * Add a version (in parameter) to the database and refresh
	 * the table at the end of the method. It stores the version
	 * in the map to increase the display's speed.
	 * @param version the version to add.
	 * @return true if it was possible to insert the version,
	 * false if the version exists or if it was not possible
	 * to insert in database.
	 */
	public boolean addVersion(String version) {
		if (versionMap.containsKey(version) || connection.existVersion(version)) {
			return false;
		}
		long id = connection.insertVersion(version);
		versionMap.put(version, id);
		fireTableRowsInserted(versionMap.size() - 1, versionMap.size());
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return "Version";
	}

	@Override
	public int getRowCount() {
		return versionMap.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		List<String> list = new ArrayList<String>();
		list.addAll(versionMap.keySet());
		return list.get(row);
	}

	@Override
	public BiMap<String, Long> getMap() {
		return versionMap;
	}

	/**
	 * Update the map from the database.
	 */
	public void update() {
		versionMap = connection.getVersions();
	}
}
