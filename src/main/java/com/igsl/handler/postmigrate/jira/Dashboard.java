package com.igsl.handler.postmigrate.jira;

import java.net.URI;
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
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/Dashboard.jspa").setQuery(PAGEID, SELECT_PAGEID),
	};
	
	public Dashboard(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraDashboards(), 
						CloudJiraDashboards.COL_DCID, 
						CloudJiraDashboards.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(PAGEID, CloudJiraDashboards.class),
					new ParamSetting(SELECT_PAGEID, CloudJiraDashboards.class)
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
