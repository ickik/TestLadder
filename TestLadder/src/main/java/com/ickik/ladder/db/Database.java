package com.ickik.ladder.db;

import java.util.ArrayList;
import java.util.List;

import com.ickik.ladder.model.RightsFunction;

/**
 * This enum contains all databases compatible /
 * available with TestLadder.
 * @author Patrick Allgeyer
 * @version 0.1.000, 24/03/11
 */
public enum Database {

	/**
	 * IBM DB2 database.
	 */
	DB2 {
		@Override
		public String toString() {
			return "DB2";
		}

		@Override
		public String getDriverName() {
			return "com.ibm.db2.jcc.DB2Driver";
		}

		@Override
		public List<String> getCreationTablesRequestList() {
			ArrayList<String> list = new ArrayList<String>();
			list.add("CREATE TABLE Versions(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ), VERSION VARCHAR(12) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Categories(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),CATEGORY VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE States(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),STATE VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Results(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),RESULT VARCHAR(12) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Test_Types(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),TEST_TYPE VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE XML_Files(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),XML_FILE VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Test_Classes(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),TEST_CLASS VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Criticality(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),CRITICALITY VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Users(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),LOGIN VARCHAR(20) UNIQUE not null,PASSWORD VARCHAR(50) not null,RIGHTS VARCHAR(200) not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Campaigns(ID INTEGER not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ), CAMPAIGN VARCHAR(30) UNIQUE not null, ID_USER INTEGER,  EXECUTED SMALLINT WITH DEFAULT 0, ID_EXECUTED_VERSION INTEGER, PRIMARY KEY(ID), FOREIGN KEY(ID_USER) REFERENCES Users(ID), FOREIGN KEY(ID_EXECUTED_VERSION) REFERENCES Versions(ID))");
			list.add("CREATE TABLE Test_Plan(ID BIGINT not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),ID_CATEGORY INTEGER,ID_XMLFILE INTEGER,ID_TESTCLASS INTEGER,TEST_METHOD VARCHAR(30),DESCRIPTION VARCHAR(1000),ACTION VARCHAR(2800),ID_STATE INTEGER not null,ID_VERSION INTEGER not null,ID_LAST_MODIFIED_VERSION INTEGER not null,ID_CRITICALITY INTEGER,ID_USER_CREATOR INTEGER not null,DATE_CREATION DATE not null,ID_USER_MODIFIER INTEGER not null,DATE_MODIFICATION DATE not null,PRIMARY KEY(ID),FOREIGN KEY(ID_CATEGORY) REFERENCES Categories(ID),FOREIGN KEY(ID_XMLFILE) REFERENCES XML_Files(ID),FOREIGN KEY(ID_TESTCLASS) REFERENCES Test_Classes(ID),FOREIGN KEY(ID_STATE) REFERENCES States(ID),FOREIGN KEY(ID_VERSION) REFERENCES Versions(ID),FOREIGN KEY(ID_LAST_MODIFIED_VERSION) REFERENCES Versions(ID),FOREIGN KEY(ID_CRITICALITY) REFERENCES Criticality(ID))");
			list.add("CREATE TABLE Test_Results(ID BIGINT not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE ),ID_TESTCASE INTEGER not null,DATE_CREATION DATE not null,ID_USER_CREATOR INTEGER not null,ID_RESULT INTEGER not null,ID_TEST_TYPE INTEGER not null,ID_USER_EXECUTOR INTEGER,DATE_EXECUTION DATE, ID_CAMPAIGN INTEGER not null, PRIMARY KEY(ID),FOREIGN KEY(ID_TESTCASE) REFERENCES Test_Plan(ID),FOREIGN KEY(ID_RESULT) REFERENCES Results(ID),FOREIGN KEY(ID_TEST_TYPE) REFERENCES Test_Types(ID), FOREIGN KEY(ID_CAMPAIGN) REFERENCES Campaigns(ID), FOREIGN KEY(ID_USER_EXECUTOR) REFERENCES Users(ID), FOREIGN KEY(ID_USER_CREATOR) REFERENCES Users(ID))");
			return list;
		}

		@Override
		public String getDBDialect() {
			return "org.hibernate.dialect.DB2Dialect";
		}
	},
	
	POSTGRESQL {
		@Override
		public String toString() {
			return "POSTGRESQL";
		}

		@Override
		public String getDriverName() {
			return "org.postgresql.Driver";
		}

		@Override
		public List<String> getCreationTablesRequestList() {
			ArrayList<String> list = new ArrayList<String>();
			list.add("CREATE TABLE Versions(ID SERIAL, VERSION VARCHAR(12) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Categories(ID SERIAL,CATEGORY VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE States(ID SERIAL,STATE VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Results(ID SERIAL,RESULT VARCHAR(12) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Test_Types(ID SERIAL,TEST_TYPE VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE XML_Files(ID SERIAL,XML_FILE VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Test_Classes(ID SERIAL,TEST_CLASS VARCHAR(20) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Criticality(ID SERIAL,CRITICALITY VARCHAR(10) UNIQUE not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Users(ID SERIAL,LOGIN VARCHAR(20) UNIQUE not null,PASSWORD VARCHAR(50) not null,RIGHTS VARCHAR(200) not null,PRIMARY KEY(ID))");
			list.add("CREATE TABLE Campaigns(ID SERIAL, CAMPAIGN VARCHAR(30) UNIQUE not null, ID_USER INTEGER,  EXECUTED VARCHAR(5) DEFAULT false, ID_EXECUTED_VERSION INTEGER, PRIMARY KEY(ID), FOREIGN KEY(ID_USER) REFERENCES Users(ID), FOREIGN KEY(ID_EXECUTED_VERSION) REFERENCES Versions(ID))");
			list.add("CREATE TABLE Test_Plan(ID BIGSERIAL,ID_CATEGORY INTEGER,ID_XMLFILE INTEGER,ID_TESTCLASS INTEGER,TEST_METHOD VARCHAR(30),DESCRIPTION VARCHAR(1000),ACTION VARCHAR(2800),ID_STATE INTEGER not null,ID_VERSION INTEGER not null,ID_LAST_MODIFIED_VERSION INTEGER not null,ID_CRITICALITY INTEGER,ID_USER_CREATOR INTEGER not null,DATE_CREATION DATE not null,ID_USER_MODIFIER INTEGER not null,DATE_MODIFICATION DATE not null,PRIMARY KEY(ID),FOREIGN KEY(ID_CATEGORY) REFERENCES Categories(ID),FOREIGN KEY(ID_XMLFILE) REFERENCES XML_Files(ID),FOREIGN KEY(ID_TESTCLASS) REFERENCES Test_Classes(ID),FOREIGN KEY(ID_STATE) REFERENCES States(ID),FOREIGN KEY(ID_VERSION) REFERENCES Versions(ID),FOREIGN KEY(ID_LAST_MODIFIED_VERSION) REFERENCES Versions(ID),FOREIGN KEY(ID_CRITICALITY) REFERENCES Criticality(ID))");
			list.add("CREATE TABLE Test_Results(ID BIGSERIAL not null,ID_TESTCASE INTEGER not null,DATE_CREATION DATE not null,ID_USER_CREATOR INTEGER not null,ID_RESULT INTEGER not null,ID_TEST_TYPE INTEGER not null,ID_USER_EXECUTOR INTEGER,DATE_EXECUTION DATE, ID_CAMPAIGN INTEGER not null, PRIMARY KEY(ID),FOREIGN KEY(ID_TESTCASE) REFERENCES Test_Plan(ID),FOREIGN KEY(ID_RESULT) REFERENCES Results(ID),FOREIGN KEY(ID_TEST_TYPE) REFERENCES Test_Types(ID),FOREIGN KEY(ID_CAMPAIGN) REFERENCES Campaigns(ID), FOREIGN KEY(ID_USER_EXECUTOR) REFERENCES Users(ID), FOREIGN KEY(ID_USER_CREATOR) REFERENCES Users(ID))");
			return list;
		}

		@Override
		public String getDBDialect() {
			return "org.hibernate.dialect.PostgreSQLDialect";
		}
	};

	/**
	 * Return the name of the driver to load to connect
	 * to the server database.
	 * @return the name of the driver to load to connect
	 * to the server database.
	 */
	public abstract String getDriverName();
	
	/**
	 * Return a list which contains the requests to execute
	 * to create all table in the database.
	 * @return the list of requests.
	 */
	public abstract List<String> getCreationTablesRequestList();
	
	public abstract String getDBDialect();
	/**
	 * Get the default login for administrator.
	 * @return the default login for administrator.
	 */
	public String defaultAdminLogin() {
		return "admin";
	}
	
	/**
	 * Get the default password for administrator.
	 * @return the default password for administrator.
	 */
	public String defaultAdminPassword() {
		return "123admin";
	}
	
	/**
	 * Get the default rights (full) for administrator.
	 * @return the default rights for administrator.
	 */
	public String defaultAdminRights() {
		return initAndConvertRights('1');
	}
	
	/**
	 * Get the default rights (any) for all users.
	 * @return the defaults rights for users.
	 */
	public String defaultRights() {
		return initAndConvertRights('0');
	}
	
	private String initAndConvertRights(char c) {
		int len = RightsFunction.values()[RightsFunction.values().length - 1].getValue() + 1;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < len; i++) {
			str.append(c);
		}
		return str.toString();
	}
}
