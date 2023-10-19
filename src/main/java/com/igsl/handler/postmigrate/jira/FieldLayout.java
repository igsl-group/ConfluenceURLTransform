package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraFieldConfigurations;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class FieldLayout extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(FieldLayout.class);
	private static final String ID = "id";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
			new URLPattern().setPath("/secure/admin/ConfigureFieldLayout!default.jspa").setQuery(ID),
	};
	
	public FieldLayout(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraFieldConfigurations(), 
						CloudJiraFieldConfigurations.COL_DCID, 
						CloudJiraFieldConfigurations.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(ID, CloudJiraFieldConfigurations.class)
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
