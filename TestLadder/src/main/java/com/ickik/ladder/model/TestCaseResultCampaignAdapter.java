package com.ickik.ladder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.listener.UpdateStatsListener;
import com.ickik.ladder.values.CriticalityEnum;
import com.ickik.ladder.values.Result;
import com.ickik.ladder.values.Test;
import com.ickik.ladder.values.TestResult;
import com.ickik.ladder.values.Type;

/**
 * 
 * @author Patrick Allgeyer
 * @version 0.1.000, 11/04/11
 */
@SuppressWarnings("serial")
public class TestCaseResultCampaignAdapter extends AbstractTableModel {

	private final TestCaseResultTableModel testCaseResultModel;
	private final TestResultModel resultModel;
	private final CategoryTableModel categoryTableModel;
	private final List<Test> testCasesResultList;
	private final List<Test> filteredList = new ArrayList<Test>();
	private final DBConnection connection;
	private final List<UpdateStatsListener> updateStatsListenerList = new ArrayList<UpdateStatsListener>();
	private final Stats stats;
	
	public TestCaseResultCampaignAdapter(TestCaseResultTableModel testCaseResultModel, DBConnection connection, TestResultModel resultModel, CategoryTableModel categoryTableModel) {
		this.testCaseResultModel = testCaseResultModel;
		this.categoryTableModel = categoryTableModel;
		this.connection = connection;
		this.resultModel = resultModel;
		testCasesResultList = connection.getTestCasesResults();
		filteredList.addAll(testCasesResultList);
		stats = new Stats();
	}
	
	public void addUpdateStatsListener(UpdateStatsListener listener) {
		if (listener != null) {
			updateStatsListenerList.add(listener);
		}
	}
	
	public List<Test> getTestCaseByCampaignId(long id) {
		filteredList.clear();
		checkTestCaseResultList();
		for (Test tcr : testCasesResultList) {
			if (tcr.getCampaign().getId() == id) {
				filteredList.add(tcr);
			}
		}
		fireTableRowsInserted(0, filteredList.size());
		fireUpdateStatsListener();
		return filteredList;
	}
	
	private void checkTestCaseResultList() {
		int count = connection.getTestNumber();
		if (count > testCasesResultList.size()) {
			testCasesResultList.addAll(connection.getTest(testCasesResultList.size(), count - testCasesResultList.size()));
		}
	}
	
	@Override
	public int getColumnCount() {
		return testCaseResultModel.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return filteredList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Test test = filteredList.get(row);
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

	public String getColumnName(int column) {
		return testCaseResultModel.getColumnName(column);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 10 && getValueAt(rowIndex, columnIndex) == null || ((String) getValueAt(rowIndex, columnIndex)).isEmpty() || ((String) getValueAt(rowIndex, columnIndex)).equals(TestResult.NOT_EXECUTED.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 10) {
			Test t = filteredList.get(rowIndex);
			Result r = new Result();
			r.setResult((String) aValue);
			r.setId(resultModel.getTestResults().get(aValue));
			t.setResult(r);
			connection.updateTest(t);
		}
	}
	
	public Stats getStats() {
		return stats;
	}

	private void fireUpdateStatsListener() {
		for (UpdateStatsListener listener : updateStatsListenerList) {
			listener.update();
		}
	}
	
	public class Stats {
		
		private final Map<String, Integer> resultMap = new HashMap<String, Integer>();
		private final Map<String, Integer> typeMap = new HashMap<String, Integer>();
		private final Map<String, Integer> criticalityMap = new HashMap<String, Integer>();
		private final Map<String, Map<String, Integer>> criticalityResultmap = new HashMap<String, Map<String, Integer>>();
		private final Map<String, Map<String, Map<String, Integer>>> categoryCritcalityResultMap = new HashMap<String, Map<String, Map<String, Integer>>>();
		
		public Stats() {
			initMap();
			treatment();
			addUpdateStatsListener(new UpdateStatsListener() {
				@Override
				public void update() {
					treatment();
				}
			});
		}
		
		private void initMap() {
			for (TestResult res : TestResult.values()) {
				resultMap.put(res.toString(), 0);
			}
			for (Type t : Type.values()) {
				typeMap.put(t.toString(), 0);
			}
			CriticalityEnum[] tab = CriticalityEnum.values();
			for (CriticalityEnum criticality : CriticalityEnum.values()) {
				criticalityMap.put(criticality.toString(), 0);
				criticalityResultmap.put(criticality.toString(), getTestResultMap());
			}
			for (String cat : categoryTableModel.getMap().keySet()) {
				Map<String, Map<String, Integer>> map = getCriticalityInitMap();
				categoryCritcalityResultMap.put(cat, map);
			}
		}
		
		private Map<String, Map<String, Integer>> getCriticalityInitMap() {
			Map<String, Map<String, Integer>> criticalityMap = new HashMap<String, Map<String, Integer>>();
			for (TestResult criticality : TestResult.values()) {
				criticalityMap.put(criticality.toString(), getCriticalityMap2());
			}
			return criticalityMap;
		}
		
		private Map<String, Integer> getCriticalityMap2() {
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (CriticalityEnum res : CriticalityEnum.values()) {
				map.put(res.toString(), 0);
			}
			return map;
		}
		
		private Map<String, Integer> getTestResultMap() {
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (TestResult res : TestResult.values()) {
				map.put(res.toString(), 0);
			}
			return map;
		}
		
		private void reinitMap() {
			initMap();
		}
		
		public void treatment() {
			reinitMap();
			for (Test test : filteredList) {
				resultMap.put(test.getResult().getResult(), resultMap.get(test.getResult().getResult()) + 1);
				typeMap.put(test.getTestType().getTestType(), typeMap.get(test.getTestType().getTestType()) + 1);
				
				criticalityMap.put(test.getTestCase().getCriticality().getCriticality(), criticalityMap.get(test.getTestCase().getCriticality().getCriticality()) + 1);
				
				Map<String, Integer> map = criticalityResultmap.get(test.getTestCase().getCriticality().getCriticality());
				map.put(test.getResult().getResult(), map.get(test.getResult().getResult()) + 1);
			
				Map<String, Map<String, Integer>> tmpMap = categoryCritcalityResultMap.get(test.getTestCase().getCategory().getCategory());
				Map<String, Integer> tmpMap2 = tmpMap.get(test.getResult().getResult());
				tmpMap2.put(test.getTestCase().getCriticality().getCriticality(), tmpMap2.get(test.getTestCase().getCriticality().getCriticality()) + 1);
			}
		}
		
		public int getNumberOfTestExecuted() {
			return filteredList.size();
		}
		
		public Map<String, Integer> getResultMap() {
			return resultMap;
		}
		
		public Map<String, Integer> getTypeMap() {
			return typeMap;
		}
		
		public Map<String, Map<String, Integer>> getResultPerCriticalityMap() {
			return criticalityResultmap;
		}
		
		public Map<String, Integer> getCriticalityMap() {
			return criticalityMap;
		}
		
		public Map<String, Map<String, Map<String, Integer>>> getCategoryPerCriticalityResult() {
			return categoryCritcalityResultMap;
		}
	}
}
