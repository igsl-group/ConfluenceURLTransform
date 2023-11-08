package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;
import com.igsl.config.SQLConfig;

public class JiraAttachment extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(JiraAttachment.class);
//			"SELECT " + 
//			"a.ID, a.ISSUEID, CONCAT(p.PKEY, '-', i.ISSUENUM) AS ISSUEKEY, " + 
//			"a.MIMETYPE, a.FILENAME, " + 
//			"a.AUTHOR, cu.ID, cu.DISPLAY_NAME, cu.EMAIL_ADDRESS " + 
//			"FROM FILEATTACHMENT a " + 
//			"JOIN JIRAISSUE i ON i.ID = a.ISSUEID " + 
//			"JOIN PROJECT p ON p.ID = i.PROJECT " + 
//			"JOIN APP_USER au ON au.USER_KEY = a.AUTHOR " + 
//			"JOIN CWD_USER cu ON cu.ID = au.ID";
	public static final String COL_ID = "ID";
	public static final String COL_ISSUEID = "ISSUEID";
	public static final String COL_ISSUEKEY = "ISSUEKEY";
	public static final String COL_MIMETYPE = "MIMETYPE";
	public static final String COL_FILENAME = "FILENAME";
	public static final String COL_AUTHOR = "AUTHOR";
	public static final String COL_AUTHORID = "AUTHOR_ID";
	public static final String COL_AUTHORDISPLAYNAME = "AUTHOR_DISPLAYNAME";
	public static final String COL_AUTHOREMAIL = "AUTHOR_EMAIL";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_ISSUEID, COL_ISSUEKEY, COL_MIMETYPE, COL_FILENAME, 
			COL_AUTHOR, COL_AUTHORID, COL_AUTHORDISPLAYNAME, COL_AUTHOREMAIL);
	
	private PreparedStatement ps;
	private ResultSet rs;
	
	@Override
	public List<String> getHeaders() {
		return COL_LIST;
	}

	@Override
	public void startGetObjects() throws Exception {
		Connection conn = config.getConnections().getJiraConnection();
		ps = conn.prepareStatement(SQL);
		rs = ps.executeQuery();
	}

	@Override
	public List<String> getNextObject() throws Exception {
		if (rs.next()) {
			String id = rs.getString(1);
			String issueId = rs.getString(2);
			String issueKey = rs.getString(3);
			String mimeType = rs.getString(4);
			String fileName = rs.getString(5);
			String author = rs.getString(6);
			String authorId = rs.getString(7);
			String authorDisplayName = rs.getString(8);
			String authorEmail = rs.getString(9);
			return Arrays.asList(
					id, issueId, issueKey, 
					mimeType, fileName, 
					author, authorId, authorDisplayName, authorEmail);
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
		String issueKey = r.get(COL_ISSUEKEY);
		String fileName = r.get(COL_FILENAME);
		return ObjectData.createUniqueKey(issueKey, fileName);
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
