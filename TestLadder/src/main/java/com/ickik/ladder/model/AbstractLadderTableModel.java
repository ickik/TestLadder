package com.ickik.ladder.model;

import javax.swing.table.AbstractTableModel;

import com.google.common.collect.BiMap;

/**
 * The abstract class which contains common code for
 * all model in the software.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
@SuppressWarnings("serial")
public abstract class AbstractLadderTableModel extends AbstractTableModel {

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
	
	/**
	 * Return the BiMap which contains the value associates
	 * to the id in the database.
	 * @return the BiMap which contains the value associates
	 * to the id in the database.
	 */
	public abstract BiMap<String, Long> getMap();
}
