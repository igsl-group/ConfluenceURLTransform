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
			"SELECT c.CONTENTID, c.VERSION, c.TITLE, s.SPACEKEY " + 
			"FROM CONTENT c " + 
			"JOIN " + 
			"(SELECT TITLE, VERSION, MAX(HIBERNATEVERSION) AS HIBERNATEVERSION FROM CONTENT GROUP BY TITLE, VERSION) v " + 
			"ON v.TITLE = c.TITLE AND v.HIBERNATEVERSION = c.HIBERNATEVERSION AND v.VERSION = c.VERSION " + 
            "LEFT JOIN CONTENT p ON p.CONTENTID = c.PREVVER " + 
			"JOIN SPACES s ON (s.SPACEID = c.SPACEID OR s.SPACEID = p.SPACEID) " + 
			"WHERE c.CONTENTTYPE = 'PAGE' ";
	public static final String COL_PAGEID = "PAGEID";
	public static final String COL_PAGEVERSION = "PAGEVERSION";
	public static final String COL_PAGENAME = "PAGENAME";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_PAGEID, COL_PAGEVERSION, COL_PAGENAME, COL_SPACEKEY);
	
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
			String pageVersion = rs.getString(2);
			String pageName = rs.getString(3);
			String spaceKey = rs.getString(4);
			return Arrays.asList(pageId, pageVersion, pageName, spaceKey);
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
		String pageVersion = r.get(COL_PAGEVERSION);
		String pageName = r.get(COL_PAGENAME);
		String spaceKey = r.get(COL_SPACEKEY);
		return ObjectData.createUniqueKey(spaceKey, pageName, pageVersion);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String pageId = r.get(COL_PAGEID);
		return pageId;
	}
}
