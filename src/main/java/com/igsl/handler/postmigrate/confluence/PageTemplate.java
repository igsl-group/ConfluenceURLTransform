package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluencePageTemplates;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class PageTemplate extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(PageTemplate.class);
	private static final String ENTITY_ID = "entityId";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/pages/templates2/viewpagetemplate.action").setQuery(ENTITY_ID)
	};
	
	public PageTemplate(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluencePageTemplates(), 
						CloudConfluencePageTemplates.COL_DCID, 
						CloudConfluencePageTemplates.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(ENTITY_ID, CloudConfluencePageTemplates.class)
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
