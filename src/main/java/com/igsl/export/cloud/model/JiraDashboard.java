package com.igsl.export.cloud.model;

public class JiraDashboard {
	private String id;
	private String name;
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
	public JiraUser getOwner() {
		return owner;
	}
	public void setOwner(JiraUser owner) {
		this.owner = owner;
	}
}
