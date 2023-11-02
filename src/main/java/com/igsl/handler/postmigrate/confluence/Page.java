package com.igsl.handler.postmigrate.confluence;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluencePages;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class Page extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Page.class);
	private static final String PAGE_ID = "fromPageId";
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/createpage.action").setQuery(PAGE_ID)
		};
	}
	
	public Page(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				config.getUrlTransform().getConfluenceToBasePath(),
				Arrays.asList(
					new MappingSetting(
						new CloudConfluencePages(),
						CloudConfluencePages.COL_DCID,
						CloudConfluencePages.COL_CLOUDID)
				),
				null,
				Arrays.asList(new ParamSetting(
					Page.class, PAGE_ID, CloudConfluencePages.class)
				));
	}
}
