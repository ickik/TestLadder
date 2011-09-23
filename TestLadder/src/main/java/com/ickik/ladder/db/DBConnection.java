package com.ickik.ladder.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.Type;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ickik.ladder.exception.LadderException;
import com.ickik.ladder.utils.TestLadderEncoding;
import com.ickik.ladder.values.Campaign;
import com.ickik.ladder.values.Category;
import com.ickik.ladder.values.Criticality;
import com.ickik.ladder.values.CriticalityEnum;
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
 * Connection to the database is managed by this class. It create and close the
 * connection and permit to send request.
 * @author Patrick Allgeyer
 * @version 0.1.000, 08/03/11.
 */
public class DBConnection {

	private final String login;
	private final String password;
	private final String server;
	private final int portNumber;
	private final String dataBase;
	private final Database db;
	private Session session;
	//private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
	
	/**
	 * Constructor which instantiates the connection and load the drivers.
	 * @param server the name of the server.
	 * @param portNumber the port to ask on the server.
	 * @param database the database which contains test campaign.
	 * @param login the login to access to the database.
	 * @param password the password associates to the login.
	 * @throws LadderException is throw if the database driver cannot be loaded
	 * or was not found on the system.
	 */
	public DBConnection(String server, int portNumber, String database, String login, String password, Database db) throws LadderException {
		this.login = login;
		this.password = password;
		this.server = server;
		this.portNumber = portNumber;
		this.dataBase = database;
		this.db = db;
		loadDriver();
		connection();
		boolean init = false;
		if (!initDataBase()) {
			init = true;
		}
		this.session = HibernateUtil.getSessionFactory().openSession();
		session.setFlushMode(FlushMode.COMMIT);
		if (init) {
			initField();
		}
	}
	
	private boolean initDataBase() throws LadderException {
		try {
			boolean isModify = false;
			Connection connection = DriverManager.getConnection ("jdbc:" + db.toString().toLowerCase() + "://" + server + ":" + Integer.toString(portNumber) + "/" + dataBase, login, password);
			Statement statement = connection.createStatement();
			for(String request : db.getCreationTablesRequestList()) {
				int index = request.indexOf('(');
				String table = request.substring(13, index);
				if (!existTable(connection, table)) {
					System.out.println(request);
					statement.execute(request);
					isModify = true;
				}
			}
			connection.close();
			return !isModify;
		} catch (SQLException e) {
			throw new LadderException("connection.initialization.database.creation_table");
		}
	}
	
	private void initField() throws LadderException {
		for(TestResult result : TestResult.values()) {
			insertResult(result.toString());
		}
//		insertResult("Not Executed");
//		insertResult("Passed");
//		insertResult("Skipped");
//		insertResult("Failed");
		insertState("Valide");
		insertState("Deprecated");
		for (CriticalityEnum criticality : CriticalityEnum.values()) {
			insertCriticality(criticality.toString());
		}
//		insertCriticality("Low");
//		insertCriticality("Medium");
//		insertCriticality("High");
//		insertCriticality("Critical");
		insertTestType("Automated");
		insertTestType("Manual");
		createUser(db.defaultAdminLogin(), db.defaultAdminPassword(), db.defaultAdminRights()/*User.getConvertBinaryStringToString(db.defaultAdminRights())*/);
		flush();
	}
	
	private void flush() {
		session.beginTransaction().commit();
		session.flush();
	}
	
	private void insertResult(String result) {
		Result r = new Result();
		r.setResult(result);
		session.save(r);
	}
	
	private void insertState(String state) {
		State s = new State();
		s.setState(state);
		session.save(s);
	}
	
	private void insertCriticality(String criticality) {
		Criticality c = new Criticality();
		c.setCriticality(criticality);
		session.save(c);
	}
	
	private void insertTestType(String testType) {
		TestType t = new TestType();
		t.setTestType(testType);
		session.save(t);
	}

	private boolean existTable(Connection connection, String table) throws LadderException {
		try {
			Statement statement = connection.createStatement();
			if (db == Database.DB2) {
				ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM SYSIBM.SYSTABLES WHERE SYSIBM.SYSTABLES.NAME like '" + table.toUpperCase() + "'");
				result.next();
				return 1 == result.getInt(1);
			} else if (db == Database.POSTGRESQL) {
				ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM pg_tables WHERE tablename like '" + table.toLowerCase() + "'");
				result.next();
				int i = result.getInt(1);
				return 1 == i;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new LadderException("connection.initialization.database.exists");
		}
	}
	
	private void loadDriver() throws LadderException {
		try {
			Class.forName(db.getDriverName());
		} catch (ClassNotFoundException e) {
			throw new LadderException("connection.driver.loading", e);
		}
	}
	
	/**
	 * Initialize the connection depending the driver loaded and
	 * if the server is reachable.
	 * @throws LadderException is throw when the server was not
	 * reachable or when it was not possible to connect to the
	 * server because of false login or password.
	 */
	public void connection() throws LadderException {
		try {
			InetAddress.getByName(server);
			
			this.session = HibernateUtil.getSessionFactory().openSession();
			session.setFlushMode(FlushMode.COMMIT);
		} catch (UnknownHostException e) {
			throw new LadderException("connection.connect.server.unavailable", e);
		}
	}
	
	public void close() {
		session.close();
	}
	
	public long getCategoryId(String category) {
		return getId("select c.id from Category c where c.category=?", category);
	}
	
	public long getVersionId(String version) {
		return getId("select v.id from Version v where v.version=?", version);
	}
	
	public long getXMLTestFileId(String xmlFile) {
		return getId("select x.id from XMLFile x where x.xmlFile=?", xmlFile);
	}
	
	public long getTestClassId(String testClass) {
		return getId("select t.id from TestClass t where t.testClass=?", testClass);
	}
	
	public long getTestCaseId(TestCase testCase) {
		List<TestCase> list = getTestCases(testCase, null, null, null, null);
		if (list == null || list.isEmpty() || list.size() > 1) {
			return -1;
		}
		return list.get(0).getId();
	}
	
	@SuppressWarnings("unchecked")
	private long getId(String request, String argument) {
		Query query = session.createQuery(request);
		query.setString(0, argument.trim());
		List<Long> list = query.list();
		if (list.isEmpty()) {
			return -1;
		}
		return list.get(0);
	}
	
	public BiMap<String, Long> getVersions() {
		BiMap<String, Long> versionMap = HashBiMap.create();
		Iterator<Version> iterator = getVersionIterator();
		while(iterator.hasNext()) {
			Version v = iterator.next();
			versionMap.put(v.getVersion(), v.getId());
		}
		return versionMap;
	}
	
	@SuppressWarnings("unchecked")
	private Iterator<Version> getVersionIterator() {
		return session.createQuery("from Version v order by v.version asc").iterate();
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getCategories() {
		BiMap<String, Long> categoryMap = HashBiMap.create();
		List<Category> list = session.createQuery("from Category").list();
		for(Category c : list) {
			categoryMap.put(c.getCategory(), c.getId());
		}
		return categoryMap;
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getXMLTestFiles() {
		BiMap<String, Long> xmlTestFileMap = HashBiMap.create();
		List<XMLFile> list = session.createQuery("from XMLFile").list();
		for(XMLFile x : list) {
			xmlTestFileMap.put(x.getXmlFile(), x.getId());
		}
		return xmlTestFileMap;
	}
	
	public BiMap<String, Long> getTestClasses() {
		BiMap<String, Long> testClassMap = HashBiMap.create();
		Iterator<TestClass> iterator = getTestClassIterator();
		while(iterator.hasNext()) {
			TestClass t = iterator.next();
			testClassMap.put(t.getTestClass(), t.getId());
		}
		return testClassMap;
	}
	
	@SuppressWarnings({"unchecked" })
	private Iterator<TestClass> getTestClassIterator() {
		return session.createQuery("from TestClass tc order by tc.testClass asc").iterate();
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getStates() {
		BiMap<String, Long> statesMap = HashBiMap.create();
		List<State> list = session.createQuery("from State").list();
		for(State s : list) {
			statesMap.put(s.getState(), s.getId());
		}
		return statesMap;
	}

	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getCriticalities() {
		BiMap<String, Long> criticalitiesMap = HashBiMap.create();
		List<Criticality> list = session.createQuery("from Criticality").list();
		for(Criticality s : list) {
			criticalitiesMap.put(s.getCriticality(), s.getId());
		}
		return criticalitiesMap;
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getTestResults() {
		BiMap<String, Long> testResultsMap = HashBiMap.create();
		List<Result> list = session.createQuery("from Result").list();
		for(Result s : list) {
			testResultsMap.put(s.getResult(), s.getId());
		}
		return testResultsMap;
	}
	
	public Long getTestResultId(TestResult result) {
		Query query = session.createQuery("from Result where result=?");
		query.setString(0, result.toString());
		if (!query.list().isEmpty()) {
			Result r = (Result) query.list().get(0);
			return r.getId();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Test> getTestCasesResults() {
		return session.createQuery("from Test").list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Test> getTestCasesResults(TestCase testCase, Test test, Calendar creationBeginDate, Calendar creationEndDate, Calendar modificationBeginDate, Calendar modificationEndDate, Calendar creationTestBeginDate, Calendar creationTestEndDate, Calendar modificationTestBeginDate, Calendar modificationTestEndDate) {
		List<Object> objectList = new ArrayList<Object>();
		List<Type> typeList = new ArrayList<Type>();
		StringBuilder queryRequest = new StringBuilder("select tr from TestCase AS tp ,Test AS tr where tr.testCase.id=tp.id");
		if (test.getResult() != null) {
			queryRequest.append(" and tr.result.id=?");
			objectList.add(test.getResult().getId());
			typeList.add(Hibernate.LONG);
		}
		if (test.getTestType() != null) {
			queryRequest.append(" and tr.testType.id=?");
			objectList.add(test.getTestType().getId());
			typeList.add(Hibernate.LONG);
		}
		if (test.getCampaign() != null && test.getCampaign().getExecutedVersion() != null) {
			queryRequest.append(" and tr.campaign.executedVersion.id=?");
			objectList.add(test.getCampaign().getExecutedVersion().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getCategory() != null) {
			queryRequest.append(" and tp.category.id=?");
			objectList.add(testCase.getCategory().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getXmlFile() != null) {
			queryRequest.append(" and tp.xmlFile.id=?");
			objectList.add(testCase.getXmlFile().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getTestClass() != null) {
			queryRequest.append(" and tp.testClass.id=?");
			objectList.add(testCase.getTestClass().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getState() != null) {
			queryRequest.append(" and tp.state.id=?");
			objectList.add(testCase.getState().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getVersion() != null) {
			queryRequest.append(" and tp.version.id=?");
			objectList.add(testCase.getVersion().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getLastModifiedVersion() != null) {
			queryRequest.append(" and tp.lastModifiedVersion.id=?");
			objectList.add(testCase.getLastModifiedVersion().getId());
			typeList.add(Hibernate.LONG);
		}
		if (test.getCampaign() != null) {
			queryRequest.append(" and tr.campaign.id=?");
			objectList.add(test.getCampaign().getId());
			typeList.add(Hibernate.LONG);
		}
		if (!testCase.getMethod().isEmpty()) {
			queryRequest.append(" and tp.method=?");
			objectList.add(testCase.getMethod());
			typeList.add(Hibernate.STRING);
		}
		if (creationBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateCreation >= ?");
			objectList.add(creationBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (creationEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateCreation <= ?");
			objectList.add(creationEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateModification >= ?");
			objectList.add(modificationBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateModification <= ?");
			objectList.add(modificationEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		
		if (creationTestBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tr.dateCreation >= ?");
			objectList.add(creationTestBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (creationTestEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tr.dateCreation <= ?");
			objectList.add(creationTestEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationTestBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tr.dateModification >= ?");
			objectList.add(modificationTestBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationTestEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tr.dateModification <= ?");
			objectList.add(modificationTestEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
//			if (!description.isEmpty()) {
//				textualSearch(query, description, "DESCRIPTION");
//			}
//			if (!action.isEmpty()) {
//				textualSearch(query, action, "ACTION");
//			}
		System.out.println(queryRequest);
		Query query = session.createQuery(queryRequest.toString());
		Type[] type = new Type[typeList.size()];
		query.setParameters(objectList.toArray(), typeList.toArray(type));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Test> getTestCasesResults(long categoryId, long xmlTestFileId, long testClassId, String methodName, long stateId, long versionId, long lastModifiedVersionId, long resultId, long softwareVersionId, long testTypeId, long campaignId) {
		List<Object> objectList = new ArrayList<Object>();
		List<Type> typeList = new ArrayList<Type>();
		StringBuilder queryRequest = new StringBuilder("select tr from TestCase AS tp ,Test AS tr where tr.testCase.id=tp.id");
		if (resultId > 0) {
			queryRequest.append(" and tr.result.id=?");
			objectList.add(resultId);
			typeList.add(Hibernate.LONG);
		}
		if (testTypeId > 0) {
			queryRequest.append(" and tr.testType.id=?");
			objectList.add(testTypeId);
			typeList.add(Hibernate.LONG);
		}
		if (softwareVersionId > 0) {
			queryRequest.append(" and tr.softwareVersion.id=?");
			objectList.add(softwareVersionId);
			typeList.add(Hibernate.LONG);
		}
		if (categoryId > 0) {
			queryRequest.append(" and tp.category.id=?");
			objectList.add(categoryId);
			typeList.add(Hibernate.LONG);
		}
		if (xmlTestFileId > 0) {
			queryRequest.append(" and tp.xmlFile.id=?");
			objectList.add(xmlTestFileId);
			typeList.add(Hibernate.LONG);
		}
		if (testClassId > 0) {
			queryRequest.append(" and tp.testClass.id=?");
			objectList.add(testClassId);
			typeList.add(Hibernate.LONG);
		}
		if (stateId > 0) {
			queryRequest.append(" and tp.state.id=?");
			objectList.add(stateId);
			typeList.add(Hibernate.LONG);
		}
		if (versionId > 0) {
			queryRequest.append(" and tp.version.id=?");
			objectList.add(versionId);
			typeList.add(Hibernate.LONG);
		}
		if (lastModifiedVersionId > 0) {
			queryRequest.append(" and tp.lastModifiedVersion.id=?");
			objectList.add(lastModifiedVersionId);
			typeList.add(Hibernate.LONG);
		}
		if (campaignId > 0) {
			queryRequest.append(" and tr.campaign.id=?");
			objectList.add(campaignId);
			typeList.add(Hibernate.LONG);
		}
		if (!methodName.isEmpty()) {
			queryRequest.append(" and tp.method=?");
			objectList.add(methodName);
			typeList.add(Hibernate.STRING);
		}
//			if (!description.isEmpty()) {
//				textualSearch(query, description, "DESCRIPTION");
//			}
//			if (!action.isEmpty()) {
//				textualSearch(query, action, "ACTION");
//			}
		System.out.println(queryRequest);
		Query query = session.createQuery(queryRequest.toString());
		Type[] type = new Type[typeList.size()];
		query.setParameters(objectList.toArray(), typeList.toArray(type));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TestCase> getTestCases(long xmlTestFileId, long testClassId, String methodName, long versionId) {
		Query query = session.createQuery("select tp from TestCase AS tp where tp.xmlFile.id=? and tp.testClass.id=? and tp.version.id=? and tp.method=?");
		query.setLong(0, xmlTestFileId);
		query.setLong(1, testClassId);
		query.setLong(2, versionId);
		query.setString(3, methodName);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getTestTypes() {
		BiMap<String, Long> testResultsMap = HashBiMap.create();
		List<TestType> list = session.createQuery("from TestType").list();
		for (TestType t : list) {
			testResultsMap.put(t.getTestType(), t.getId());
		}
		return testResultsMap;
	}
	
	public Long getTestTypeId(com.ickik.ladder.values.Type type) {
		Query query = session.createQuery("from TestType where testType=?");
		query.setString(0, type.toString());
		TestType tt = (TestType) query.list().get(0);
		return tt.getId();
	}
	
	public Long insertTestResult(Test test) {
		java.util.Date d = new java.util.Date();
		Date date = new Date(d.getTime());
		test.setDateCreation(date);
		test.setExecutionDate(date);
		long id = (Long) session.save(test);
		flush();
		return id;
	}
	
	public Long insertTest(Test test) {
		java.util.Date d = new java.util.Date();
		Date date = new Date(d.getTime());
		test.setDateCreation(date);
		long id = (Long) session.save(test);
		flush();
		return id;
	}

	public long insertVersion(String version) {
		Version v = new Version();
		v.setVersion(version.trim());
		long id = (Long) session.save(v);
		flush();
		return id;
	}
	
	public long insertCategory(String category) {
		Category c = new Category();
		c.setCategory(category.trim());
		long id = (Long) session.save(c);
		flush();
		return id;
	}
	
	public long insertXMLTestFile(String xmlFile) {
		XMLFile x = new XMLFile();
		x.setXmlFile(xmlFile);
		long id = (Long) session.save(x);
		flush();
		return id;
	}
	
	public long insertTestClass(String testClass) {
		TestClass tc = new TestClass();
		tc.setTestClass(testClass.trim());
		long id = (Long) session.save(tc);
		flush();
		return id;
	}

	public boolean existTestCase(TestCase testCase) {
		return getTestCaseId(testCase) != -1;
	}

	public long insertTestCase(TestCase testCase) {
		testCase.setDateCreation(getDate());
		testCase.setDateModification(getDate());
		long id = (Long) session.save(testCase);
		flush();
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public List<TestCase> getTestCases() {
		Query query = session.createQuery("from TestCase");
		return query.list();
	}

	/**
	 * Update the test case given in argument depending the ID of the
	 * test case found in database. All data are updated.
	 * @param testCase the test case to update.
	 * @throws LadderException is thrown if the update has occur an error.
	 */
	public void updateTestCase(TestCase testCase) throws LadderException {
		testCase.setDateModification(getDate());
		session.update(testCase);
		flush();
	}
	
	private Date getDate() {
		java.util.Date d = new java.util.Date();
		return new Date(d.getTime());
	}
	
	@SuppressWarnings("unchecked")
	public List<TestCase> getTestCases(TestCase testCase, Calendar creationBeginDate, Calendar creationEndDate, Calendar modificationBeginDate, Calendar modificationEndDate) {
		List<Object> objectList = new ArrayList<Object>();
		List<Type> typeList = new ArrayList<Type>();
		StringBuilder queryRequest = new StringBuilder("");
		if (testCase.getCategory() != null && testCase.getCategory().getId() > 0) {
			queryRequest.append("tp.category.id = ?");
			objectList.add(testCase.getCategory().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getXmlFile() != null && testCase.getXmlFile().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.xmlFile.id = ?");
			objectList.add(testCase.getXmlFile().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getTestClass() != null && testCase.getTestClass().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.testClass.id = ?");
			objectList.add(testCase.getTestClass().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getState() != null && testCase.getState().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.state.id = ?");
			objectList.add(testCase.getState().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getVersion() != null && testCase.getVersion().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.version.id = ?");
			objectList.add(testCase.getVersion().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getLastModifiedVersion() != null && testCase.getLastModifiedVersion().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.lastModifiedVersion.id = ?");
			objectList.add(testCase.getLastModifiedVersion().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getCriticality() != null && testCase.getCriticality().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.criticality.id = ?");
			objectList.add(testCase.getCriticality().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getUserCreator() != null && testCase.getUserCreator().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.userCreator.id = ?");
			objectList.add(testCase.getUserCreator().getId());
			typeList.add(Hibernate.LONG);
		}
		if (testCase.getUserModifier() != null && testCase.getUserModifier().getId() > 0) {
			addCondition(queryRequest);
			queryRequest.append("tp.userModifier.id = ?");
			objectList.add(testCase.getUserModifier().getId());
			typeList.add(Hibernate.LONG);
		}
		if (creationBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateCreation >= ?");
			objectList.add(creationBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (creationEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateCreation <= ?");
			objectList.add(creationEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationBeginDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateModification >= ?");
			objectList.add(modificationBeginDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (modificationEndDate != null) {
			addCondition(queryRequest);
			queryRequest.append("tp.dateModification <= ?");
			objectList.add(modificationEndDate.getTime());
			typeList.add(Hibernate.DATE);
		}
		if (!testCase.getMethod().isEmpty()) {
			addCondition(queryRequest);
			queryRequest.append("tp.method LIKE ?");
			objectList.add(testCase.getMethod());
			typeList.add(Hibernate.STRING);
			//textualSearch(query, method, "TEST_METHOD");
		}
//		if (!testCase.getMethod().isEmpty()) {
//			addCondition(queryRequest);
//			queryRequest.append("tp.method ~ ?");
//			objectList.add(testCase.getMethod());
//			typeList.add(Hibernate.STRING);
//			//textualSearch(query, method, "TEST_METHOD");
//		}
//		if (!testCase.getDescription().isEmpty()) {
//			addCondition(queryRequest);
//			queryRequest.append("tp.description ~ ?");
//			objectList.add(testCase.getDescription());
//			typeList.add(Hibernate.STRING);
//			//textualSearch(query, description, "DESCRIPTION");
//		}
//		if (!testCase.getAction().isEmpty()) {
//			addCondition(queryRequest);
//			queryRequest.append("tp.action ~ ?");
//			objectList.add(testCase.getAction());
//			typeList.add(Hibernate.STRING);
//			//textualSearch(query, action, "ACTION");
//		}
		Query query;
		if (queryRequest.length() > 0) {
			System.out.println("select * from test_plan where " + queryRequest.toString());
			query = session.createQuery("from TestCase tp where " + queryRequest.toString());
		} else {
			query = session.createQuery("from TestCase");
		}
		Type[] typeArray = new Type[typeList.size()];
		typeArray = typeList.toArray(typeArray);
		query.setParameters(objectList.toArray(), typeArray);
		return query.list();
	}
	
//	private void textualSearch(StringBuilder query, String textes, String field) {
//		addCondition(query);
//		String[] wordsArray = textes.split("\\s");
//		int len = wordsArray.length;
//		for (int i = 0; i < len ; i++) {
//			query.append(field);
//			query.append(" LIKE '%");
//			query.append(wordsArray[i]);
//			query.append("%'");
//			if (i < len - 1) {
//				query.append(" or ");
//			}
//		}
//	}
	
	private void addCondition(StringBuilder query) {
		if (query.length() > 0) {
			query.append(" and ");
		}
	}

	/**
	 * Return a TestCase object depending the id stored in database.
	 * @param idTestCase the id of the Test case to find.
	 * @return the Test case associates to the id in database.
	 * @throws LadderException is throw if the request has
	 * failed.
	 */
	public TestCase getTestCase(long idTestCase) throws LadderException {
		return (TestCase) session.get(TestCase.class, idTestCase);
	}

	/**
	 * Verify the existence of the user and if the password
	 * correspond to the stored. The user is identified by the
	 * couple of login.password.
	 * @param login the login of the user.
	 * @param password the password associates to the user.
	 * @return the User if one was found, null otherwise.
	 * @throws LadderException if the request has encount a
	 * problem during execution.
	 */
	@SuppressWarnings("unchecked")
	public User checkUser(String login, String password) throws LadderException {
		String hashPwd = TestLadderEncoding.encryptionPassword(password);
		Query query = session.createQuery("from User where login=? and password=?");
		query.setString(0, login);
		query.setString(1, hashPwd);
		List<User> list = query.list();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkUser(String login) {
		Query query = session.createQuery("from User where login=?");
		query.setString(0, login);
		List<User> list = query.list();
		if (list != null && !list.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public void createUser(String login, String password, String rights) throws LadderException {
		String hashPwd = TestLadderEncoding.encryptionPassword(password);
		String hashRights = rights;//TestLadderEncoding.encryptionPassword(rights);
		User u = new User();
		u.setLogin(login);
		u.setPassword(hashPwd);
		u.setRights(hashRights);
		session.save(u);
		flush();
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		return session.createQuery("from User").list();
	}
	
	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getUsersMap() {
		BiMap<String, Long> map = HashBiMap.create();
		List<User> list = session.createQuery("from User").list();
		for (User user : list) {
			map.put(user.getLogin(), user.getId());
		}
		return map;
	}

//	public User removeUser(String login) {
//		try {
//			PreparedStatement statement = connection.prepareStatement("DELETE FROM USERS WHERE login=?");
//			statement.setString(1, login);
//			statement.execute();
//		} catch (SQLException e) {
//			throw new LadderException("");
//		}
//	}
	
	/**
	 * Remove an user from the table Users. Be careful, this
	 * operation cannot be undone.
	 * @param id the id of the user.
	 * @throws LadderException if the delete was not possible, because
	 * of non existence of the id in the database.
	 */
	public void removeUser(long id) throws LadderException {
		User u = new User();
		u.setId(id);
		session.delete(u);
		flush();
	}

	public void updateUser(User user) {
		session.update(user);
		flush();
	}

	public long getCampaignId(String campaign) {
		return getId("SELECT c.id from Campaign c where c.campaign like ?", campaign);
	}
	
	public long insertCampaign(String campaign, long userId) {
		Campaign c = new Campaign();
		c.setCampaign(campaign);
		User u = new User();
		u.setId(userId);
		c.setUser(u);
		c.setExecuted(false);
		long id = (Long) session.save(c);
		flush();
		return id;
	}
	
	public long insertCampaign(String campaign, User user) {
		Campaign c = new Campaign();
		c.setCampaign(campaign);
		c.setUser(user);
		c.setExecuted(false);
		long id = (Long) session.save(c);
		flush();
		return id;
	}
	
//	public void insertHistorical() {
//		
//	}

	@SuppressWarnings("unchecked")
	public BiMap<String, Long> getCampaignMap() {
		BiMap<String, Long> campaignMap = HashBiMap.create();
		List<Campaign> list = session.createQuery("from Campaign").list();
		for (Campaign c : list) {
			campaignMap.put(c.getCampaign(), c.getId());
		}
		return campaignMap;
	}

	@SuppressWarnings("unchecked")
	public void updateCampaign(String campaign, boolean executed) {
		Query query = session.createQuery("from Campaign where campaign=?");
		query.setString(0, campaign);
		List<Campaign> list = query.list();
		if (list != null && !list.isEmpty()) {
			Campaign c = list.get(0);
			c.setExecuted(executed);
			session.update(c);
			flush();
		}
	}

	@SuppressWarnings("unchecked")
	public boolean existVersion(String version) {
		Query query = session.createQuery("select count(v) from Version v where v.version like ?");
		query.setString(0, version);
		Iterator<Long> it = query.list().iterator();
		return it.next() != 0;
	}

	public int getTestNumber() {
		Query query = session.createQuery("select count(*) from Test");
		return new Long((Long) query.list().get(0)).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Test> getTest(int index, int length) {
		Query query = session.createQuery("from Test").setFirstResult(index).setMaxResults(length);
		return query.list();
	}

	public void updateTest(Test test) {
		session.update(test);
		flush();
	}
	
}
