package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
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
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/calendar/calendarPage.action").setQuery(CALENDAR_ID),
		new URLPattern().setPath("/display/UTS/calendar/").setQuery(CALENDAR_NAME),
	};
	
	public Calendar(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(),
				Arrays.asList(
					new MappingSetting(
							new CloudConfluenceCalendars(), 
							CloudConfluenceCalendars.COL_DCID, 
							CloudConfluenceCalendars.COL_CLOUDID)
				),
				Arrays.asList(
					new PathSetting(
							Calendar.class, 
							Pattern.compile("/display/(.+)/calendar/([^?]+)"),
							CloudConfluenceCalendars.class) {
						@Override
						public String getReplacement(Matcher m, Map<String, Map<String, String>> mappings) {
							Map<String, String> mapping = mappings.get(this.getBaseExport().getCanonicalName());
							String calendar = m.group(2);
							if (mapping.containsKey(calendar)) {
								return "/display/$1/calendar/" + mapping.get(calendar);
							} else {
								Log.warn(LOGGER, 
										getPostMigrate().getCanonicalName() + 
										" Mapping not found for calendar: " + calendar);
								return m.group(0);
							}
						}
					}
				),
				Arrays.asList(
					new ParamSetting(
						Calendar.class,
						CALENDAR_ID, 
						CloudConfluencePageTemplates.class)
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
