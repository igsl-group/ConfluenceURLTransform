package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraDashboards;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class Dashboard extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Dashboard.class);
	private static final String PAGEID = "pageId";
	private static final String SELECT_PAGEID = "selectPageId";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/secure/Dashboard.jspa").setQuery(PAGEID, SELECT_PAGEID),
		};
	}
	
	public Dashboard(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				config.getUrlTransform().getJiraToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraDashboards(), 
						CloudJiraDashboards.COL_DCID, 
						CloudJiraDashboards.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(Dashboard.class, PAGEID, CloudJiraDashboards.class),
					new ParamSetting(Dashboard.class, SELECT_PAGEID, CloudJiraDashboards.class)
				));
	}
}
