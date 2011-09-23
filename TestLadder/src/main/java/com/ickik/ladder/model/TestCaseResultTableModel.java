package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.values.Test;
import com.ickik.ladder.values.TestCase;

@SuppressWarnings("serial")
public class TestCaseResultTableModel extends AbstractTableModel {

	private final DBConnection connection;
	private final List<Test> testCaseResultList;
	//private HashMap<Long, TestCase> testCaseMap;
	private final BiMap<String, Long> testTypesMap;
	
	public TestCaseResultTableModel(DBConnection connection) {
		this.connection = connection;
		testTypesMap = connection.getTestTypes();
		testCaseResultList = new ArrayList<Test>();
		//testCaseMap = new HashMap<Long, TestCase>();
	}
	
	public boolean addTest(Test test) {
		connection.insertTestResult(test);
		return true;
	}
	
	public void search(TestCase testCase, Test test, Calendar creationBeginDate, Calendar creationEndDate, Calendar modificationBeginDate, Calendar modificationEndDate, Calendar creationTestBeginDate, Calendar creationTestEndDate, Calendar modificationTestBeginDate, Calendar modificationTestEndDate) {
		testCaseResultList.clear();
		testCaseResultList.addAll(connection.getTestCasesResults(testCase, test, creationBeginDate, creationEndDate, modificationBeginDate, modificationEndDate, creationTestBeginDate, creationTestEndDate, modificationTestBeginDate, modificationTestEndDate));
		fireTableRowsInserted(0, testCaseResultList.size());
	}
	
	public void search(long categoryId, long xmlTestFileId, long testClassId, String methodName, long stateId, long versionId, long lastModifiedVersionId, long resultId, long cfirstVersionId, long typeTestId, long idCampaign) {
		testCaseResultList.clear();
		//testCaseMap.clear();
		testCaseResultList.addAll(connection.getTestCasesResults(categoryId, xmlTestFileId, testClassId, methodName, stateId, versionId, lastModifiedVersionId, resultId, cfirstVersionId, typeTestId, idCampaign));
		//for (Test tcr : testCaseResultList) {
			//testCaseMap.put(tcr.getTestCase().getId(), tcr.getTestCase());
			//fireTableRowsInserted(testCaseResultList.size() -1, testCaseResultList.size());
		//}
		fireTableRowsInserted(0, testCaseResultList.size());
	}
	
	@Override
	public int getColumnCount() {
		return 13;
	}

	@Override
	public int getRowCount() {
		return testCaseResultList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Test test = testCaseResultList.get(row);
		switch (column) {
		case 0:
			if (test.getTestCase().getCategory() != null) {
				return test.getTestCase().getCategory().getCategory();//categoryTableModel.getMap().inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getCategory().getId());
			}
			return new String();
		case 1:
			return test.getTestCase().getXmlFile().getXmlFile();//xmlTestFileTableModel.getMap().inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getXmlFile().getId());
		case 2:
			return test.getTestCase().getTestClass().getTestClass();//testClassTableModel.getMap().inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getTestClass().getId());
		case 3:
			return test.getTestCase().getMethod();
		case 4:
			return test.getTestCase().getDescription();
		case 5:
			return test.getTestCase().getAction();
		case 6:
			return test.getTestType().getTestType();//testTypesMap.inverse().get(testCaseResultList.get(row).getTestType().getId());
		case 7 :
			return test.getTestCase().getCriticality().getCriticality();
		case 8:
			return test.getTestCase().getState().getState();//statesMap.inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getState().getId());
		case 9 :
			return test.getTestCase().getVersion().getVersion();///versiontableModel.getMap().inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getVersion().getId());
		case 10 :
			return test.getTestCase().getLastModifiedVersion().getVersion(); //versiontableModel.getMap().inverse().get(testCaseMap.get(testCaseResultList.get(row).getTestCase().getId()).getLastModifiedVersion().getId());
		case 11 :
			return test.getResult().getResult();//resultMap.inverse().get(testCaseResultList.get(row).getResult().getId());
		case 12 :
			return test.getDateCreation();
		case 13 :
			if (test.getCampaign().getExecutedVersion() != null) {
				return test.getCampaign().getExecutedVersion().getVersion();
			}
			return new String();//testCaseResultList.get(row).getVersion().getVersion();//versiontableModel.getMap().inverse().get(testCaseResultList.get(row).getVersion().getId());
		case 14 :
			return test.getUserExecutor().getId();
		case 15 :
			return test.getExecutionDate();
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return "Category";
		case 1:
			return "XML File";
		case 2 :
			return "Test Class";
		case 3 :
			return "Test Method";
		case 4 :
			return "Description";
		case 5 :
			return "Action";
		case 6 :
			return "Type of the test";
		case 7 :
			return "Criticality";
		case 8 :
			return "State of the test";
		case 9 :
			return "CFirst Test Version";
		case 10 :
			return "Modified in Version";
		case 11 :
			return "Result of the test";
		case 12 :
			return "Date of execution";
		case 13 :
			return "CFirst version";
		case 14 :
			return "Executed by";
		case 15 :
			return "Executed on";
		}
		return "";
	}
	
	public BiMap<String, Long> getTestTypesMap() {
		return testTypesMap;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 11 && testCaseResultList.get(row).getResult() == null) {
			return true;
		}
		return false;
	}
}
