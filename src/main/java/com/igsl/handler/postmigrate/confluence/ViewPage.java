package com.igsl.handler.postmigrate.confluence;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluencePages;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class ViewPage extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(ViewPage.class);
	private static final String PAGE_ID = "pageId";
	
	@Override
	public boolean needPostMigrate() {
		return true;
	}

	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPathRegex(
						Pattern.quote(config.getUrlTransform().getConfluenceToBasePath()) + 
						"/pages/viewpage.action")
				.setQuery(PAGE_ID)
		};
	}
	
	public ViewPage(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudConfluencePages(),
						CloudConfluencePages.COL_DCID,
						CloudConfluencePages.COL_CLOUDID)
				),
				null,
				Arrays.asList(new ParamSetting(
					ViewPage.class, PAGE_ID, null, CloudConfluencePages.class)
				));
	}
}
