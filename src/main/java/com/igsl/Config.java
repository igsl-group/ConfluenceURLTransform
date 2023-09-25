package com.igsl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Config {
	private static final String NEWLINE = System.getProperty("line.separator");	
	@JsonIgnore
	private Connection jiraConnection;
	@JsonIgnore
	private Connection confluenceConnection;
	private String outputDirectory;
	private String postMigrateListBaseName;
	private String urlListBaseName;
	private String urlErrorBaseName;
	private String urlIgnoredBaseName;
	// Switch to enable/disable update of BODYCONTENT table
	private boolean performUpdate;
	// Database connection information
	private String confluenceConnectionString;
	private String confluenceUser;
	private String confluencePassword;
	private String jiraConnectionString;
	private String jiraUser;
	private String jiraPassword;
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
	// List of class name of Handler implementations, they will be checked in sequence
	private List<String> handlers;
	public void validate() throws Exception {
		List<String> messages = new ArrayList<>();
		if (postMigrateListBaseName == null) {
			messages.add("postMigrateListBaseName is not specified. Please provide a file name.");
		}
		if (urlListBaseName == null) {
			messages.add("urlListBaseName is not specified. Please provide a file name.");
		}
		if (urlIgnoredBaseName == null) {
			messages.add("urlIgnoredBaseName is not specified. Please provide a file name.");
		}
		if (urlErrorBaseName == null) {
			messages.add("urlErrorBaseName is not specified. Please provide a file name.");
		}
		if (confluenceConnectionString == null) {
			messages.add("confluenceConnectionString is not specified. Please provide JDBC connection string to Confluence.");
		}
		if (jiraConnectionString == null) {
			messages.add("jiraConnectionString is not specified. Please provide JDBC connection string to Jira.");
		}
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
		if (handlers.isEmpty()) {
			messages.add("No handlers are defined.");
		}
		if (!messages.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String msg : messages) {
				sb.append(msg).append(NEWLINE);
			}
			throw new Exception(sb.toString());
		}
	}
	// Generated
	public boolean isPerformUpdate() {
		return performUpdate;
	}
	public void setPerformUpdate(boolean performUpdate) {
		this.performUpdate = performUpdate;
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
	public List<String> getHandlers() {
		return handlers;
	}
	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
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
	public String getDefaultScheme() {
		return defaultScheme;
	}
	public void setDefaultScheme(String defaultScheme) {
		this.defaultScheme = defaultScheme;
	}
	public String getPostMigrateListBaseName() {
		return postMigrateListBaseName;
	}
	public void setPostMigrateListBaseName(String postMigrateListBaseName) {
		this.postMigrateListBaseName = postMigrateListBaseName;
	}
	public String getUrlListBaseName() {
		return urlListBaseName;
	}
	public void setUrlListBaseName(String urlListBaseName) {
		this.urlListBaseName = urlListBaseName;
	}
	public String getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	public String getUrlIgnoredBaseName() {
		return urlIgnoredBaseName;
	}
	public void setUrlIgnoredBaseName(String urlIgnoredBaseName) {
		this.urlIgnoredBaseName = urlIgnoredBaseName;
	}
	public String getUrlErrorBaseName() {
		return urlErrorBaseName;
	}
	public void setUrlErrorBaseName(String urlErrorBaseName) {
		this.urlErrorBaseName = urlErrorBaseName;
	}
}
