package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;

public class ConfluenceAttachment extends ObjectExport {

	private static final String SQL = 
			"SELECT c.CONTENTID, c.VERSION, c.TITLE, " + 
			"p.VERSION AS PAGE_VERSION, p.TITLE AS PAGE_TITLE, " + 
			"s.SPACEKEY " + 
			"FROM CONTENT c " + 
			"JOIN CONTENT p ON p.CONTENTID = c.PAGEID AND p.CONTENTTYPE = 'PAGE' AND p.CONTENT_STATUS = 'current' " + 
			"JOIN SPACES s ON s.SPACEID = c.SPACEID " + 
			"WHERE c.CONTENTTYPE = 'ATTACHMENT' AND c.CONTENT_STATUS = 'current'";
	public static final String COL_ATTACHMENT_ID = "ATTACHMENTID";
	public static final String COL_ATTACHMENT_VERSION = "ATTACHMENTVERSION";
	public static final String COL_ATTACHMENT_NAME = "ATTACHMENTNAME";
	public static final String COL_PAGE_VERSION = "PAGE_VERSION";
	public static final String COL_PAGENAME = "PAGENAME";
	public static final String COL_SPACEKEY = "SPACEKEY";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ATTACHMENT_ID, COL_ATTACHMENT_VERSION, COL_ATTACHMENT_NAME, COL_PAGE_VERSION, COL_PAGENAME, COL_SPACEKEY);
	
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
			String attachmentId = rs.getString(1);
			String attachmentVersion = rs.getString(2);
			String attachmentName = rs.getString(3);
			String pageVersion = rs.getString(4);
			String pageName = rs.getString(5);
			String spaceKey = rs.getString(6);
			return Arrays.asList(
					attachmentId, attachmentVersion, attachmentName, 
					pageVersion, pageName, 
					spaceKey);
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
		String attachmentVersion = r.get(COL_ATTACHMENT_VERSION);
		String attachmentName = r.get(COL_ATTACHMENT_NAME);
		String pageVersion = r.get(COL_PAGE_VERSION);
		String pageName = r.get(COL_PAGENAME);
		String spaceKey = r.get(COL_SPACEKEY);
		return ObjectData.createUniqueKey(spaceKey, pageName, pageVersion, attachmentName, attachmentVersion);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String pageId = r.get(COL_ATTACHMENT_ID);
		return pageId;
	}
}
