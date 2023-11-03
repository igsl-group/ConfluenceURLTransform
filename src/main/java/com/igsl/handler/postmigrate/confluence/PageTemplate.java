package com.igsl.handler.postmigrate.confluence;

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
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPath(
					config.getUrlTransform().getConfluenceToBasePath() + 
					"/pages/templates2/viewpagetemplate.action")
				.setQuery(ENTITY_ID)
		};
	}
	
	public PageTemplate(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluencePageTemplates(), 
						CloudConfluencePageTemplates.COL_DCID, 
						CloudConfluencePageTemplates.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(PageTemplate.class, ENTITY_ID, null, CloudConfluencePageTemplates.class)
				));
	}
}
