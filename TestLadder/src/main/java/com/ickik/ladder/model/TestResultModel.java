package com.ickik.ladder.model;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;

/**
 * 
 * @author Patrick Allgeyer
 * @version 0.1.000, 28/03/11
 */
public class TestResultModel {

	private final DBConnection connection;
	private BiMap<String, Long> testResultMap;
	
	public TestResultModel(DBConnection connection) {
		this.connection = connection;
		loadTestResults();
	}

	private void loadTestResults() {
		testResultMap = connection.getTestResults();
	}
	
	public BiMap<String, Long> getTestResults() {
		return testResultMap;
	}
}
