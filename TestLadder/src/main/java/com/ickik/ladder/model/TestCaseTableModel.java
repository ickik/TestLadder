package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.values.Campaign;
import com.ickik.ladder.values.Result;
import com.ickik.ladder.values.Test;
import com.ickik.ladder.values.TestCase;
import com.ickik.ladder.values.TestResult;
import com.ickik.ladder.values.TestType;
import com.ickik.ladder.values.Type;
import com.ickik.ladder.values.User;
import com.ickik.ladder.values.Version;

/**
 * Table model which insert, modify and format the data
 * displayed for every test cases. Insertion and
 * modification could be possible in the dedicated views.
 * In the same views, the user can search a test case
 * depending most criterias.
 * @author Patrick Allgeyer
 * @version 0.1.000, 27/04/11
 */
@SuppressWarnings("serial")
public class TestCaseTableModel extends AbstractTableModel {

	private final DBConnection connection;
	private final VersionsTableModel versiontableModel;
	private final CategoryTableModel categoryTableModel;
	private final XMLTestFileTableModel xmlTestFileTableModel;
	private final TestClassTableModel testClassTableModel;
	private final List<TestCase> testCaseList;
	private final BiMap<String, Long> statesMap;
	private final BiMap<String, Long> criticityMap;
	private final List<Boolean> choiceList = new ArrayList<Boolean>();
	
	public TestCaseTableModel(DBConnection connection, VersionsTableModel versiontableModel, CategoryTableModel categoryTableModel, XMLTestFileTableModel xmlTestFileTableModel, TestClassTableModel testClassTableModel) {
		this.connection = connection;
		this.versiontableModel = versiontableModel;
		this.categoryTableModel = categoryTableModel;
		this.xmlTestFileTableModel = xmlTestFileTableModel;
		this.testClassTableModel = testClassTableModel;
		statesMap = connection.getStates();
		criticityMap = connection.getCriticalities();
		testCaseList = connection.getTestCases();
		initChoiceList();
	}
	
	public boolean addTestCase(TestCase testCase) {
		if (connection.existTestCase(testCase)) {
			return false;
		}
		connection.insertTestCase(testCase);
		testCaseList.add(testCase);
		choiceList.add(false);
		fireTableRowsInserted(testCaseList.size() - 1, testCaseList.size());
		return true;
	}
	
	private void initChoiceList() {
		choiceList.clear();
		int len = testCaseList.size();
		for (int i = 0; i < len; i++) {
			choiceList.add(false);
		}
	}
	
	public boolean updateTestCase(TestCase testCase) throws LadderException {
		if (connection.existTestCase(testCase)) {
			return false;
		}
		connection.updateTestCase(testCase);
		for (int i = 0; i < testCaseList.size(); i++) {
			if (testCaseList.get(i).getId() == testCase.getId()) {
				testCaseList.set(i, testCase);
				fireTableRowsUpdated(i, i);
				break;
			}
		}
		return true;
	}
	
	public void search(TestCase testCase, Calendar creationBeginDate, Calendar creationEndDate, Calendar modificationBeginDate, Calendar modificationEndDate) {
		testCaseList.clear();
		testCaseList.addAll(connection.getTestCases(testCase, creationBeginDate, creationEndDate, modificationBeginDate, modificationEndDate));
		initChoiceList();
		fireTableRowsInserted(0, testCaseList.size());
	}
	
	public BiMap<String, Long> getStatesMap() {
		return statesMap;
	}
	
	public BiMap<String, Long> getCriticitiesMap() {
		return criticityMap;
	}
	
	@Override
	public int getColumnCount() {
		return 15;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 1:
			return "Category";
		case 2:
			return "XML File";
		case 3 :
			return "Test Class";
		case 4 :
			return "Test Method";
		case 5 :
			return "Description";
		case 6 :
			return "Action";
		case 7 :
			return "State";
		case 8 :
			return "Criticality";
		case 9 :
			return "Software Test Version";
		case 10 :
			return "Modified in Version";
		case 11 :
			return "Created by";
		case 12 :
			return "Creation date";
		case 13 :
			return "Modified by";
		case 14 :
			return "Modification date";
		}
		return "";
	}

	@Override
	public int getRowCount() {
		return testCaseList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		TestCase tc = testCaseList.get(row);
		switch(column) {
		case 0:
			return new Boolean(choiceList.get(row));
		case 1:
			if (tc.getCategory() != null) {
				return tc.getCategory().getCategory();//categoryTableModel.getMap().inverse().get(tc.getCategory().getId());
			}
			return new String();
		case 2:
			return tc.getXmlFile().getXmlFile();//xmlTestFileTableModel.getMap().inverse().get(tc.getXmlFile().getId());
		case 3 :
			return tc.getTestClass().getTestClass();//testClassTableModel.getMap().inverse().get(tc.getTestClass().getId());
		case 4 :
			return tc.getMethod();
		case 5 :
			return tc.getDescription();
		case 6 :
			return tc.getAction();
		case 7 :
			return tc.getState().getState();//statesMap.inverse().get(tc.getState().getId());
		case 8 :
			//if (tc.getCriticality() != null) {
				return tc.getCriticality().getCriticality();
			//}
			//return new String();
		case 9 :
			return tc.getVersion().getVersion();//versiontableModel.getMap().inverse().get(tc.getVersion().getId());
		case 10 :
			return tc.getLastModifiedVersion().getVersion();//versiontableModel.getMap().inverse().get(tc.getLastModifiedVersion().getId());
		case 11 :
			return tc.getUserCreator().getLogin();
		case 12 :
			return tc.getDateCreation();
		case 13 :
			return tc.getUserModifier().getLogin();
		case 14 :
			return tc.getDateModification();
		}
		return null;
	}
	
	public TestCase getTestCase(int row) {
		return testCaseList.get(row);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return Boolean.class;
		default :
			return String.class;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (aValue instanceof Boolean) {
				Boolean b = (Boolean) aValue;
				choiceList.set(rowIndex, b);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}

	public void duplicateTestCase(String version, User user) {
		long id = versiontableModel.getMap().get(version);
		Version v = new Version();
		v.setId(id);
		v.setVersion(version);
		for (int i = 0; i < choiceList.size(); i++) {
			if (choiceList.get(i)) {
				TestCase tc = testCaseList.get(i).clone();
				tc.setVersion(v);
				tc.setUserCreator(user);
				tc.setUserModifier(user);
				if (!addTestCase(tc)) {
					System.out.println("pas possible!!!");
				}
			}
		}
	}
	
	public boolean canDuplicate() {
		return choiceList.indexOf(new Boolean(true)) > -1;
	}
	
	public void createTestCampaign(Campaign campaign, User user) {
		long resultId = connection.getTestResultId(TestResult.NOT_EXECUTED);
		Result r = new Result();
		r.setId(resultId);
		long typeId = connection.getTestTypeId(Type.MANUAL);
		TestType tt = new TestType();
		tt.setId(typeId);
		for (int i = 0; i < choiceList.size(); i++) {
			if (choiceList.get(i)) {
				TestCase tc = testCaseList.get(i);
				campaign.setExecutedVersion(tc.getVersion());
				Test test = new Test();
				test.setTestCase(tc);
				test.setCampaign(campaign);
				test.setResult(r);
				test.setTestType(tt);
				test.setUserCreator(user);
				connection.insertTest(test);
			}
		}
	}

}
