package com.igsl.export.cloud.model;

public class JiraIssue {
	private String id;
	private String key;
	private JiraIssueFields fields;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public JiraIssueFields getFields() {
		return fields;
	}
	public void setFields(JiraIssueFields fields) {
		this.fields = fields;
	}
}
