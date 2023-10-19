package com.igsl.handler.postmigrate.jira;

import java.net.URI;
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
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern().setPath("/secure/ReleaseNote.jspa").setQuery(PROJECT_ID, VERSION),
	};
	
	public ReleaseNote(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
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
					new ParamSetting(PROJECT_ID, CloudJiraProjects.class),
					new ParamSetting(VERSION, CloudJiraProjectVersions.class)
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
