package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraProjectCategories;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class BrowseProjects extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(BrowseProjects.class);
	private static final String PROJECT_CATEGORY = "selectedCategory";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern().setPath("/secure/BrowseProjects.jspa").setQuery("selectedCategory"),
	};
	
	public BrowseProjects(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraProjectCategories(), 
						CloudJiraProjectCategories.COL_DCID, 
						CloudJiraProjectCategories.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(BrowseProjects.class, PROJECT_CATEGORY, CloudJiraProjectCategories.class)
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
