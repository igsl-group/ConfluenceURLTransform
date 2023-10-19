package com.igsl.handler.postmigrate.confluence;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudConfluenceCalendars;
import com.igsl.export.cloud.CloudConfluencePageTemplates;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class Calendar extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(Calendar.class);
	private static final String CALENDAR_ID = "calendarId";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/calendar/calendarPage.action").setQuery(CALENDAR_ID)
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
				null,
				Arrays.asList(new ParamSetting(
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
