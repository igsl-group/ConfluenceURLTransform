package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class ObjectExport implements ConfigInterface {
	private String confluenceScheme;
	private String confluenceHost;
	private String jiraScheme;
	private String jiraHost;
	private String jiraUser;
	private String confluenceUser;
	private String jiraPassword;
	private String confluencePassword;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (confluenceScheme == null || confluenceScheme.isBlank()) {
			messages.add("confluenceScheme must be specified (http|https)");
		}
		if (confluenceHost == null || confluenceHost.isBlank()) {
			messages.add("confluenceHost must be specified (e.g. localhost:8080/wiki)");
		}
		if (jiraScheme == null || jiraScheme.isBlank()) {
			messages.add("jiraScheme must be specified (http|https)");
		}
		if (jiraHost == null || jiraHost.isBlank()) {
			messages.add("jiraHost must be specified (e.g. localhost:8080/jira)");
		}
		return messages;
	}
	public String getConfluenceScheme() {
		return confluenceScheme;
	}
	public void setConfluenceScheme(String confluenceScheme) {
		this.confluenceScheme = confluenceScheme;
	}
	public String getConfluenceHost() {
		return confluenceHost;
	}
	public void setConfluenceHost(String confluenceHost) {
		this.confluenceHost = confluenceHost;
	}
	public String getJiraScheme() {
		return jiraScheme;
	}
	public void setJiraScheme(String jiraScheme) {
		this.jiraScheme = jiraScheme;
	}
	public String getJiraHost() {
		return jiraHost;
	}
	public void setJiraHost(String jiraHost) {
		this.jiraHost = jiraHost;
	}
	public String getJiraUser() {
		return jiraUser;
	}
	public void setJiraUser(String jiraUser) {
		this.jiraUser = jiraUser;
	}
	public String getConfluenceUser() {
		return confluenceUser;
	}
	public void setConfluenceUser(String confluenceUser) {
		this.confluenceUser = confluenceUser;
	}
	public String getJiraPassword() {
		return jiraPassword;
	}
	public void setJiraPassword(String jiraPassword) {
		this.jiraPassword = jiraPassword;
	}
	public String getConfluencePassword() {
		return confluencePassword;
	}
	public void setConfluencePassword(String confluencePassword) {
		this.confluencePassword = confluencePassword;
	}
}
