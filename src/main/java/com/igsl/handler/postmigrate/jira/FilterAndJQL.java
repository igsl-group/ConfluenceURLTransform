package com.igsl.handler.postmigrate.jira;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraFilters;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class FilterAndJQL extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(FilterAndJQL.class);
	private static final String FILTER = "filter";
	private static final String JQL = "jql";
	
	@Override
	protected URLPattern[] getPatterns() {
		String basePath = config.getUrlTransform().getJiraToBasePath();
		return new URLPattern[] {
			new URLPattern()
				.setPath(basePath + "/issues")
				.setQuery(FILTER, JQL),
			new URLPattern()
				.setPathRegex(Pattern.quote(basePath) + "/browse/[^/]+")
				.setQuery(FILTER, JQL),
			new URLPattern()
				.setPath(basePath + "/secure/IssueNavigator.jspa")
				.setQuery(FILTER, JQL),
			new URLPattern()
				.setPathRegex(Pattern.quote(basePath) + "/projects/[^#?]*")
				.setQuery(FILTER, JQL),
		};
	}
	
	public FilterAndJQL(Config config) {
		super(	config, 
				config.getUrlTransform().getJiraToHost(),
				Arrays.asList(
					new MappingSetting(
						new CloudJiraFilters(), 
						CloudJiraFilters.COL_DCID, 
						CloudJiraFilters.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							FilterAndJQL.class, 
							Pattern.compile(
									"(" + 
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/issues)"),
							CloudJiraFilters.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							// Issues link does not start with /jira
							return "issues";
						}
					},
					new PathSetting(
							FilterAndJQL.class, 
							Pattern.compile(
									"(" + 
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/browse/([^/]+))"),
							CloudJiraFilters.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							// Browse link does not start with /jira
							return "/browse/$1";
						}
					},
					new PathSetting(
							FilterAndJQL.class, 
							Pattern.compile(
									"(" + 
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/secure/IssueNavigator.jspa)"),
							CloudJiraFilters.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							// Browse link does not start with /jira
							return "/issues";
						}
					},
					new PathSetting(
							FilterAndJQL.class, 
							Pattern.compile(
									"(" + 
									Pattern.quote(config.getUrlTransform().getJiraToBasePath()) + 
									"/projects/([^#?]*))"),
							CloudJiraFilters.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							// Browse link does not start with /jira
							return "/browse/$1";
						}
					}
				),
				Arrays.asList(
					new ParamSetting(FilterAndJQL.class, FILTER, null, CloudJiraFilters.class)
				));
	}
}
