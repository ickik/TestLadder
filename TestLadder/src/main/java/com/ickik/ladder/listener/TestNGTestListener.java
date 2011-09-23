package com.ickik.ladder.listener;

import java.util.List;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.google.common.collect.BiMap;
import com.ickik.ladder.db.DBConnection;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.model.TestCaseResultTableModel;
import com.ickik.ladder.model.TestCaseTableModel;
import com.ickik.ladder.model.TestClassTableModel;
import com.ickik.ladder.model.VersionsTableModel;
import com.ickik.ladder.model.XMLTestFileTableModel;
import com.ickik.ladder.values.Campaign;
import com.ickik.ladder.values.Result;
import com.ickik.ladder.values.State;
import com.ickik.ladder.values.Test;
import com.ickik.ladder.values.TestCase;
import com.ickik.ladder.values.TestClass;
import com.ickik.ladder.values.TestResult;
import com.ickik.ladder.values.TestType;
import com.ickik.ladder.values.User;
import com.ickik.ladder.values.Version;
import com.ickik.ladder.values.XMLFile;

/**
 * Listener that insert the result of every test in  the database defined
 * in the connection given by constructor's argument.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
public class TestNGTestListener extends TestListenerAdapter {

	private final DBConnection connection;
	private final BiMap<String, Long> stateMap;
	private final BiMap<String, Long> resultMap;
	private final long idCampaign;
	private final long idAutomated;
	private final long userId;
	private final TestClassTableModel testClassModel;
	private final XMLTestFileTableModel xmlTestFileModel;
	private final TestCaseTableModel testCaseModel;
	private final VersionsTableModel versionModel;
	private final TestCaseResultTableModel testResultModel;
	private final String version;
	
	public TestNGTestListener(DBConnection connection, String campaign, TestClassTableModel testClassModel, XMLTestFileTableModel xmlTestFileModel, TestCaseTableModel testCaseModel, long userId, VersionsTableModel versionModel, String version, TestCaseResultTableModel testResultModel) {
		if (connection == null) {
			throw new NullPointerException();
		}
		this.connection = connection;
		this.testClassModel = testClassModel;
		this.xmlTestFileModel = xmlTestFileModel;
		this.testCaseModel = testCaseModel;
		this.versionModel = versionModel;
		this.userId = userId;
		this.version = version;
		this.testResultModel = testResultModel;
		stateMap = connection.getStates();
		resultMap = connection.getTestResults();
		//Campaign c = new Campaign();
		//c.getUser(user);
		//c.setCampaign(campaign);
		//c.setExecuted(false);
		idCampaign = connection.insertCampaign(campaign, userId);
		idAutomated = connection.getTestTypes().get("Automated");
	}

	private TestCase getTestCase(ITestResult tr) {
		String xmlFile = tr.getTestClass().getXmlTest().getName();
		String className = tr.getTestClass().getRealClass().getName();
		String method = tr.getMethod().getMethodName();
		
		if (xmlTestFileModel.getMap().containsKey(xmlFile) && testClassModel.getMap().containsKey(className) && versionModel.getMap().containsKey(version)) {
			List<TestCase> testCaseList = connection.getTestCases(xmlTestFileModel.getMap().get(xmlFile), testClassModel.getMap().get(className), method, versionModel.getMap().get(version));
			if (testCaseList != null && !testCaseList.isEmpty()) {
				return testCaseList.get(0);
			}
		}
		try {
			createTestCase(tr);
		} catch (LadderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getTestCase(tr);
	}
	
	private void createTestCase(ITestResult tr) throws LadderException {
		String xmlFile = tr.getTestClass().getXmlTest().getName();
		String className = tr.getTestClass().getRealClass().getName();
		String method = tr.getMethod().getMethodName();
		
		TestCase testCase = new TestCase();
		if (tr.getMethod().getMethod().getAnnotation(java.lang.Deprecated.class) == null) {
			State s = new State();
			s.setState("Valide");
			s.setId(stateMap.get("Valide"));
			testCase.setState(s);
		} else {
			State s = new State();
			s.setState("Deprecated");
			s.setId(stateMap.get("Deprecated"));
			testCase.setState(s);
		}
		
		if (connection.getTestClassId(className) == -1) {
			testClassModel.addTestClass(className);
		} else {
			testClassModel.update();
		}
		TestClass tc = new TestClass();
		tc.setId(testClassModel.getMap().get(className));
		tc.setTestClass(className);
		testCase.setTestClass(tc);
		testCase.setMethod(method);
		
		if (connection.getXMLTestFileId(xmlFile) == -1) {
			xmlTestFileModel.addXMLTestFile(xmlFile);
		} else {
			xmlTestFileModel.update();
		}
		if (connection.getVersionId(version) == -1) {
			versionModel.addVersion(version);
		} else {
			versionModel.update();
		}
		Version v = new Version();
		v.setId(versionModel.getMap().get(version));
		v.setVersion(version);
		testCase.setVersion(v);
		testCase.setLastModifiedVersion(testCase.getVersion());
		User u = new User();
		u.setId(userId);
		testCase.setUserCreator(u);
		testCase.setUserModifier(u);
		XMLFile x = new XMLFile();
		x.setId(xmlTestFileModel.getMap().get(xmlFile));
		x.setXmlFile(xmlFile);
		testCase.setXmlFile(x);
		testCaseModel.addTestCase(testCase);
	}
	
	private void insertTestResult(ITestResult tr, TestResult testResult) {
		TestCase tc = getTestCase(tr);
		Test test = new Test();
		Campaign c = new Campaign();
		c.setId(idCampaign);
		c.setExecutedVersion(tc.getVersion());
		test.setCampaign(c);
		test.setTestCase(tc);
		User u = new User();
		u.setId(userId);
		test.setUserExecutor(u);
		Version v = new Version();
		v.setId(versionModel.getMap().get(version));
		v.setVersion(version);
		TestType tt = new TestType();
		tt.setId(idAutomated);
		test.setTestType(tt);
		Result r = new Result();
		r.setId(resultMap.get(testResult.toString()));
		r.setResult(testResult.toString());
		test.setResult(r);
		testResultModel.addTest(test);
	}
	
	@Override
	public void onTestSuccess(ITestResult tr) {
		insertTestResult(tr, TestResult.PASSED);
	}
	
	@Override
	public void onTestSkipped(ITestResult tr) {
		insertTestResult(tr, TestResult.SKIPPED);
	}
	
	@Override
	public void onTestFailure(ITestResult tr) {
		insertTestResult(tr, TestResult.FAILED);
	}
}
