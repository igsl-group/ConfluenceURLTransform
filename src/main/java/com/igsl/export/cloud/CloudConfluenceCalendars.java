package com.igsl.export.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.ObjectData;
import com.igsl.config.Config;
import com.igsl.export.cloud.model.ConfluenceCalendar;
import com.igsl.export.cloud.model.ConfluenceCalendars;
import com.igsl.export.cloud.model.ConfluenceSubCalendar;
import com.igsl.export.dc.ObjectExport;

/**
 * Note:
 * The user account used must have 0 calendars. The REST API excludes calendars already added by the user.
 *
 */
public class CloudConfluenceCalendars extends BaseExport<ConfluenceCalendars> {

	private static final Logger LOGGER = LogManager.getLogger(CloudConfluenceCalendars.class);
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final String COL_SPACENAME = "SPACENAME";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_NAME, COL_DESCRIPTION, COL_SPACEKEY, COL_SPACENAME);
	
	public CloudConfluenceCalendars() {
		super(ConfluenceCalendars.class);
	}
	
	@Override
	public String getLimitParameter() {
		return "pageSize";
	}

	@Override
	public String getStartAtParameter() {
		return "startIndex";
	}

	@Override
	protected List<String> getCSVHeaders() {
		return COL_LIST;
	}

	@Override
	protected List<ObjectData> getCSVRows(ConfluenceCalendars obj) {
		List<ObjectData> result = new ArrayList<>();
		if (obj.getPayload() != null) {
			for (ConfluenceCalendar page : obj.getPayload()) {
				ConfluenceSubCalendar cal = page.getSubCalendar();
				List<String> list = Arrays.asList(
						cal.getId(), cal.getName(), cal.getDescription(), cal.getSpaceKey(), cal.getSpaceName());
				String uniqueKey = ObjectData.createUniqueKey(cal.getSpaceKey(), cal.getName(), cal.getDescription());
				result.add(new ObjectData(cal.getId(), uniqueKey, list));
			}
		}
		return result;
	}

	@Override
	public List<ConfluenceCalendars> getObjects(Config config) throws Exception {
		MultivaluedMap<String, Object> header = getAuthenticationHeader(config);
		Map<String, Object> query = new HashMap<>();
		query.put("term", "*");
		List<ConfluenceCalendars> result = invokeRest(
				config, "/wiki/rest/calendar-services/1.0/calendar/search/subcalendars.json", 
				HttpMethod.GET, header, query, null);
		return result;
	}

	@Override
	protected ObjectExport getObjectExport() {
		return new com.igsl.export.dc.ConfluenceCalendar();
	}
}
