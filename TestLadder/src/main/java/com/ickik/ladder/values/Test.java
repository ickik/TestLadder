package com.ickik.ladder.values;

import java.util.Date;

/**
 * Object that represents a test.
 * @author Patrick Allgeyer
 * @version 0.1.000
 */
public class Test {

	private long id;
	private TestCase testCase;
	private Result result;
	private Date dateCreation;
	private User userCreator;
	private TestType testType;
	private Campaign campaign;
	private User userExecutor;
	private Date executionDate;
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	public TestCase getTestCase() {
		return testCase;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setUserExecutor(User userExecutor) {
		this.userExecutor = userExecutor;
	}

	public User getUserExecutor() {
		return userExecutor;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Test) {
			Test t = (Test) obj;
			if (id == t.id) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public void setUserCreator(User userCreator) {
		this.userCreator = userCreator;
	}

	public User getUserCreator() {
		return userCreator;
	}
}
