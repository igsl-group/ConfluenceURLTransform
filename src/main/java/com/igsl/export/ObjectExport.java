package com.igsl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igsl.config.Config;

/*
 * Confluence: 
 * User = /display/~<UserName>
 * 
 * Jira: 
 * Dashboard = https://jira.pccwglobal.com/secure/Dashboard.jspa?selectPageId=13902 or pageId
 * Report = https://jira.pccwglobal.com/secure/PortfolioReportView.jspa?r=989GC#plan
 * RapidBoard = https://jira.pccwglobal.com/secure/RapidBoard.jspa?projectKey=CCSUP&amp;rapidView=125
 * CustomFieldContext = https://jira.pccwglobal.com/secure/admin/ConfigureFieldLayout!default.jspa?id=10000
 * ProjectCategory = https://jira.pccwglobal.com/secure/BrowseProjects.jspa?selectedCategory=11103&amp;selectedProjectType=all&amp;sortColumn=name&amp;sortOrder=ascending
 * Project = https://jira.pccwglobal.com/secure/CreateIssue.jspa?pid=13754&amp;issuetype=11300
 * IssueType = https://jira.pccwglobal.com/secure/CreateIssue.jspa?pid=13754&amp;issuetype=11300
 * JQL = https://jira.pccwglobal.com/issues?jql=project%20%3D%20CCENG%20AND%20issuetype%20in%20standardIssueTypes()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20created%20DESC%2C%20priority%20DESC%2C%20updated%20DESC
 * Filter = https://jira.pccwglobal.com/secure/IssueNavigator.jspa?reset=true&amp;jqlQuery=filter%3D%22CCENGOpenNotVeracode%22+
 * ProjectVersion = https://jira.pccwglobal.com/secure/ReleaseNote.jspa?projectId=10902&amp;version=13235
 * Avatar = https://jira.pccwglobal.com/secure/viewavatar?size=xsmall&amp;avatarId=10303&amp;avatarType=issuetype
 */
public abstract class ObjectExport {
	protected Path getOutputPath(Config config) throws IOException {
		Path dir = Paths.get(config.getObjectExport().getOutputDirectory());
		if (!Files.exists(dir)) {
			dir = Files.createDirectories(dir);
		} else {
			if (!Files.isDirectory(dir)) {
				dir = Paths.get(".");
			}
		}
		return Paths.get(dir.toFile().getAbsolutePath(), this.getClass().getSimpleName() + ".csv");
	}
	public abstract Path exportObjects(Config config) throws Exception;
}
