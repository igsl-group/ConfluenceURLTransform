package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.igsl.config.SQLConfig;

public class ConfluenceUser extends ObjectExport {

	private static final String SQL = SQLConfig.getInstance().getSQL(ConfluenceUser.class);
//	private static final String SQL = "SELECT ID, USER_NAME, DISPLAY_NAME FROM CWD_USER";
	public static final String COL_ID = "ID";
	public static final String COL_USERNAME = "USERNAME";
	public static final String COL_DISPLAYNAME = "DISPLAYNAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_USERNAME, COL_DISPLAYNAME);
	
	private PreparedStatement ps;
	private ResultSet rs;
	
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
			String id = rs.getString(1);
			String name = rs.getString(2);
			String displayName = rs.getString(3);
			return Arrays.asList(id, name, displayName);
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
		String displayName = r.get(COL_DISPLAYNAME);
		return displayName;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_USERNAME);
		return id;
	}
}
