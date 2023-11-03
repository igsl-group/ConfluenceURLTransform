package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraIssueTypes;
import com.igsl.export.cloud.CloudJiraProjects;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class CreateIssue extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(CreateIssue.class);
	private static final String PROJECT_ID = "pid";
	private static final String ISSUE_TYPE = "issuetype";
	
	@Override
	protected URLPattern[] getPatterns() {
		String basePath = config.getUrlTransform().getJiraToBasePath();
		return new URLPattern[] {
			new URLPattern()
				.setPath(basePath + "/secure/CreateIssue.jspa")
				.setQuery(PROJECT_ID, ISSUE_TYPE),
			new URLPattern()
				.setPath(basePath + "/secure/CreateIssue!default.jspa")
				.setQuery(PROJECT_ID, ISSUE_TYPE)
		};
	}
	
	public CreateIssue(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraProjects(), 
						CloudJiraProjects.COL_DCID, 
						CloudJiraProjects.COL_CLOUDID),
					new MappingSetting(
						new CloudJiraIssueTypes(),
						CloudJiraIssueTypes.COL_DCID,
						CloudJiraIssueTypes.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(CreateIssue.class, PROJECT_ID, null, CloudJiraProjects.class),
					new ParamSetting(CreateIssue.class, ISSUE_TYPE, null, CloudJiraIssueTypes.class)
				));
	}
}
