package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraFilters;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class EditFilter extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(EditFilter.class);
	private static final String FILTER_ID = "filterId";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/secure/EditFilter.jspa").setQuery(FILTER_ID),
			new URLPattern().setPath("/secure/EditFilter!default.jspa").setQuery(FILTER_ID),
		};
	}
	
	public EditFilter(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				config.getUrlTransform().getJiraToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraFilters(), 
						CloudJiraFilters.COL_DCID, 
						CloudJiraFilters.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(EditFilter.class, FILTER_ID, CloudJiraFilters.class)
				));
	}
}
