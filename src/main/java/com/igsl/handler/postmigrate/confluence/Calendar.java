package com.igsl.handler.postmigrate.confluence;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.Log;
import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluenceCalendars;
import com.igsl.export.cloud.CloudConfluencePageTemplates;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;
import com.igsl.handler.postmigrate.PathSetting;

public class Calendar extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Calendar.class);
	private static final String CALENDAR_ID = "calendarId";
	private static final String CALENDAR_NAME = "calendarName";
	
	@Override
	protected URLPattern[] getPatterns() {
		String basePath = config.getUrlTransform().getConfluenceToBasePath();
		return new URLPattern[] {
			new URLPattern()
				.setPath(basePath + "/calendar/calendarPage.action")
				.setQuery(CALENDAR_ID),
			new URLPattern().setPathRegex(Pattern.quote(basePath) + "/display/[^/]+/calendar/[^?]+/?"),
		};
	}
	
	public Calendar(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceToHost(),
				Arrays.asList(
					new MappingSetting(
							new CloudConfluenceCalendars(), 
							CloudConfluenceCalendars.COL_DCID, 
							CloudConfluenceCalendars.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							Calendar.class, 
							Pattern.compile(
									Pattern.quote(config.getUrlTransform().getConfluenceToBasePath()) + 
									"/display/([^/]+)/calendar/([^?]+)"),
							CloudConfluenceCalendars.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) throws Exception {
							Map<String, String> mapping = mappings.get(this.getBaseExport().getCanonicalName());
							String calendar = m.group(2);
							if (mapping.containsKey(calendar)) {
								return config.getUrlTransform().getConfluenceToBasePath() + 
										"/display/$1/calendar/" + mapping.get(calendar);
							} else {
								throw new Exception(getPostMigrate().getCanonicalName() + 
										" Mapping not found for calendar: " + calendar);
							}
						}
					}
				),
				Arrays.asList(
					new ParamSetting(
						Calendar.class,
						CALENDAR_ID, null,
						CloudConfluenceCalendars.class)
				));
	}
}
