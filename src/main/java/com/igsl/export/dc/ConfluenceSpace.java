package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

public class ConfluenceSpace extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(ConfluenceSpace.class);
//	private static final String SQL = "SELECT SPACEID, SPACENAME, SPACEKEY FROM SPACES";
	public static final String COL_SPACEID = "SPACEID";
	public static final String COL_SPACENAME = "SPACENAME";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(COL_SPACEID, COL_SPACENAME, COL_SPACEKEY);
	
	private ResultSet rs;
	private PreparedStatement ps;
	
	@Override
	public List<String> getHeaders() {
		return COL_LIST;
	}

	@Override
	public void startGetObjects() throws Exception {
		Connection conn = config.getConnections().getConfluenceConnection();
		ps = conn.prepareStatement(SQL);
		rs = ps.executeQuery();
	}

	@Override
	public List<String> getNextObject() throws Exception {
		if (rs.next()) {
			String pageId = rs.getString(1);
			String pageName = rs.getString(2);
			String spaceKey = rs.getString(3);
			return Arrays.asList(pageId, pageName, spaceKey);
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
		String spaceKey = r.get(COL_SPACEKEY);
		return spaceKey;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String pageId = r.get(COL_SPACEID);
		return pageId;
	}
}
