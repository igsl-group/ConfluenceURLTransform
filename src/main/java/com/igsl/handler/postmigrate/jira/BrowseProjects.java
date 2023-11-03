package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraProjectCategories;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;
import com.igsl.handler.postmigrate.PathSetting;
import com.mysql.cj.x.protobuf.MysqlxCrud.Projection;

public class BrowseProjects extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(BrowseProjects.class);
	private static final String PROJECT_CATEGORY = "selectedCategory";
	private static final String PROJECT_CATEGORY_ID = "projectCategoryId";
	
	@Override
	protected URLPattern[] getPatterns() {
		return new URLPattern[] {
			new URLPattern()
				.setPath(config.getUrlTransform().getJiraToBasePath() + "/secure/BrowseProjects.jspa")
				.setQuery("selectedCategory"),
		};
	}
	
	public BrowseProjects(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraProjectCategories(), 
						CloudJiraProjectCategories.COL_DCID, 
						CloudJiraProjectCategories.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							BrowseProjects.class,
							Pattern.compile(Pattern.quote(
									config.getUrlTransform().getJiraToBasePath() + "/secure/BrowseProjects.jspa")),
							CloudJiraProjectCategories.class
						) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							return "/projects";
						}
					}
				),
				Arrays.asList(
					new ParamSetting(BrowseProjects.class, 
							PROJECT_CATEGORY, PROJECT_CATEGORY_ID, 
							CloudJiraProjectCategories.class)
				));
	}
}
