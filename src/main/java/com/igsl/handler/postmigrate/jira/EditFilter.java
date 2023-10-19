package com.igsl.handler.postmigrate.jira;

import java.net.URI;
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
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern().setPath("/secure/EditFilter.jspa").setQuery(FILTER_ID),
			new URLPattern().setPath("/secure/EditFilter!default.jspa").setQuery(FILTER_ID),
	};
	
	public EditFilter(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraFilters(), 
						CloudJiraFilters.COL_DCID, 
						CloudJiraFilters.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(FILTER_ID, CloudJiraFilters.class)
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
