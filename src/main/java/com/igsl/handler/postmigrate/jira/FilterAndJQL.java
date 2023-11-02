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

public class FilterAndJQL extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(FilterAndJQL.class);
	private static final String FILTER = "filter";
	private static final String JQL = "jql";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern().setPath("/issues").setQuery("filter", "jql"),
			new URLPattern().setPath("/browse").setQuery("filter", "jql"),
			new URLPattern().setPath("/secure/IssueNavigator.jspa").setQuery("filter", "jql"),
			new URLPattern().setPathRegex("/projects/[^#?]*").setQuery("filter", "jql"),
		};
	}
	
	// TODO Fix JQL here or in URLTransform?
	
	public FilterAndJQL(Config config) {
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
					new ParamSetting(FilterAndJQL.class, FILTER, CloudJiraFilters.class)
				));
	}
}
