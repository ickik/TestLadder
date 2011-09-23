package com.ickik.ladder.values;

import java.util.Date;

public class TestCase {

	private long id;
	private Category category;
	private XMLFile xmlFile;
	private TestClass testClass;
	private String method = "";
	private String description = "";
	private String action = "";
	private State state;
	private Version version;
	private Version lastModifiedVersion;
	private Criticality criticality;
	private User userCreator;
	private Date dateCreation;
	private Date dateModification;
	private User userModifier;
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	public String getMethod() {
		return method;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAction() {
		return action;
	}
	
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	public Date getDateCreation() {
		return dateCreation;
	}
	
	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}
	public Date getDateModification() {
		return dateModification;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("ID :");
		str.append(getId());
		str.append("\n");
		str.append("Category :");
		str.append(getCategory().getCategory() + "(" + getCategory().getId() + ")");
		str.append("\n");
		str.append("XML Test file :");
		str.append(getXmlFile().getXmlFile() + "(" + getXmlFile().getId() + ")");
		str.append("\n");
		str.append("Test class :");
		str.append(getTestClass().getTestClass() + "(" + getTestClass().getId() + ")");
		str.append("\n");
		str.append("Method :");
		str.append(getMethod());
		str.append("\n");
		str.append("Description :");
		str.append(getDescription());
		str.append("\n");
		str.append("Test case :");
		str.append(getAction());
		str.append("\n");
		str.append("State :");
		str.append(getState().getState() + "(" + getState().getId() + ")");
		str.append("\n");
		str.append("Version :");
		str.append(getVersion().getVersion() + "(" + getVersion().getId() + ")");
		str.append("\n");
		str.append("Test case last modified version :");
		str.append(getLastModifiedVersion().getVersion() + "(" + getLastModifiedVersion().getId() + ")");
		return str.toString();
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Category getCategory() {
		return category;
	}
	public void setXmlFile(XMLFile xmlFile) {
		this.xmlFile = xmlFile;
	}
	public XMLFile getXmlFile() {
		return xmlFile;
	}
	public void setTestClass(TestClass testClass) {
		this.testClass = testClass;
	}
	public TestClass getTestClass() {
		return testClass;
	}
	public void setState(State state) {
		this.state = state;
	}
	public State getState() {
		return state;
	}
	public void setVersion(Version version) {
		this.version = version;
	}
	public Version getVersion() {
		return version;
	}
	public void setLastModifiedVersion(Version lastModifiedVersion) {
		this.lastModifiedVersion = lastModifiedVersion;
	}
	public Version getLastModifiedVersion() {
		return lastModifiedVersion;
	}
	public void setCriticality(Criticality criticality) {
		this.criticality = criticality;
	}
	public Criticality getCriticality() {
		return criticality;
	}
	public void setUserCreator(User userCreator) {
		this.userCreator = userCreator;
	}
	public User getUserCreator() {
		return userCreator;
	}
	public void setUserModifier(User userModifier) {
		this.userModifier = userModifier;
	}
	public User getUserModifier() {
		return userModifier;
	}
	
	@Override
	public TestCase clone() {
		TestCase testCase = new TestCase();
		testCase.setAction(action);
		testCase.setCategory(category);
		testCase.setCriticality(criticality);
		testCase.setDescription(description);
		testCase.setLastModifiedVersion(lastModifiedVersion);
		testCase.setMethod(method);
		testCase.setState(state);
		testCase.setTestClass(testClass);
		testCase.setVersion(version);
		testCase.setXmlFile(xmlFile);
		return testCase; 
	}
	
}
