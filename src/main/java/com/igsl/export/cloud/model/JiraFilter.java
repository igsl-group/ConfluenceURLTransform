package com.igsl.export.cloud.model;

public class JiraFilter {
	private String id;
	private String name;
	private String jql;
	private String description;
	private JiraUser owner;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public JiraUser getOwner() {
		return owner;
	}
	public void setOwner(JiraUser owner) {
		this.owner = owner;
	}
}
