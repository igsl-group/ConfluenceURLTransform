package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.ObjectData;

public class JiraUser extends ObjectExport {

	private static final String SQL = 
			"SELECT ID, USER_NAME, ACTIVE, DISPLAY_NAME, EMAIL_ADDRESS FROM CWD_USER";
	public static final String COL_ID = "ID";
	public static final String COL_USER_NAME = "USER_NAME";
	public static final String COL_DISPLAY_NAME = "DISPLAY_NAME";
	public static final String COL_EMAIL_ADDRESS = "EMAIL_ADDRESS";
	public static final String COL_ACTIVE = "ACTIVE";
	public static final List<String> COL_LIST = Arrays.asList(
			COL_ID, COL_USER_NAME, COL_DISPLAY_NAME, COL_EMAIL_ADDRESS, COL_ACTIVE);
	
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
			String userName = rs.getString(2);
			String active = rs.getString(3);
			String displayName = rs.getString(4);
			String emailAddress = rs.getString(5);
			return Arrays.asList(id, userName, displayName, emailAddress, active);
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
		String name = r.get(COL_DISPLAY_NAME);
		return name;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
