package com.igsl.handler.postmigrate.jira;

import java.net.URI;
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
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/CreateIssue.jspa").setQuery(PROJECT_ID, ISSUE_TYPE),
		new URLPattern().setPath("/secure/CreateIssue!default.jspa").setQuery(PROJECT_ID, ISSUE_TYPE),
	};
	
	public CreateIssue(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
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
					new ParamSetting(PROJECT_ID, CloudJiraProjects.class),
					new ParamSetting(ISSUE_TYPE, CloudJiraIssueTypes.class)
				));
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		for (URLPattern path : PATTERNS) {
			if (path.match(uri)) {
				return true;
			}
		}
		return false;
	}
}
