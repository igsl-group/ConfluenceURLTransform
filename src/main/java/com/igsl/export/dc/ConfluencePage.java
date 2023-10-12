package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;

public class ConfluencePage extends ObjectExport {

	private static final String SQL = 
			"SELECT c.CONTENTID, c.TITLE, s.SPACEKEY " + 
			"FROM CONTENT c " + 
			"JOIN SPACES s ON s.SPACEID = c.SPACEID " + 
			"WHERE c.CONTENTTYPE = 'PAGE' AND PREVVER IS NULL AND CONTENT_STATUS = 'current'";
	public static final String COL_PAGEID = "PAGEID";
	public static final String COL_PAGENAME = "PAGENAME";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(COL_PAGEID, COL_PAGENAME, COL_SPACEKEY);
	
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
		String pageName = r.get(COL_PAGENAME);
		String spaceKey = r.get(COL_SPACEKEY);
		return ObjectData.createUniqueKey(spaceKey, pageName);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String pageId = r.get(COL_PAGEID);
		return pageId;
	}
}
