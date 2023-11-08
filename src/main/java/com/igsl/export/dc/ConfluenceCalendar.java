package com.igsl.export.dc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpStatus;

import com.igsl.ObjectData;
import com.igsl.config.SQLConfig;
import com.igsl.rest.RESTUtil;

public class ConfluenceCalendar extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(ConfluenceCalendar.class);
	//private static final String SQL = "SELECT ID, NAME, DESCRIPTION, SPACE_KEY FROM AO_950DC3_TC_SUBCALS";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_DESCRIPTION = "DESCRIPTION";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final String COL_ICAL = "ICAL";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_NAME, COL_DESCRIPTION, COL_SPACEKEY, COL_ICAL);
	
	private PreparedStatement ps = null;
	private ResultSet rs = null;

	@Override
	public List<String> getHeaders() {
		return COL_LIST;
	}

	@Override
	public void startGetObjects() throws Exception {
		ps = config.getConnections().getConfluenceConnection().prepareStatement(SQL);
		rs = ps.executeQuery();
	}

	@Override
	public List<String> getNextObject() throws Exception {
		MultivaluedMap<String, Object> authHeader = RESTUtil.getDCConfluenceAuthenticationHeader(config);
		if (rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			String desc = rs.getString(3);
			String spaceKey = rs.getString(4);
			String content = null;
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("os_authType", "basic");
			queryParameters.put("isSubscribe", false);
			Response resp = RESTUtil.webRequest(
					config, 
					config.getDcExport().getConfluenceScheme(), 
					config.getDcExport().getConfluenceHost(),
					"/rest/calendar-services/1.0/calendar/export/subcalendar/" + id + ".ics", 
					HttpMethod.GET,
					null,
					authHeader,
					queryParameters,
					null,
					HttpStatus.SC_OK);
			try (	InputStream in = resp.readEntity(InputStream.class);
					ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				int c = -1;
				while ((c = in.read()) != -1) {
					baos.write(0xFF & c);
				}
				content = Base64.getEncoder().encodeToString(baos.toByteArray());
			}
			return Arrays.asList(id, name, desc, spaceKey, content);
		}
		return null;
	}

	@Override
	public void stopGetObjects() throws Exception {
		close(rs);
		close(ps);
	}

	@Override
	protected String getObjectKey(CSVRecord r) throws Exception {
		String name = r.get(COL_NAME);
		String spaceKey = r.get(COL_SPACEKEY);
		return ObjectData.createUniqueKey(spaceKey, name);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		return r.get(COL_ID);
	}
}
