package com.igsl.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Connections implements ConfigInterface {
	@JsonIgnore
	private Connection jiraConnection;
	@JsonIgnore
	private Connection confluenceConnection;
	// Database connection information
	private String confluenceConnectionString;
	private String confluenceUser;
	private String confluencePassword;
	private String jiraConnectionString;
	private String jiraUser;
	private String jiraPassword;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (confluenceConnectionString == null) {
			messages.add("confluenceConnectionString is not specified. Please provide JDBC connection string to Confluence.");
		}
		if (jiraConnectionString == null) {
			messages.add("jiraConnectionString is not specified. Please provide JDBC connection string to Jira.");
		}
		return messages;
	}
	public Connection getJiraConnection() {
		return jiraConnection;
	}
	public void setJiraConnection(Connection jiraConnection) {
		this.jiraConnection = jiraConnection;
	}
	public Connection getConfluenceConnection() {
		return confluenceConnection;
	}
	public void setConfluenceConnection(Connection confluenceConnection) {
		this.confluenceConnection = confluenceConnection;
	}
	public String getConfluenceConnectionString() {
		return confluenceConnectionString;
	}
	public void setConfluenceConnectionString(String confluenceConnectionString) {
		this.confluenceConnectionString = confluenceConnectionString;
	}
	public String getConfluenceUser() {
		return confluenceUser;
	}
	public void setConfluenceUser(String confluenceUser) {
		this.confluenceUser = confluenceUser;
	}
	public String getConfluencePassword() {
		return confluencePassword;
	}
	public void setConfluencePassword(String confluencePassword) {
		this.confluencePassword = confluencePassword;
	}
	public String getJiraConnectionString() {
		return jiraConnectionString;
	}
	public void setJiraConnectionString(String jiraConnectionString) {
		this.jiraConnectionString = jiraConnectionString;
	}
	public String getJiraUser() {
		return jiraUser;
	}
	public void setJiraUser(String jiraUser) {
		this.jiraUser = jiraUser;
	}
	public String getJiraPassword() {
		return jiraPassword;
	}
	public void setJiraPassword(String jiraPassword) {
		this.jiraPassword = jiraPassword;
	}
}
