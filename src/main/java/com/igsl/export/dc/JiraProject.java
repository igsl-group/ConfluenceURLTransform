package com.igsl.export.dc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class JiraProject extends ObjectExport {

	private static final String SQL = "SELECT ID, PKEY, PNAME FROM PROJECT";
	public static final String COL_ID = "ID";
	public static final String COL_PROJECTKEY = "PROJECTKEY";
	public static final String COL_PROJECTNAME = "PROJECTNAME";
	public static final List<String> COL_LIST = Arrays.asList(COL_ID, COL_PROJECTKEY, COL_PROJECTNAME);
	
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
			String projectKey = rs.getString(2);
			String projectName = rs.getString(3);
			return Arrays.asList(id, projectKey, projectName);
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
		String projectKey = r.get(COL_PROJECTKEY);
		return projectKey;
	}

	@Override
	protected String getObjectId(CSVRecord r) throws Exception {
		String id = r.get(COL_ID);
		return id;
	}
}
