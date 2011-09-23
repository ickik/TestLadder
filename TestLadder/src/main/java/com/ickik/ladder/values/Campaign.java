package com.ickik.ladder.values;

public class Campaign {

	private long id;
	private String campaign;
	private User user;
	private boolean executed;
	private Version executedVersion;
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	public boolean isExecuted() {
		return executed;
	}
	public void setExecutedVersion(Version executedVersion) {
		this.executedVersion = executedVersion;
	}
	public Version getExecutedVersion() {
		return executedVersion;
	}
	
}
