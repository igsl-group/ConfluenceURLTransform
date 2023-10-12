package com.igsl.export.dc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;

public class ConfluenceCalendar extends ObjectExport {

	private static final String SQL = "SELECT ID, NAME, SPACE_KEY FROM AO_950DC3_TC_SUBCALS";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_NAME, COL_SPACEKEY);
	
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
		if (rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			String spaceKey = rs.getString(3);
			return Arrays.asList(id, name, spaceKey);
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
