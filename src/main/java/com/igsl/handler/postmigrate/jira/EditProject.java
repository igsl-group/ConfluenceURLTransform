package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraProjects;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class EditProject extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(EditProject.class);
	private static final String PROJECT_ID = "pid";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/secure/project/EditProject!default.jspa").setQuery(PROJECT_ID),
		};
	}
	
	public EditProject(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				config.getUrlTransform().getJiraToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraProjects(), 
						CloudJiraProjects.COL_DCID, 
						CloudJiraProjects.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(EditProject.class, PROJECT_ID, CloudJiraProjects.class)
				));
	}
}
