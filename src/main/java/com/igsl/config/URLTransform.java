package com.igsl.config;

import java.util.ArrayList;
import java.util.List;

public class URLTransform implements ConfigInterface {
	private boolean performUpdate;
	// Default scheme if the URL has none
	private String defaultScheme;
	// Accepted schemes regular expression
	private String fromSchemeRegex;
	// Convert to scheme
	private String toScheme;
	// Confluence DC host and port, e.g. localhost:8090
	private String confluenceFromHost;
	// Confluence DC base path, e.g. /wiki
	private String confluenceFromBasePath;
	// Confluence Cloud host, e.g. kcwong.atlassian.net
	private String confluenceToHost;
	// Confluence Cloud base path, e.g. /wiki
	private String confluenceToBasePath;
	// Jira DC host and port, e.g. localhost:8080
	private String jiraFromHost;
	// Jira DC base path, e.g. /
	private String jiraFromBasePath;
	// Jira Cloud host, e.g. kcwong.atlassian.net
	private String jiraToHost;
	// Jira Cloud base path, e.g. /jira
	private String jiraToBasePath;
	@Override
	public List<String> validate() {
		List<String> messages = new ArrayList<>();
		if (defaultScheme == null || defaultScheme.isBlank()) {
			messages.add("defaultScheme is empty. Please provide a default URL scheme, e.g. \"https\"");
		}
		if (fromSchemeRegex == null || fromSchemeRegex.isBlank()) {
			messages.add("fromSchemeRegex is empty. Please provide regular expression for URL schemes, e.g. \"https?\"");
		}
		if (toScheme == null || toScheme.isBlank()) {
			messages.add("toScheme is empty. Please provide target URL scheme, e.g. \"https\"");
		}
		if (confluenceFromHost == null || confluenceFromHost.isBlank()) {
			messages.add("confluenceFromHost is empty. Please provide Confluence hostname, e.g. \"localhost:8090\"");
		}
		if (confluenceFromBasePath == null) {
			messages.add("confluenceFromBasePath is not specified. Please provide Confluece base path, e.g. \"/wiki\"");
		}
		if (confluenceToHost == null || confluenceToHost.isBlank()) {
			messages.add("confluenceToHost is empty. Please provide Confluence Cloud domain, e.g. \"kcwong.atlassian.net\"");
		}
		if (confluenceToBasePath == null) {
			messages.add("confluenceToBasePath is not specified. Please provide Confluence Cloud base path, e.g. \"/wiki\"");
		}
		if (jiraFromHost == null || jiraFromHost.isBlank()) {
			messages.add("jiraFromHost is empty. Please provide Jira hostname, e.g. \"localhost:8080\"");
		}
		if (jiraFromBasePath == null) {
			messages.add("jiraFromBasePath is not specified. Please provide Jira base path, e.g. \"/jira\"");
		}
		if (jiraToHost == null || jiraToHost.isBlank()) {
			messages.add("jiraToHost is empty. Please provide Jira Cloud domain, e.g. \"kcwong.atlassian.net\"");
		}
		if (jiraToBasePath == null) {
			messages.add("jiraToBasePath is not specified. Please provide Jira Cloud base path, e.g. \"/jira\"");
		}
		return messages;
	}
	public String getDefaultScheme() {
		return defaultScheme;
	}
	public void setDefaultScheme(String defaultScheme) {
		this.defaultScheme = defaultScheme;
	}
	public String getFromSchemeRegex() {
		return fromSchemeRegex;
	}
	public void setFromSchemeRegex(String fromSchemeRegex) {
		this.fromSchemeRegex = fromSchemeRegex;
	}
	public String getToScheme() {
		return toScheme;
	}
	public void setToScheme(String toScheme) {
		this.toScheme = toScheme;
	}
	public String getConfluenceFromHost() {
		return confluenceFromHost;
	}
	public void setConfluenceFromHost(String confluenceFromHost) {
		this.confluenceFromHost = confluenceFromHost;
	}
	public String getConfluenceFromBasePath() {
		return confluenceFromBasePath;
	}
	public void setConfluenceFromBasePath(String confluenceFromBasePath) {
		this.confluenceFromBasePath = confluenceFromBasePath;
	}
	public String getConfluenceToHost() {
		return confluenceToHost;
	}
	public void setConfluenceToHost(String confluenceToHost) {
		this.confluenceToHost = confluenceToHost;
	}
	public String getConfluenceToBasePath() {
		return confluenceToBasePath;
	}
	public void setConfluenceToBasePath(String confluenceToBasePath) {
		this.confluenceToBasePath = confluenceToBasePath;
	}
	public String getJiraFromHost() {
		return jiraFromHost;
	}
	public void setJiraFromHost(String jiraFromHost) {
		this.jiraFromHost = jiraFromHost;
	}
	public String getJiraFromBasePath() {
		return jiraFromBasePath;
	}
	public void setJiraFromBasePath(String jiraFromBasePath) {
		this.jiraFromBasePath = jiraFromBasePath;
	}
	public String getJiraToHost() {
		return jiraToHost;
	}
	public void setJiraToHost(String jiraToHost) {
		this.jiraToHost = jiraToHost;
	}
	public String getJiraToBasePath() {
		return jiraToBasePath;
	}
	public void setJiraToBasePath(String jiraToBasePath) {
		this.jiraToBasePath = jiraToBasePath;
	}
	public boolean isPerformUpdate() {
		return performUpdate;
	}
	public void setPerformUpdate(boolean performUpdate) {
		this.performUpdate = performUpdate;
	}
}
