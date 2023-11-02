package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraProjectVersions;
import com.igsl.export.cloud.CloudJiraProjects;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class ReleaseNote extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(ReleaseNote.class);
	private static final String PROJECT_ID = "projectId";
	private static final String VERSION = "version";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/secure/ReleaseNote.jspa").setQuery(PROJECT_ID, VERSION),
		};
	}
	
	public ReleaseNote(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				config.getUrlTransform().getJiraToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraProjects(), 
						CloudJiraProjects.COL_DCID, 
						CloudJiraProjects.COL_CLOUDID),
					new MappingSetting(
						new CloudJiraProjectVersions(),
						CloudJiraProjectVersions.COL_DCID,
						CloudJiraProjectVersions.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(ReleaseNote.class, PROJECT_ID, CloudJiraProjects.class),
					new ParamSetting(ReleaseNote.class, VERSION, CloudJiraProjectVersions.class)
				));
	}
}
